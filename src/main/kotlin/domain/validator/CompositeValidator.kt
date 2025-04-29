package bookstore.playground.domain.validator

class CompositeValidator<T>(private vararg val validators: Validator<T>) : Validator<T> {

    override fun validate(value: T): ValidationResult =
        validators.fold<_, List<ValidationResult>>(listOf(Valid())) { acc, validator -> acc + validator.validate(value) }
            .filterIsInstance<Invalid>()
            .map { it.messages }
            .flatten()
            .let { invalidMessages -> if (invalidMessages.isEmpty()) Valid() else Invalid(invalidMessages) }

}
