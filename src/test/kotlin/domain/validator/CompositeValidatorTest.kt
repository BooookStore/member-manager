package bookstore.playground.domain.validator

import kotlin.test.Test
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

}