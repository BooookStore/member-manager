package bookstore.playground.gateway

import bookstore.playground.domain.Member
import bookstore.playground.driver.InsertMemberRow
import bookstore.playground.driver.PostgresMemberDriver
import bookstore.playground.port.MemberPort

object MemberGateway : MemberPort {

    override fun registerNewMember(member: Member) {
        val rawName = member.name.rawName
        val rawEmailAddress = member.emailAddress.rawEmailAddress
        PostgresMemberDriver.insertMember(InsertMemberRow(rawEmailAddress, rawName))
    }

}