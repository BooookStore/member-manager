package bookstore.playground.port

import arrow.core.Option
import bookstore.playground.domain.EmailAddress
import bookstore.playground.domain.Member
import bookstore.playground.domain.MemberId

interface MemberPort {

    fun registerNewMember(member: Member)

    fun getMemberByEmailAddress(emailAddress: EmailAddress): Option<Member>

    fun getMemberById(memberId: MemberId): MemberId

}