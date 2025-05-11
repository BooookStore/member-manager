package bookstore.playground.gateway

import arrow.core.Either
import bookstore.playground.domain.EmailAddress
import bookstore.playground.domain.Member
import bookstore.playground.domain.MemberNotFound
import bookstore.playground.driver.InsertMemberRow
import bookstore.playground.driver.PostgresMemberDriver
import bookstore.playground.port.MemberPort

object MemberGateway : MemberPort {

    override fun registerNewMember(member: Member) {
        val rawName = member.name.rawName
        val rawEmailAddress = member.emailAddress.rawEmailAddress
        PostgresMemberDriver.insertMember(InsertMemberRow(rawEmailAddress, rawName))
    }

    override fun getMemberByEmailAddress(emailAddress: EmailAddress): Either<MemberNotFound, Member> {
        TODO("Not yet implemented")
    }

}