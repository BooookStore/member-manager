package bookstore.playground.domain.validator

import org.junit.jupiter.api.Nested
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class EmailAddressValidatorTest {

    @Nested
    inner class ContainsAySymbolEmailAddressValidatorTest {

        @Test
        fun `include@EmailAddress`() {
            val validator = ContainsAySymbolEmailAddressValidator
            val result = validator.validate("aaa@example.com")

            when (result) {
                is Valid -> Unit
                is Invalid -> fail("Expected Valid but got Invalid")
            }
        }

        @Test
        fun `notInclude@EmailAddress`() {
            val validator = ContainsAySymbolEmailAddressValidator
            val result = validator.validate("aaa")

            when (result) {
                is Valid -> fail("Expected Invalid but got Valid")
                is Invalid -> assertEquals(listOf("invalid email address [aaa] not contain '@' symbol"), result.messages)
            }
        }

    }

    @Nested
    inner class EmailAddressDomainValidatorTest {

        @Test
        fun validDomainEmailAddress() {
            val validator = EmailAddressDomainValidator("example.com")
            val result = validator.validate("aaa@example.com")

            when (result) {
                is Valid -> Unit
                is Invalid -> fail("Expected Valid but got Invalid")
            }
        }

        @Test
        fun invalidDomainEmailAddress() {
            val validator = EmailAddressDomainValidator("example.com")
            val result = validator.validate("aaa@xample.com")

            when (result) {
                is Valid -> fail("Expected Invalid but got Valid")
                is Invalid -> assertEquals(listOf("invalid email address [aaa@xample.com] not match domain [example.com]"), result.messages)
            }
        }

    }

}