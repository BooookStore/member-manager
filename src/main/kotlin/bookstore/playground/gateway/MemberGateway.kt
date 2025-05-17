package bookstore.playground.gateway

import arrow.core.raise.option
import bookstore.playground.domain.*
import bookstore.playground.driver.InsertMemberRow
import bookstore.playground.driver.PostgresMemberDriver
import bookstore.playground.port.MemberPort
import java.util.UUID

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
        Member.create(
            UnvalidatedMember(
                UnvalidatedName(memberRow.name),
                UnvalidatedEmailAddress(memberRow.emailAddress)
            )
        ).getOrNull() ?: throw IllegalStateException("Member can't create. Data is invalid. member email address: $rawEmailAddress")
    }

}