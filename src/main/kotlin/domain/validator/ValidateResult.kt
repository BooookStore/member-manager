package bookstore.playground.domain.validator

sealed interface ValidateResult

class Valid : ValidateResult

data class Invalid(val messages: List<String>) : ValidateResult {

    companion object {
        fun fromSingleMessage(message: String): Invalid = Invalid(listOf(message))
    }

}
