package bookstore.playground.domain

import arrow.core.Either.Left
import arrow.core.Either.Right
import arrow.core.getOrElse
import arrow.core.nonEmptyListOf
import arrow.core.right
import bookstore.playground.port.MemberPort
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
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
    inner class Exist {

        @Test
        fun whenExistReturnTrue() {
            val existingMember = Member.create(
                UnvalidatedMember(
                    UnvalidatedName("John Done"),
                    UnvalidatedEmailAddress("john.done@example.com")
                )
            ).getOrElse { fail("Expected a Either.Right, but got Either.Left") }

            val memberPort = mockk<MemberPort>()
            every { memberPort.getMemberByEmailAddress(existingMember.emailAddress) } returns existingMember.right()

            val result = Member.exist(memberPort, existingMember)

            result.isLeft() shouldBe true
        }

    }

}