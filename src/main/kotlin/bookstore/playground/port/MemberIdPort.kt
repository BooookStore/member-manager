package bookstore.playground.port

import bookstore.playground.domain.MemberId

interface MemberIdPort {

    fun generateId(): MemberId

}