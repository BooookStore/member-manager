package bookstore.playground.domain.validator

import bookstore.playground.domain.UnvalidatedEmailAddress

class EmailAddressDomainValidator(private val domain: String) : Validator<UnvalidatedEmailAddress> {

    override fun validate(value: UnvalidatedEmailAddress): ValidationResult {
        return if (value.rawEmailAddress.endsWith("@$domain")) {
            Valid()
        } else {
            Invalid.fromSingleMessage("invalid email address '${value.rawEmailAddress}' not match domain '$domain'")
        }
    }
}

object EmailAddressContainsAtSymbolValidator : Validator<UnvalidatedEmailAddress> {

    override fun validate(value: UnvalidatedEmailAddress): ValidationResult {
        return if (value.rawEmailAddress.contains("@")) {
            Valid()
        } else {
            Invalid.fromSingleMessage("invalid email address '${value.rawEmailAddress}' not contain '@' symbol")
        }
    }

}