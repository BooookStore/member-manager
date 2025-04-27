package bookstore.playground.handler.member

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

@Serializable
data class ReceiveNewMember(val emailAddress: String, val name: String)

suspend fun RoutingContext.registerNewMemberHandler() {
    val receiveNewMember = call.receive<ReceiveNewMember>()
    call.application.environment.log.info("Received new member registration: $receiveNewMember")

    call.respond(HttpStatusCode.OK)
}