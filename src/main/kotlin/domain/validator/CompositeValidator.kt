package bookstore.playground.domain.validator

class CompositeValidator<T>(private vararg val validators: Validator<T>) : Validator<T> {

    override fun validate(value: T): ValidationResult {
        val invalids = validators
            .fold<_, List<ValidationResult>>(listOf(Valid)) { acc, validator -> acc + validator.validate(value) }
            .filterIsInstance<Invalid>()

        return if (invalids.isEmpty()) {
            Valid
        } else {
            invalids.reduce(Invalid::accumulate)
        }
    }
}
