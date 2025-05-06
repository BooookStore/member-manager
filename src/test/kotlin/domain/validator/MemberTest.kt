package bookstore.playground.domain.validator

import arrow.core.Either.Left
import arrow.core.Either.Right
import arrow.core.nonEmptyListOf
import bookstore.playground.domain.*
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Nested
import kotlin.test.Test
import kotlin.test.fail

class MemberTest {

    @Test
    fun memberFromValidEmailAddressAndName() {
        val member = Member.create(
            UnvalidatedMember(
                UnvalidatedName("John Doe"),
                UnvalidatedEmailAddress("john.doe@example.com")
            )
        )

        when (member) {
            is Left -> fail("Expected a Either.Right, but got Either.Left")
            is Right -> {
                member.value.name.rawName shouldBe "John Doe"
                member.value.emailAddress.rawEmailAddress shouldBe "john.doe@example.com"
            }
        }
    }

    @Test
    fun memberFromInvalidName() {
        val member = Member.create(
            UnvalidatedMember(
                UnvalidatedName(" "),
                UnvalidatedEmailAddress("john.doe@example.com")
            )
        )

        when (member) {
            is Left -> member.value shouldBe nonEmptyListOf(InvalidMember.InvalidMemberName(InvalidName.Blank))
            is Right -> fail("Expected a Either.Left, but got Either.Right")
        }
    }

    @Test
    fun memberFromInvalidNameAndEmailAddress() {
        val member = Member.create(
            UnvalidatedMember(
                UnvalidatedName(" "),
                UnvalidatedEmailAddress("")
            )
        )

        when (member) {
            is Left -> member.value shouldBe nonEmptyListOf(
                InvalidMember.InvalidMemberName(InvalidName.Blank),
                InvalidMember.InvalidMemberEmailAddress(InvalidEmailAddress.Blank),
                InvalidMember.InvalidMemberEmailAddress(InvalidEmailAddress.UnexpectedDomain)
            )
            is Right -> fail("Expected a Either.Left, but got Either.Right")
        }
    }

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
                is Left -> emailAddress.value shouldBe nonEmptyListOf(InvalidEmailAddress.Blank, InvalidEmailAddress.UnexpectedDomain
                )
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

    @Nested
    inner class NameTest {

        @Test
        fun nameFromUnvalidatedName() {
            val name = Name.create(UnvalidatedName("John Doe"))

            when (name) {
                is Left -> fail("Expected a Either.Right, but got Either.Left")
                is Right -> name.value.rawName shouldBe "John Doe"
            }
        }

        @Test
        fun invalidNameFromBlank() {
            val name = Name.create(UnvalidatedName(" "))

            when (name) {
                is Left -> name.value shouldBe InvalidName.Blank
                is Right -> fail("Expected a Either.Left, but got Either.Right")
            }
        }
    }

}