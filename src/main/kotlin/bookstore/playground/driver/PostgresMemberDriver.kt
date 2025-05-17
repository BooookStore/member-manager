package bookstore.playground.driver

import arrow.core.Option
import arrow.core.firstOrNone
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

object MemberTable : Table("member") {
    val id = uuid("id")
    val emailAddress = varchar("email_address", 255)
    val name = varchar("name", 255)
    override val primaryKey = PrimaryKey(id)
}

data class InsertMemberRow(val emailAddress: String, val name: String)

data class SelectMemberRow(val id: UUID, val emailAddress: String, val name: String)

object PostgresMemberDriver {

    fun insertMember(row: InsertMemberRow) {
        transaction {
            MemberTable.insert {
                it[id] = UUID.randomUUID()
                it[emailAddress] = row.emailAddress
                it[name] = row.name
            }
        }
    }

    fun selectMember(rawEmailAddress: String): Option<SelectMemberRow> = transaction {
        val member = MemberTable
            .selectAll()
            .where { MemberTable.emailAddress eq rawEmailAddress }
            .map {
                SelectMemberRow(
                    it[MemberTable.id],
                    it[MemberTable.emailAddress],
                    it[MemberTable.name]
                )
            }
            .firstOrNone()
        return@transaction member
    }

}