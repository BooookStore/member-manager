package bookstore.playground.domain.validator

class EmailAddressDomainValidator(val domain: String) {

    fun validate(emailAddress: String): ValidateResult {
        return if (emailAddress.endsWith("@$domain")) {
            Valid()
        } else {
            Invalid.fromSingleMessage("invalid email address [$emailAddress] does not match domain [$domain]")
        }
    }

}