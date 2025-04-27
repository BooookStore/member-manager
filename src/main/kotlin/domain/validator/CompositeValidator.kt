package bookstore.playground.domain.validator

class CompositeValidator(private vararg val validators: Validator) : Validator {

    override fun validate(value: String): ValidationResult =
        validators.fold<_, List<ValidationResult>>(listOf(Valid())) { acc, validator -> acc + validator.validate(value) }
            .filterIsInstance<Invalid>()
            .map { it.messages }
            .flatten()
            .let { invalidMessages -> if (invalidMessages.isEmpty()) Valid() else Invalid(invalidMessages) }

}
