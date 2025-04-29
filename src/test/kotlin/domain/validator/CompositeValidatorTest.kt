package bookstore.playground.domain.validator

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class CompositeValidatorTest {

    val alwaysValidValidator = object : Validator<String> {
        override fun validate(value: String) = Valid
    }

    val alwaysInvalidValidator = object : Validator<String> {
        override fun validate(value: String) = Invalid(listOf("invalid"))
    }

    @Test
    fun multipleValidations() {
        val validator = CompositeValidator(
            alwaysValidValidator,
            alwaysValidValidator
        )
        val result = validator.validate("dummy value")

        when (result) {
            is Valid -> Unit
            is Invalid -> fail("Expected Valid but got Invalid")
        }
    }

    @Test
    fun oneInvalidValidation() {
        val validator = CompositeValidator(
            alwaysValidValidator,
            alwaysInvalidValidator
        )
        val result = validator.validate("dummy value")

        when (result) {
            is Valid -> fail("Expected Invalid but got Valid")
            is Invalid -> assertEquals(listOf("invalid"), result.messages)
        }
    }

    @Test
    fun multipleInvalidValidations() {
        val validator = CompositeValidator(
            alwaysInvalidValidator,
            alwaysInvalidValidator
        )
        val result = validator.validate("dummy value")

        when (result) {
            is Valid -> fail("Expected Invalid but got Valid")
            is Invalid -> assertEquals(listOf("invalid", "invalid"), result.messages)
        }
    }

}