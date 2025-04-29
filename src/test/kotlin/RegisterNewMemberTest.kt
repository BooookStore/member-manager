package bookstore.playground

import io.kotest.matchers.should
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import io.ktor.server.testing.*
import net.javacrumbs.jsonunit.kotest.equalJson
import kotlin.test.Test
import kotlin.test.assertEquals

fun testApplicationWithCommonSetup(block: suspend ApplicationTestBuilder.(client: HttpClient) -> Unit) =
    testApplication {
        application {
            module()
        }

        val client = createClient {
            install(ContentNegotiation) {
                jackson()
            }
        }

        block(client)
    }

class RegisterNewMemberTest {

    @Test
    fun testRoot() = testApplicationWithCommonSetup { client ->
        client.post("/members") {
            contentType(ContentType.Application.Json)
            setBody("""
                {
                    "name": "John Doe",
                    "emailAddress": "john.done@example.com"
                }
            """.trimIndent())
        }.apply {
            assertEquals(HttpStatusCode.OK, status)
        }
    }

    @Test
    fun invalidEmailAddressReturnedBadRequest() = testApplicationWithCommonSetup { client ->
        client.post("/members") {
            contentType(ContentType.Application.Json)
            setBody("""
                {
                    "name": "John Doe",
                    "emailAddress": "invalid-email"
                }
            """.trimIndent())
        }.apply {
            assertEquals(HttpStatusCode.BadRequest, status)
            """
                {
                    "messages": [
                        "invalid email address [invalid-email] not contain '@' symbol",
                        "invalid email address [invalid-email] not match domain [example.com]"
                    ]
                }
            """ should equalJson(bodyAsText())
        }
    }

}
