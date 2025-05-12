package bookstore.playground.e2e

import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.jackson.*
import io.ktor.server.config.*
import io.ktor.server.testing.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.TestInfo
import java.io.File
import kotlin.test.BeforeTest

open class E2ETestBase {

    private val postgresUser = "member-manager"

    private val postgresPassword = "dev-password"

    @BeforeTest
    fun setUp(testInfo: TestInfo) {
        connectToDatabase()
        truncateDatabase()
        loadDataToDatabase(testInfo)
    }

    private fun loadDataToDatabase(testInfo: TestInfo) {
        val testClassResourceDir = testInfo.testClass.map { it.name }
            .map { it.replace("bookstore.playground", "").replace(".", "/").substring(1) }
            .orElseThrow { IllegalArgumentException("Test class not found") }

        val testMethodResourceDir = testInfo.testMethod.map { it.name }
            .orElseThrow { IllegalArgumentException("Test method not found") }

        val testResourceDir = "$testClassResourceDir/$testMethodResourceDir"
        println("test resource dir: $testResourceDir")

        File("src/test/resources/$testResourceDir").listFiles()?.forEach { file ->
            println("test resource file found: ${file.name}")
            loadFileToDatabase(file)
        }
    }

    private fun loadFileToDatabase(file: File) = file.bufferedReader().use { reader ->
        val sql = reader.readText()
        println("===load to database===")
        println(sql)
        println("======================")
        transaction { exec(sql) }
    }

    private fun truncateDatabase() = transaction {
        exec("TRUNCATE TABLE member")
    }

    private fun connectToDatabase() {
        Database.connect(
            url = "jdbc:postgresql://localhost/member-manager",
            user = postgresUser,
            password = postgresPassword,
            driver = "org.postgresql.Driver",
        )
    }

    fun testApplicationWithCommonSetup(block: suspend ApplicationTestBuilder.(client: HttpClient) -> Unit) =
        testApplication {
            environment {
                config = ApplicationConfig("application.yaml")
            }

            val client = createClient {
                install(ContentNegotiation) {
                    jackson()
                }
            }

            block(client)
        }


}