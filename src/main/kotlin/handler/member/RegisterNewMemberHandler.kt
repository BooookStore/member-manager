package bookstore.playground.handler.member

import bookstore.playground.domain.validator.CompositeValidator
import bookstore.playground.domain.validator.ContainsAySymbolEmailAddressValidator
import bookstore.playground.domain.validator.EmailAddressDomainValidator
import bookstore.playground.domain.validator.Invalid
import bookstore.playground.domain.validator.Valid
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

@Serializable
data class ReceiveNewMember(val emailAddress: String, val name: String)

suspend fun RoutingContext.registerNewMemberHandler() {
    val receiveNewMember = call.receive<ReceiveNewMember>()
    call.application.environment.log.info("Received new member registration: $receiveNewMember")

    val validator = CompositeValidator(ContainsAySymbolEmailAddressValidator, EmailAddressDomainValidator("example.com"))
    val validationResult = validator.validate(receiveNewMember.emailAddress)

    when (validationResult) {
        is Valid -> call.respond(HttpStatusCode.OK)
        is Invalid -> call.respond(HttpStatusCode.BadRequest)
    }
}