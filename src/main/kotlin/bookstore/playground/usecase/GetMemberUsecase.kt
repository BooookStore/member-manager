package bookstore.playground.usecase

import bookstore.playground.domain.MemberId
import bookstore.playground.port.MemberPort

fun getMemberUsecase(memberPort: MemberPort, memberId: MemberId): MemberId {
    return memberPort.getMemberById(memberId)
}