package bookstore.playground.domain.validator

interface Validator {

    fun validate(value: String): ValidationResult

}