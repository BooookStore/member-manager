package bookstore.playground.driver

import java.util.UUID

object JdkUUIDDriver {

    fun generateUUID(): UUID {
        return UUID.randomUUID()
    }

}