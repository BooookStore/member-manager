package bookstore.playground.handler

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import bookstore.playground.domain.MemberId
import bookstore.playground.gateway.MemberGateway
import bookstore.playground.usecase.getMemberUsecase
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
            .map { getMemberUsecase(MemberGateway, it) }

    when (memberId) {
        is None -> call.respond(HttpStatusCode.BadRequest)
        is Some -> call.respond(HttpStatusCode.OK, GetMemberResponse(memberId.value.rawId.toString()))
    }
}
