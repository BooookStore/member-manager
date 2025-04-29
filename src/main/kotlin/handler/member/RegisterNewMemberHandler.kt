package bookstore.playground.handler.member

import bookstore.playground.domain.validator.*
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("RegisterNewMemberHandler")!!

@Serializable
data class RequestNewMemberJson(val emailAddress: String, val name: String)

suspend fun RoutingContext.registerNewMemberHandler() {
    val receiveNewMember = call.receive<RequestNewMemberJson>()
    logger.info("Received new member request: $receiveNewMember")

    val validator = CompositeValidator(ContainsAySymbolEmailAddressValidator, EmailAddressDomainValidator("example.com"))
    val validationResult = validator.validate(receiveNewMember.emailAddress)

    when (validationResult) {
        is Valid -> call.respond(HttpStatusCode.OK)
        is Invalid -> call.respond(HttpStatusCode.BadRequest)
    }
}