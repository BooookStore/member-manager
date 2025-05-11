package bookstore.playground.domain.validator

import arrow.core.Either.Left
import arrow.core.Either.Right
import arrow.core.nonEmptyListOf
import bookstore.playground.domain.*
import io.kotest.matchers.shouldBe
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

}