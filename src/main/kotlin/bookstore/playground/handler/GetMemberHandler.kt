package bookstore.playground.handler

import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.RoutingContext
import java.util.UUID

suspend fun RoutingContext.getMemberHandler() {
    val id = call.parameters["id"] ?: throw Exception("Member ID not found")
    // check id is valid UUID
    val isValid = try {
        UUID.fromString(id)
        true
    } catch (e: IllegalArgumentException) {
        false
    }
    // if invalid, respond with BadRequest
    if (!isValid) {
        call.respond(HttpStatusCode.BadRequest)
        return
    }
    call.respond(HttpStatusCode.OK, id)
}
