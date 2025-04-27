package bookstore.playground.domain.validator

class EmailAddressValidator {

    fun validate(emailAddress: String): ValidateResult {
        return if (emailAddress.contains("@")) {
            Valid()
        } else {
            Invalid()
        }
    }

}