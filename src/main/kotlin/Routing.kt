package bookstore.playground

import bookstore.playground.handler.member.registerNewMemberHandler
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        post("/members") {
            registerNewMemberHandler()
        }
    }
}
