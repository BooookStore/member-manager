package bookstore.playground.domain.validator

class ContainsAySymbolEmailAddressValidator : Validator {

    override fun validate(emailAddress: String): ValidationResult {
        return if (emailAddress.contains("@")) {
            Valid()
        } else {
            Invalid.fromSingleMessage("invalid email address [$emailAddress] not contain '@' symbol")
        }
    }

}