package e2e

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.TestInfo
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.utility.DockerImageName
import org.testcontainers.utility.MountableFile
import java.io.File
import kotlin.test.BeforeTest

open class E2ETestBase {

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
            println("test resource file: ${file.name}")
            loadFileToDatabase(file)
        }
    }

    private fun loadFileToDatabase(file: File) = file.bufferedReader().use { reader ->
        println("test resource content: ${reader.readText()}")
        transaction { exec(reader.readText()) }
    }

    private fun truncateDatabase() = transaction {
        exec("TRUNCATE TABLE member")
    }

    private fun connectToDatabase() {
        Database.connect(
            url = "jdbc:postgresql://${postgres.host}:${postgres.firstMappedPort}/member-manager",
            user = postgresUser,
            password = postgresPassword,
            driver = "org.postgresql.Driver",
        )
    }

}