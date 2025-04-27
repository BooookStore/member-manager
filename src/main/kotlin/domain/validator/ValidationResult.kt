package bookstore.playground.domain.validator

sealed interface ValidationResult

class Valid : ValidationResult

data class Invalid(val messages: List<String>) : ValidationResult {

    companion object {
        fun fromSingleMessage(message: String): Invalid = Invalid(listOf(message))
    }

}
