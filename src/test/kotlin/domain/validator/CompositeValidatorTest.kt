package bookstore.playground.domain.validator

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class CompositeValidatorTest {

    @Test
    fun multipleValidations() {
        val validator = CompositeValidator(
            ContainsAySymbolEmailAddressValidator(),
            EmailAddressDomainValidator("example.com")
        )
        val result = validator.validate("aaa@example.com")

        when (result) {
            is Valid -> Unit
            is Invalid -> fail("Expected Valid but got Invalid")
        }
    }

    @Test
    fun oneInvalidValidation() {
        val validator = CompositeValidator(
            ContainsAySymbolEmailAddressValidator(),
            EmailAddressDomainValidator("example.com")
        )
        val result = validator.validate("aaa@xample.com")

        when (result) {
            is Valid -> fail("Expected Invalid but got Valid")
            is Invalid -> assertEquals(
                listOf("invalid email address [aaa@xample.com] not match domain [example.com]"),
                result.messages
            )
        }
    }

    @Test
    fun multipleInvalidValidations() {
        val validator = CompositeValidator(
            ContainsAySymbolEmailAddressValidator(),
            EmailAddressDomainValidator("example.com")
        )
        val result = validator.validate("aaa")

        when (result) {
            is Valid -> fail("Expected Invalid but got Valid")
            is Invalid -> assertEquals(
                listOf(
                    "invalid email address [aaa] not contain '@' symbol",
                    "invalid email address [aaa] not match domain [example.com]"
                ),
                result.messages
            )
        }
    }

}