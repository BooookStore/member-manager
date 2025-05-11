package bookstore.playground.port

import arrow.core.Either
import bookstore.playground.domain.EmailAddress
import bookstore.playground.domain.Member
import bookstore.playground.domain.MemberNotFound

interface MemberPort {

    fun registerNewMember(member: Member)

    fun getMemberByEmailAddress(emailAddress: EmailAddress): Either<MemberNotFound, Member>

}