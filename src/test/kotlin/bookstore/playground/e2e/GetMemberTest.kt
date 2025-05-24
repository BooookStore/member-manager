package bookstore.playground.e2e

import io.kotest.matchers.shouldBe
import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import kotlin.test.Test

class GetMemberTest : E2ETestBase() {

    @Test
    fun getMember() = testApplicationWithCommonSetup { client ->
        client.get("/members/1bfafdec-16e5-4ff8-a8ab-146e0e6bb215")
        .apply {
            status shouldBe HttpStatusCode.OK
            bodyAsText() shouldBe "1bfafdec-16e5-4ff8-a8ab-146e0e6bb215"
        }
    }

}