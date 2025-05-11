package domain.validator

import arrow.core.Either.Left
import arrow.core.Either.Right
import arrow.core.nonEmptyListOf
import bookstore.playground.domain.EmailAddress
import bookstore.playground.domain.InvalidEmailAddress
import bookstore.playground.domain.UnvalidatedEmailAddress
import io.kotest.matchers.shouldBe
import kotlin.test.Test
import kotlin.test.fail

class EmailAddressTest {

    @Test
    fun emailAddressFromUnvalidatedEmailAddress() {
        val emailAddress = EmailAddress.create(UnvalidatedEmailAddress("john.done@example.com"))

        when (emailAddress) {
            is Left -> fail("Expected a Either.Right, but got Either.Left")
            is Right -> emailAddress.value.rawEmailAddress shouldBe "john.done@example.com"
        }
    }

    @Test
    fun invalidEmailAddressFromBlank() {
        val emailAddress = EmailAddress.create(UnvalidatedEmailAddress(""))

        when (emailAddress) {
            is Left -> emailAddress.value shouldBe nonEmptyListOf(InvalidEmailAddress.Blank, InvalidEmailAddress.UnexpectedDomain)
            is Right -> fail("Expected a Either.Left, but got Either.Right")
        }
    }

    @Test
    fun invalidEmailAddressFromNotExampleComDomain() {
        val emailAddress = EmailAddress.create(UnvalidatedEmailAddress("john.done@xample.com"))

        when (emailAddress) {
            is Left -> emailAddress.value shouldBe nonEmptyListOf(InvalidEmailAddress.UnexpectedDomain)
            is Right -> fail("Expected a Either.Left, but got Either.Right")
        }
    }

    @Test
    fun invalidEmailAddressFromEmptyAtSymbol() {
        val emailAddress = EmailAddress.create(UnvalidatedEmailAddress("john.done"))

        when (emailAddress) {
            is Left -> emailAddress.value shouldBe nonEmptyListOf(InvalidEmailAddress.UnexpectedDomain)
            is Right -> fail("Expected a Either.Left, but got Either.Right")
        }
    }

}