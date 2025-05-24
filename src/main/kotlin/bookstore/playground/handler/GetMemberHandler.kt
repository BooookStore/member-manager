package bookstore.playground.handler

import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.RoutingContext

suspend fun RoutingContext.getMemberHandler() {
    val id = call.parameters["id"] ?: throw Exception("Member ID not found")
    call.respond(HttpStatusCode.OK, id)
}