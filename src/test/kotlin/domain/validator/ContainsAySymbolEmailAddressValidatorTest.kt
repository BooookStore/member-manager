package bookstore.playground.domain.validator

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class ContainsAySymbolEmailAddressValidatorTest {

    @Test
    fun `include@EmailAddress`() {
        val validator = ContainsAySymbolEmailAddressValidator()
        val result = validator.validate("aaa@example.com")

        when (result) {
            is Valid -> Unit
            is Invalid -> fail("Expected Valid but got Invalid")
        }
    }

    @Test
    fun `notInclude@EmailAddress`() {
        val validator = ContainsAySymbolEmailAddressValidator()
        val result = validator.validate("aaa")

        when (result) {
            is Valid -> fail("Expected Invalid but got Valid")
            is Invalid -> assertEquals(listOf("invalid email address [aaa] not contain @"), result.messages)
        }
    }

}