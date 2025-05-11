package bookstore.playground.gateway

import arrow.core.Either
import arrow.core.raise.option
import bookstore.playground.domain.EmailAddress
import bookstore.playground.domain.Member
import bookstore.playground.domain.MemberNotFound
import bookstore.playground.domain.UnvalidatedEmailAddress
import bookstore.playground.domain.UnvalidatedMember
import bookstore.playground.domain.UnvalidatedName
import bookstore.playground.driver.InsertMemberRow
import bookstore.playground.driver.PostgresMemberDriver
import bookstore.playground.port.MemberPort

object MemberGateway : MemberPort {

    override fun registerNewMember(member: Member) {
        val rawName = member.name.rawName
        val rawEmailAddress = member.emailAddress.rawEmailAddress
        PostgresMemberDriver.insertMember(InsertMemberRow(rawEmailAddress, rawName))
    }

    override fun getMemberByEmailAddress(emailAddress: EmailAddress): Either<MemberNotFound, Member> = option {
        val rawEmailAddress = emailAddress.rawEmailAddress
        val memberRow = PostgresMemberDriver.selectMember(rawEmailAddress).bind()
        Member.create(
            UnvalidatedMember(
                UnvalidatedName(memberRow.name),
                UnvalidatedEmailAddress(memberRow.emailAddress)
            )
        ).getOrNull()
            ?: throw IllegalStateException("Member can't create. Data is invalid. member email address: $rawEmailAddress")
    }.toEither { MemberNotFound }

}