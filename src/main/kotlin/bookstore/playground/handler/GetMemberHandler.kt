package bookstore.playground.handler

import arrow.core.Option
import bookstore.playground.domain.MemberId
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class GetMemberResponse(val id: String)

suspend fun RoutingContext.getMemberHandler() {
    val id = call.parameters["id"] ?: throw Exception("Member ID not found")

    val memberId: Option<MemberId> =
        Option
            .catch { UUID.fromString(id) }
            .map { MemberId(it) }

    if (memberId.isNone()) {
        call.respond(HttpStatusCode.BadRequest)
        return
    }

    call.respond(HttpStatusCode.OK, GetMemberResponse(id))
}
