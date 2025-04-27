package bookstore.playground.domain.validator

import kotlin.test.Test
import kotlin.test.assertEquals

class ValidatorTest {

    @Test
    fun testValidator() {
        val validator = EmailAddressValidator()
        val result = validator.validate("aaa@example.com")
        assertEquals(true, result.isValid())
    }

}