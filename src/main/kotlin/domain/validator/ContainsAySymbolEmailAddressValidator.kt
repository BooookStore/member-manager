package bookstore.playground.domain.validator

class ContainsAySymbolEmailAddressValidator {

    fun validate(emailAddress: String): ValidateResult {
        return if (emailAddress.contains("@")) {
            Valid()
        } else {
            Invalid.fromSingleMessage("invalid email address [$emailAddress] not contain @")
        }
    }

}