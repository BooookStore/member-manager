package bookstore.playground.handler

import arrow.core.Either
import arrow.core.NonEmptyList
import bookstore.playground.domain.*
import bookstore.playground.usecase.registerNewMemberUsecase
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("RegisterNewMemberHandler")!!

@Serializable
data class RegisterNewMemberRequest(val emailAddress: String, val name: String)

@Serializable
data class RegisterNewMemberBadRequestResponse(val messages: List<String>)

fun RegisterNewMemberRequest.toUnvalidatedMember(): UnvalidatedMember =
    UnvalidatedMember(
        unvalidatedName = UnvalidatedName(name),
        unvalidatedEmailAddress = UnvalidatedEmailAddress(emailAddress)
    )

suspend fun RoutingContext.registerNewMemberHandler() {
    val newMemberRequest = call.receive<RegisterNewMemberRequest>()
    logger.info("Received new member request: $newMemberRequest")

    val unvalidatedMember = newMemberRequest.toUnvalidatedMember()
    val validationResult = registerNewMemberUsecase(unvalidatedMember)

    when (validationResult) {
        is Either.Right -> {
            logger.info("Member created successfully: $validationResult")
            call.respond(HttpStatusCode.Companion.Created)
        }
        is Either.Left -> {
            val messages = errorMessages(validationResult, unvalidatedMember)
            logger.warn("Member creation failed: $messages")
            call.respond(
                HttpStatusCode.Companion.BadRequest,
                RegisterNewMemberBadRequestResponse(messages.toList())
            )
        }
    }
}

private fun errorMessages(member: Either.Left<NonEmptyList<InvalidMember>>, unvalidatedMember: UnvalidatedMember): NonEmptyList<String> {
    fun messageByInvalidMemberName(name: InvalidMember.InvalidMemberName): String = when (name.invalidName) {
        InvalidName.Blank -> "invalid name '${unvalidatedMember.unvalidatedName.rawName}' is blank"
    }

    fun messageByInvalidMemberEmailAddress(address: InvalidMember.InvalidMemberEmailAddress): String = when (address.invalidEmailAddress) {
        InvalidEmailAddress.Blank -> "invalid email address '${unvalidatedMember.unvalidatedEmailAddress.rawEmailAddress}' is blank"
        InvalidEmailAddress.UnexpectedDomain -> "invalid email address '${unvalidatedMember.unvalidatedEmailAddress.rawEmailAddress}' not match domain 'example.com'"
    }

    return member.value.map {
        when (it) {
            is InvalidMember.InvalidMemberName -> messageByInvalidMemberName(it)
            is InvalidMember.InvalidMemberEmailAddress -> messageByInvalidMemberEmailAddress(it)
        }
    }
}