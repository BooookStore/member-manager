package bookstore.playground.driver

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction

object MemberTable : Table("member") {
    val emailAddress = varchar("email_address", 255)
    val name = varchar("name", 255)
    override val primaryKey = PrimaryKey(emailAddress)
}

data class InsertMemberRow(val emailAddress: String, val name: String)

object PostgresMemberDriver {

    fun insertMember(row: InsertMemberRow) {
        transaction {
            MemberTable.insert {
                it[emailAddress] = row.emailAddress
                it[name] = row.name
            }
        }
    }

}