package bookstore.playground

import bookstore.playground.domain.UnvalidatedMember
import bookstore.playground.domain.validator.CompositeValidator
import bookstore.playground.domain.validator.EmailAddressContainsAtSymbolValidator
import bookstore.playground.domain.validator.EmailAddressDomainValidator
import bookstore.playground.domain.validator.ValidationResult
import bookstore.playground.domain.validator.Validator
import bookstore.playground.handler.member.registerNewMemberHandler
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        post("/members") {
            val memberValidator = object : Validator<UnvalidatedMember> {
                override fun validate(value: UnvalidatedMember): ValidationResult {
                    val emailAddressValidator = CompositeValidator(
                        EmailAddressContainsAtSymbolValidator,
                        EmailAddressDomainValidator("example.com")
                    )
                    return emailAddressValidator.validate(value.unvalidatedEmailAddress)
                }
            }
            registerNewMemberHandler(memberValidator)
        }
    }
}
