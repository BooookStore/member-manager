package bookstore.playground.domain.validator

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class EmailAddressValidatorTest {

    @Test
    fun validEmailAddress() {
        val validator = EmailAddressValidator()
        val result = validator.validate("aaa@example.com")

        when (result) {
            is Invalid -> fail("Expected Valid but got Invalid")
            is Valid -> Unit
        }
    }

    @Test
    fun invalidEmailAddress() {
        val validator = EmailAddressValidator()
        val result = validator.validate("aaa")

        when (result) {
            is Valid -> fail("Expected Invalid but got Valid")
            is Invalid -> assertEquals(listOf("invalid email address [aaa] not contain @"), result.messages)
        }
    }

}