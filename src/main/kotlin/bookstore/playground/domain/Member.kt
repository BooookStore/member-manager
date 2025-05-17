package bookstore.playground.domain

import arrow.core.Either
import arrow.core.EitherNel
import arrow.core.raise.either
import arrow.core.raise.ensure
import arrow.core.raise.zipOrAccumulate
import bookstore.playground.port.MemberIdPort
import bookstore.playground.port.MemberPort
import java.util.UUID

sealed interface InvalidMember {
    data class InvalidMemberName(val invalidName: InvalidName) : InvalidMember
    data class InvalidMemberEmailAddress(val invalidEmailAddress: InvalidEmailAddress) : InvalidMember
}

@ConsistentCopyVisibility
data class Member private constructor(val id: MemberId, val name: Name, val emailAddress: EmailAddress) {

    companion object {
        fun create(memberIdPort: MemberIdPort, unvalidatedMember: UnvalidatedMember): EitherNel<InvalidMember, Member> = either {
            val (unvalidatedName, unvalidatedEmailAddress) = unvalidatedMember
            zipOrAccumulate(
                {
                    Name.create(unvalidatedName)
                        .mapLeft { InvalidMember.InvalidMemberName(it) }
                        .bind()
                },
                {
                    EmailAddress.create(unvalidatedEmailAddress)
                        .mapLeft { it.map(InvalidMember::InvalidMemberEmailAddress) }
                        .bindNel()
                }
            ) { name, emailAddress -> Member(memberIdPort.generateId(), name, emailAddress) }
        }

        fun create(memberId: MemberId, name: Name, emailAddress: EmailAddress) = Member(memberId, name, emailAddress)

        fun registerAsNewMember(memberPort: MemberPort, member: Member) {
            memberPort.registerNewMember(member)
        }

        fun exist(memberPort: MemberPort, member: Member): Boolean {
            return memberPort.getMemberByEmailAddress(member.emailAddress).isSome()
        }
    }

}

@JvmInline
value class MemberId(val rawId: UUID)

sealed interface InvalidEmailAddress {
    object Blank : InvalidEmailAddress
    object UnexpectedDomain : InvalidEmailAddress
}

@ConsistentCopyVisibility
data class EmailAddress private constructor(val rawEmailAddress: String) {

    companion object {
        // TODO: Remove
        fun create(unvalidatedEmailAddress: UnvalidatedEmailAddress): EitherNel<InvalidEmailAddress, EmailAddress> = create(unvalidatedEmailAddress.rawEmailAddress)

        fun create(rawEmailAddress: String): EitherNel<InvalidEmailAddress, EmailAddress> = either {
            zipOrAccumulate(
                { ensure(rawEmailAddress.isNotBlank()) { InvalidEmailAddress.Blank } },
                { ensure(rawEmailAddress.substringAfter("@") == "example.com") { InvalidEmailAddress.UnexpectedDomain } }
            ) { _, _ -> EmailAddress(rawEmailAddress) }
        }
    }

}

sealed interface InvalidName {
    object Blank : InvalidName
}

@ConsistentCopyVisibility
data class Name private constructor(val rawName: String) {

    companion object {
        // TODO: Remove
        fun create(unvalidatedName: UnvalidatedName): Either<InvalidName, Name> = create(unvalidatedName.rawName)

        fun create(rawName: String): Either<InvalidName, Name> = either {
            ensure(rawName.isNotBlank()) { InvalidName.Blank }
            Name(rawName)
        }
    }

}