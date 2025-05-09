package e2e

import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.test.runTest
import net.javacrumbs.jsonunit.kotest.equalJson
import org.jetbrains.exposed.sql.transactions.transaction
import org.testcontainers.junit.jupiter.Testcontainers
import kotlin.test.Test
import kotlin.test.assertEquals

@Testcontainers
class RegisterNewMemberTest : E2ETestBase() {

    @Test
    fun testContainerIsRunning() = runTest {
        transaction {
            exec("SELECT * FROM member") { result ->
                while (result.next()) {
                    println("member name: ${result.getString("name")}")
                    assertEquals("John Doe", result.getString("name"))
                    println("member email address: ${result.getString("email_address")}")
                    assertEquals("john.doe@example.com", result.getString("email_address"))
                }
            }
        }
    }

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
    }

    // todo: already existing member testcase

}
