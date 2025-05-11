package bookstore.playground.domain

import arrow.core.Either
import arrow.core.EitherNel
import arrow.core.raise.either
import arrow.core.raise.ensure
import arrow.core.raise.zipOrAccumulate
import bookstore.playground.port.MemberPort

sealed interface InvalidMember {
    data class InvalidMemberName(val invalidName: InvalidName) : InvalidMember
    data class InvalidMemberEmailAddress(val invalidEmailAddress: InvalidEmailAddress) : InvalidMember
}

object MemberNotFound

@ConsistentCopyVisibility
data class Member private constructor(val name: Name, val emailAddress: EmailAddress) {

    companion object {
        fun create(unvalidatedMember: UnvalidatedMember): EitherNel<InvalidMember, Member> = either {
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
            ) { name, emailAddress -> Member(name, emailAddress) }
        }

        fun registerAsNewMember(memberPort: MemberPort, member: Member) {
            memberPort.registerNewMember(member)
        }

        fun exist(memberPort: MemberPort, member: Member): Boolean {
            return memberPort.getMemberByEmailAddress(member.emailAddress).isRight()
        }
    }

}

sealed interface InvalidEmailAddress {
    object Blank : InvalidEmailAddress
    object UnexpectedDomain : InvalidEmailAddress
}

@ConsistentCopyVisibility
data class EmailAddress private constructor(val rawEmailAddress: String) {

    companion object {
        fun create(unvalidatedEmailAddress: UnvalidatedEmailAddress): EitherNel<InvalidEmailAddress, EmailAddress> = either {
            val rawEmailAddress = unvalidatedEmailAddress.rawEmailAddress

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
        fun create(unvalidatedName: UnvalidatedName): Either<InvalidName, Name> = either {
            ensure(unvalidatedName.rawName.isNotBlank()) { InvalidName.Blank }
            Name(unvalidatedName.rawName)
        }
    }

}