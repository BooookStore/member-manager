package bookstore.playground.domain.validator

class EmailAddressDomainValidator(private val domain: String) : Validator {

    override fun validate(value: String): ValidationResult {
        return if (value.endsWith("@$domain")) {
            Valid()
        } else {
            Invalid.fromSingleMessage("invalid email address [$value] not match domain [$domain]")
        }
    }

}

object ContainsAySymbolEmailAddressValidator : Validator {

    override fun validate(value: String): ValidationResult {
        return if (value.contains("@")) {
            Valid()
        } else {
            Invalid.fromSingleMessage("invalid email address [$value] not contain '@' symbol")
        }
    }

}