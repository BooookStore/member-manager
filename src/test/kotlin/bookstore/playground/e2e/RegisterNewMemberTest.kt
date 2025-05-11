package bookstore.playground.e2e

import io.kotest.matchers.should
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import net.javacrumbs.jsonunit.kotest.equalJson
import org.jetbrains.exposed.sql.transactions.transaction
import org.testcontainers.junit.jupiter.Testcontainers
import kotlin.test.Test
import kotlin.test.assertEquals

@Testcontainers
class RegisterNewMemberTest : E2ETestBase() {

    @Test
    fun validMemberReturnedCreated() = testApplicationWithCommonSetup { client ->
        client.post("/members") {
            contentType(ContentType.Application.Json)
            setBody("""
                {
                    "name": "John Doe",
                    "emailAddress": "john.done@example.com"
                }
            """.trimIndent())
        }.apply {
            assertEquals(HttpStatusCode.Created, status)
        }

        transaction {
            exec("SELECT COUNT(*) FROM member") { result ->
                result.next()
                assertEquals(1, result.getInt(1), "Expected 1 member to be inserted")
            }
            exec("SELECT * FROM member") { result ->
                result.next()
                assertEquals("John Doe", result.getString("name"))
                assertEquals("john.done@example.com", result.getString("email_address"))
            }
        }
    }

    @Test
    fun invalidMemberReturnedBadRequest() = testApplicationWithCommonSetup { client ->
        client.post("/members") {
            contentType(ContentType.Application.Json)
            setBody("""
                {
                    "name": "",
                    "emailAddress": ""
                }
            """.trimIndent())
        }.apply {
            assertEquals(HttpStatusCode.BadRequest, status)
            """
                {
                    "messages": [
                        "invalid name '' is blank",
                        "invalid email address '' is blank",
                        "invalid email address '' not match domain 'example.com'"
                    ]
                }
            """ should equalJson(bodyAsText())
        }

        transaction {
            exec("SELECT COUNT(*) FROM member") { result ->
                result.next()
                assertEquals(0, result.getInt(1), "Expected 0 members to be inserted")
            }
        }
    }

    @Test
    fun alreadyExistingMemberReturnedBadRequest() = testApplicationWithCommonSetup { client ->
        client.post("/members") {
            contentType(ContentType.Application.Json)
            setBody("""
                {
                    "name": "John Doe",
                    "emailAddress": "john.done@example.com"
                }
            """.trimIndent())
        }.apply {
            assertEquals(HttpStatusCode.BadRequest, status)
            """
                {
                    "messages": [
                        "already existing member with email address 'john.done@example.com'"
                    ]
                }
            """ should equalJson(bodyAsText())
        }
    }

}
