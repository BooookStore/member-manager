package bookstore.playground.domain

import arrow.core.Either
import arrow.core.EitherNel
import arrow.core.raise.either
import arrow.core.raise.ensure
import arrow.core.raise.zipOrAccumulate

data class Member(val name: Name, val emailAddress: EmailAddress)

sealed interface InvalidEmailAddress {
    object Blank : InvalidEmailAddress
    object InvalidDomain : InvalidEmailAddress
}

@ConsistentCopyVisibility
data class EmailAddress private constructor(val rawEmailAddress: String) {

    companion object {
        fun create(unvalidatedEmailAddress: UnvalidatedEmailAddress): EitherNel<InvalidEmailAddress, EmailAddress> = either {
            val rawEmailAddress = unvalidatedEmailAddress.rawEmailAddress

            zipOrAccumulate(
                { ensure(rawEmailAddress.isNotBlank()) { InvalidEmailAddress.Blank } },
                { ensure(rawEmailAddress.substringAfter("@") == "example.com") { InvalidEmailAddress.InvalidDomain } }
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