package bookstore.playground.domain

import arrow.core.Either.Left
import arrow.core.Either.Right
import arrow.core.nonEmptyListOf
import io.kotest.matchers.shouldBe
import kotlin.test.Test
import kotlin.test.fail

class EmailAddressTest {

    @Test
    fun emailAddressFromUnvalidatedEmailAddress() {
        val emailAddress = EmailAddress.create("john.done@example.com")

        when (emailAddress) {
            is Left -> fail("Expected a Either.Right, but got Either.Left")
            is Right -> emailAddress.value.rawEmailAddress shouldBe "john.done@example.com"
        }
    }

    @Test
    fun invalidEmailAddressFromBlank() {
        val emailAddress = EmailAddress.create("")

        when (emailAddress) {
            is Left -> emailAddress.value shouldBe nonEmptyListOf(InvalidEmailAddress.Blank, InvalidEmailAddress.UnexpectedDomain)
            is Right -> fail("Expected a Either.Left, but got Either.Right")
        }
    }

    @Test
    fun invalidEmailAddressFromNotExampleComDomain() {
        val emailAddress = EmailAddress.create("john.done@xample.com")

        when (emailAddress) {
            is Left -> emailAddress.value shouldBe nonEmptyListOf(InvalidEmailAddress.UnexpectedDomain)
            is Right -> fail("Expected a Either.Left, but got Either.Right")
        }
    }

    @Test
    fun invalidEmailAddressFromEmptyAtSymbol() {
        val emailAddress = EmailAddress.create("john.done")

        when (emailAddress) {
            is Left -> emailAddress.value shouldBe nonEmptyListOf(InvalidEmailAddress.UnexpectedDomain)
            is Right -> fail("Expected a Either.Left, but got Either.Right")
        }
    }

}