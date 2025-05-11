package bookstore.playground.bookstore.playground.driver

import bookstore.playground.bookstore.playground.domain.Member
import bookstore.playground.bookstore.playground.gateway.MemberGateway
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction

object MemberTable : Table("member") {
    val emailAddress = varchar("email_address", 255)
    val name = varchar("name", 255)
    override val primaryKey = PrimaryKey(emailAddress)
}

object PostgresMemberDriver : MemberGateway {

    override fun registerNewMember(member: Member) {
        transaction {
            MemberTable.insert {
                it[emailAddress] = member.emailAddress.rawEmailAddress
                it[name] = member.name.rawName
            }
        }
    }

}