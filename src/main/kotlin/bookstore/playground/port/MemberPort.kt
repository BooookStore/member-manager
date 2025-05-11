package bookstore.playground.port

import bookstore.playground.domain.Member

interface MemberPort {

    fun registerNewMember(member: Member)

}