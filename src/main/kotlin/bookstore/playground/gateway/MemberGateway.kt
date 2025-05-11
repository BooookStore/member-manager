package bookstore.playground.gateway

import bookstore.playground.domain.Member
import bookstore.playground.driver.PostgresMemberDriver
import bookstore.playground.port.MemberPort

object MemberGateway : MemberPort {

    override fun registerNewMember(member: Member) {
        PostgresMemberDriver.insertMember(member)
    }

}