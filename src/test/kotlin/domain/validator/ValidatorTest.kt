package bookstore.playground.domain.validator

import kotlin.test.Test
import kotlin.test.assertEquals

class ValidatorTest {

    @Test
    fun validEmailAddress() {
        val validator = EmailAddressValidator()
        val result = validator.validate("aaa@example.com")
        assertEquals(true, result.isValid())
    }

    @Test
    fun invalidEmailAddress() {
        val validator = EmailAddressValidator()
        val result = validator.validate("aaa")
        assertEquals(false, result.isValid())
    }

}