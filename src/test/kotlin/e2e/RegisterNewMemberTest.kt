package bookstore.playground.e2e

import bookstore.playground.module
import io.kotest.matchers.should
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import io.ktor.server.testing.*
import kotlinx.coroutines.test.runTest
import net.javacrumbs.jsonunit.kotest.equalJson
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.TestInfo
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import org.testcontainers.utility.MountableFile
import java.io.File
import kotlin.test.BeforeTest
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

@Testcontainers
class RegisterNewMemberTest {

    private val postgresUser = "postgres"

    private val postgresPassword = "postgres"

    @Container
    val postgres = GenericContainer(DockerImageName.parse("postgres:17.4-bookworm"))
        .withExposedPorts(5432)
        .withEnv("POSTGRES_USER", postgresUser)
        .withEnv("POSTGRES_PASSWORD", postgresPassword)
        .withEnv("POSTGRES_DB", "member-manager")
        .withCopyFileToContainer(MountableFile.forClasspathResource("database/01-schema.sql"), "/docker-entrypoint-initdb.d/01-schema.sql")
        .waitingFor(Wait.forLogMessage(".*database system is ready to accept connections.*\\s", 2))!!

    @BeforeTest
    fun setUp(testInfo: TestInfo) {
        Database.connect(
            url = "jdbc:postgresql://${postgres.host}:${postgres.firstMappedPort}/member-manager",
            user = postgresUser,
            password = postgresPassword,
            driver = "org.postgresql.Driver",
        )

        transaction {
            exec("TRUNCATE TABLE member")
        }

        val testClassResourceDir = testInfo.testClass.map { it.name }.map { it.replace("bookstore.playground", "").replace(".", "/").substring(1) }
            .orElseThrow { IllegalArgumentException("Test class not found") }

        val testMethodResourceDir = testInfo.testMethod.map { it.name }
            .orElseThrow { IllegalArgumentException("Test method not found") }

        val testResourceDir = "$testClassResourceDir/$testMethodResourceDir"
        println("test resource dir: $testResourceDir")

        File("src/test/resources/$testResourceDir").listFiles()?.forEach { file ->
            println("test resource file: ${file.name}")
            transaction {
                exec(file.readText())
            }
        }
    }

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

}
