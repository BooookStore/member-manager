package bookstore.playground.domain.validator

class CompositeValidator(private vararg val validators: Validator) : Validator {

    override fun validate(value: String): ValidationResult =
        validators.fold<_, ValidationResult>(Valid()) { acc, validator -> if (acc is Valid) validator.validate(value) else acc }

}
