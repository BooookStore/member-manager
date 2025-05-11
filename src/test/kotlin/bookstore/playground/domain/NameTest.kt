package bookstore.playground.domain

import arrow.core.Either
import io.kotest.matchers.shouldBe
import kotlin.test.Test
import kotlin.test.fail

class NameTest {

    @Test
    fun nameFromUnvalidatedName() {
        val name = Name.Companion.create(UnvalidatedName("John Doe"))

        when (name) {
            is Either.Left -> fail("Expected a Either.Right, but got Either.Left")
            is Either.Right -> name.value.rawName shouldBe "John Doe"
        }
    }

    @Test
    fun invalidNameFromBlank() {
        val name = Name.create(UnvalidatedName(" "))

        when (name) {
            is Either.Left -> name.value shouldBe InvalidName.Blank
            is Either.Right -> fail("Expected a Either.Left, but got Either.Right")
        }
    }

}
