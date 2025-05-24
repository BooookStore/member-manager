package bookstore.playground.handler

import arrow.core.Option
import bookstore.playground.domain.MemberId
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*

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

    call.respond(HttpStatusCode.OK, memberId.getOrNull()?.rawId?.toString() ?: TODO())
}
