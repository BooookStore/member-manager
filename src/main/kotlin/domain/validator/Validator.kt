package bookstore.playground.domain.validator

interface Validator<T> {

    fun validate(value: T): ValidationResult

}