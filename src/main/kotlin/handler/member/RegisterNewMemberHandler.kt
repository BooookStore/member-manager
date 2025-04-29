package bookstore.playground.handler.member

import bookstore.playground.domain.unvalidated.member.UnvalidatedEmailAddress
import bookstore.playground.domain.unvalidated.member.UnvalidatedMember
import bookstore.playground.domain.unvalidated.member.UnvalidatedName
import bookstore.playground.domain.validator.*
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

suspend fun RoutingContext.registerNewMemberHandler(
    memberValidator: Validator<UnvalidatedMember>,
) {
    val newMemberRequest = call.receive<RegisterNewMemberRequest>()
    logger.info("Received new member request: $newMemberRequest")

    val unvalidatedMember = newMemberRequest.toUnvalidatedMember()
    val validationResult = memberValidator.validate(unvalidatedMember)

    when (validationResult) {
        is Valid -> call.respond(HttpStatusCode.OK)
        is Invalid -> {
            logger.info("Received new member is invalid: ${validationResult.messages}")
            call.respond(HttpStatusCode.BadRequest, RegisterNewMemberBadRequestResponse(validationResult.messages))
        }
    }
}