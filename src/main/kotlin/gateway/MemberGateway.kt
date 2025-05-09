package bookstore.playground.gateway

import bookstore.playground.domain.Member

interface MemberGateway {

    fun registerNewMember(member: Member)

}