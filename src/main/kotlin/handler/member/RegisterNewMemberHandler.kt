package bookstore.playground.handler.member

import bookstore.playground.domain.UnvalidatedEmailAddress
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

suspend fun RoutingContext.registerNewMemberHandler() {
    val newMember = call.receive<RegisterNewMemberRequest>()
    logger.info("Received new member request: $newMember")

    val unvalidatedEmailAddress = UnvalidatedEmailAddress(newMember.emailAddress)

    val validator = CompositeValidator(EmailAddressContainsAtSymbolValidator, EmailAddressDomainValidator("example.com"))
    val validationResult = validator.validate(unvalidatedEmailAddress)

    when (validationResult) {
        is Valid -> call.respond(HttpStatusCode.OK)
        is Invalid -> {
            logger.info("Received new member with invalid email address: ${validationResult.messages}")
            call.respond(HttpStatusCode.BadRequest, RegisterNewMemberBadRequestResponse(validationResult.messages))
        }
    }
}