package bookstore.playground.domain.validator

sealed interface ValidationResult

object Valid : ValidationResult

data class Invalid(val messages: List<String>) : ValidationResult {

    companion object {
        fun fromSingleMessage(message: String): Invalid = Invalid(listOf(message))
    }

    fun accumulate(other: Invalid): Invalid = Invalid(this.messages + other.messages)

}
