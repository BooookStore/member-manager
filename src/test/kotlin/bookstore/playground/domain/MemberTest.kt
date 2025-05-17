package bookstore.playground.domain

import arrow.core.Either.Left
import arrow.core.Either.Right
import arrow.core.getOrElse
import arrow.core.nonEmptyListOf
import arrow.core.none
import arrow.core.some
import bookstore.playground.port.MemberIdPort
import bookstore.playground.port.MemberPort
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import java.util.UUID
import kotlin.test.Test
import kotlin.test.fail

class MemberTest {

    lateinit var memberIdPort: MemberIdPort

    @BeforeEach
    fun setUp() {
        memberIdPort = mockk()
        every { memberIdPort.generateId() } returns MemberId(UUID.randomUUID())
    }

    @Test
    fun memberFromValidEmailAddressAndName() {
        val member = Member.create(
            memberIdPort,
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
            memberIdPort,
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
            memberIdPort,
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
                memberIdPort,
                UnvalidatedMember(
                    UnvalidatedName("John Done"),
                    UnvalidatedEmailAddress("john.done@example.com")
                )
            ).getOrElse { fail("Expected a Either.Right, but got Either.Left") }

            val memberPort = mockk<MemberPort>()
            every { memberPort.getMemberByEmailAddress(existingMember.emailAddress) } returns existingMember.some()

            Member.exist(memberPort, existingMember) shouldBe true
        }

        @Test
        fun whenNotExistReturnFalse() {
            val nonExistingMember = Member.create(
                memberIdPort,
                UnvalidatedMember(
                    UnvalidatedName("John Done"),
                    UnvalidatedEmailAddress("john.done@example.com")
                )
            ).getOrElse { fail("Expected a Either.Right, but got Either.Left") }

            val memberPort = mockk<MemberPort>()
            every { memberPort.getMemberByEmailAddress(any()) } returns none()

            Member.exist(memberPort, nonExistingMember) shouldBe false
        }

    }

}