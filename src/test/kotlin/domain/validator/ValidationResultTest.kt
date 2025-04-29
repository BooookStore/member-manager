package bookstore.playground.domain.validator

import kotlin.test.Test
import kotlin.test.assertEquals

class ValidationResultTest {

    @Test
    fun invalidAccumulation() {
        val invalid1 = Invalid.fromSingleMessage("Error 1")
        val invalid2 = Invalid.fromSingleMessage("Error 2")

        val accumulatedInvalid = invalid1.accumulate(invalid2)

        assertEquals(listOf("Error 1", "Error 2"), accumulatedInvalid.messages)
    }

}