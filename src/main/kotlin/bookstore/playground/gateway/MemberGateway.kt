package bookstore.playground.bookstore.playground.gateway

import bookstore.playground.bookstore.playground.domain.Member

interface MemberGateway {

    fun registerNewMember(member: Member)

}