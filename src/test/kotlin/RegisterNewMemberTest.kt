package bookstore.playground

import bookstore.playground.handler.member.ReceiveNewMember
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals

class RegisterNewMemberTest {

    @Test
    fun testRoot() = testApplication {
        application {
            module()
        }

        val client = createClient {
            install(ContentNegotiation) {
                jackson()
            }
        }

        client.post("/members") {
            contentType(ContentType.Application.Json)
            setBody(ReceiveNewMember("0001", "John Doe"))
        }.apply {
            assertEquals(HttpStatusCode.OK, status)
        }
    }

}
