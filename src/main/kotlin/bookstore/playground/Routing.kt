package bookstore.playground

import bookstore.playground.handler.registerNewMemberHandler
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        post("/members") {
            registerNewMemberHandler()
        }
        get("/members/{id}") {
            val id = call.parameters["id"] ?: throw Exception("Member ID not found")
            call.respond(HttpStatusCode.OK, id)
        }
    }
}
