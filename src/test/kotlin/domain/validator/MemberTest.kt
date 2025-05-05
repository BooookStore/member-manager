package bookstore.playground.domain.validator

import arrow.core.Either.Left
import arrow.core.Either.Right
import arrow.core.nonEmptyListOf
import bookstore.playground.domain.EmailAddress
import bookstore.playground.domain.InvalidEmailAddress.Blank
import bookstore.playground.domain.InvalidEmailAddress.InvalidDomain
import bookstore.playground.domain.UnvalidatedEmailAddress
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Nested
import kotlin.test.Test
import kotlin.test.fail

class MemberTest {

    @Nested
    inner class EmailAddressTest {

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
                is Left -> emailAddress.value shouldBe nonEmptyListOf(Blank, InvalidDomain)
                is Right -> fail("Expected a Either.Left, but got Either.Right")
            }
        }

        @Test
        fun invalidEmailAddressFromNotExampleComDomain() {
            val emailAddress = EmailAddress.create(UnvalidatedEmailAddress("john.done@xample.com"))

            when (emailAddress) {
                is Left -> emailAddress.value shouldBe nonEmptyListOf(InvalidDomain)
                is Right -> fail("Expected a Either.Left, but got Either.Right")
            }
        }

        @Test
        fun invalidEmailAddressFromEmptyAtSymbol() {
            val emailAddress = EmailAddress.create(UnvalidatedEmailAddress("john.done"))

            when (emailAddress) {
                is Left -> emailAddress.value shouldBe nonEmptyListOf(InvalidDomain)
                is Right -> fail("Expected a Either.Left, but got Either.Right")
            }
        }

    }

}