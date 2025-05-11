package bookstore.playground.bookstore.playground

import bookstore.playground.bookstore.playground.handler.registerNewMemberHandler
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        post("/members") {
            registerNewMemberHandler()
        }
    }
}
