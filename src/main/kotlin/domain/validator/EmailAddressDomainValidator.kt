package bookstore.playground.domain.validator

class EmailAddressDomainValidator(val domain: String) : Validator {

    override fun validate(emailAddress: String): ValidationResult {
        return if (emailAddress.endsWith("@$domain")) {
            Valid()
        } else {
            Invalid.fromSingleMessage("invalid email address [$emailAddress] not match domain [$domain]")
        }
    }

}