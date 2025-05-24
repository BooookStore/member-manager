package bookstore.playground.gateway

import arrow.core.raise.option
import bookstore.playground.domain.EmailAddress
import bookstore.playground.domain.Member
import bookstore.playground.domain.MemberId
import bookstore.playground.domain.Name
import bookstore.playground.driver.InsertMemberRow
import bookstore.playground.driver.PostgresMemberDriver
import bookstore.playground.port.MemberPort
import java.util.*

object MemberGateway : MemberPort {

    override fun registerNewMember(member: Member) {
        val id = UUID.randomUUID()
        val rawName = member.name.rawName
        val rawEmailAddress = member.emailAddress.rawEmailAddress
        PostgresMemberDriver.insertMember(InsertMemberRow(id, rawEmailAddress, rawName))
    }

    override fun getMemberByEmailAddress(emailAddress: EmailAddress) = option {
        val rawEmailAddress = emailAddress.rawEmailAddress
        val memberRow = PostgresMemberDriver.selectMember(rawEmailAddress).bind()

        val memberId = MemberId(memberRow.id)
        val memberName = Name.create(memberRow.name).getOrNull()
                ?: throw IllegalStateException("Member can't create. Data is invalid. member name: ${memberRow.name}")
        val memberEmailAddress = EmailAddress.create(memberRow.emailAddress).getOrNull()
                ?: throw IllegalStateException("Member can't create. Data is invalid. member email address: $rawEmailAddress")

        Member.create(memberId, memberName, memberEmailAddress)
    }

    override fun getMemberById(memberId: MemberId): MemberId {
        return memberId
    }

}