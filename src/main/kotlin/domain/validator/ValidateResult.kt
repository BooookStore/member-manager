package bookstore.playground.domain.validator

sealed interface ValidateResult {

    fun isValid(): Boolean

}

class Valid : ValidateResult {

    override fun isValid(): Boolean = true

}
