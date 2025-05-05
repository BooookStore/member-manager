package bookstore.playground.domain

import arrow.core.EitherNel
import arrow.core.raise.either
import arrow.core.raise.ensure
import arrow.core.raise.zipOrAccumulate
import bookstore.playground.domain.InvalidEmailAddress.Blank
import bookstore.playground.domain.InvalidEmailAddress.InvalidDomain

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
                { ensure(rawEmailAddress.isNotBlank()) { Blank } },
                { ensure(rawEmailAddress.substringAfter("@") == "example.com") { InvalidDomain } }
            ) { _, _ -> EmailAddress(rawEmailAddress) }
        }
    }

}

data class Name(val rawName: String)