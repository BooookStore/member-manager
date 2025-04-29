package bookstore.playground

import bookstore.playground.domain.validator.CompositeValidator
import bookstore.playground.domain.validator.EmailAddressContainsAtSymbolValidator
import bookstore.playground.domain.validator.EmailAddressDomainValidator
import bookstore.playground.handler.member.registerNewMemberHandler
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        post("/members") {
            val emailAddressValidator = CompositeValidator(
                EmailAddressContainsAtSymbolValidator,
                EmailAddressDomainValidator("example.com")
            )
            registerNewMemberHandler(emailAddressValidator)
        }
    }
}
