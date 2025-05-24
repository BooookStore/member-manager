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
            call.respond(HttpStatusCode.OK)
        }
    }
}
