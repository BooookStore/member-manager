package bookstore.playground.handler.member

import arrow.core.Either
import bookstore.playground.domain.*
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
    val member = Member.create(unvalidatedMember)

    when (member) {
        is Either.Right -> {
            logger.info("Member created successfully: $member")
            call.respond(HttpStatusCode.Created)
        }
        is Either.Left -> {
            val validationErrors = member.value
            val messages = validationErrors.map {
                when (it) {
                    is InvalidMember.InvalidMemberName -> {
                        when (it.invalidName) {
                            InvalidName.Blank -> "invalid name '${unvalidatedMember.unvalidatedName.rawName}' is blank"
                        }
                    }
                    is InvalidMember.InvalidMemberEmailAddress -> {
                        when (it.invalidEmailAddress) {
                            InvalidEmailAddress.Blank -> "invalid email address '${unvalidatedMember.unvalidatedEmailAddress.rawEmailAddress}' is blank"
                            InvalidEmailAddress.UnexpectedDomain -> "invalid email address '${unvalidatedMember.unvalidatedEmailAddress.rawEmailAddress}' not match domain 'example.com'"
                        }
                    }
                }
            }
            logger.warn("Member creation failed: $messages")
            call.respond(
                HttpStatusCode.BadRequest,
                RegisterNewMemberBadRequestResponse(messages.toList())
            )
        }
    }
}