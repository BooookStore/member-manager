package bookstore.playground.usecase

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.raise.either
import arrow.core.raise.ensure
import bookstore.playground.domain.InvalidMember
import bookstore.playground.domain.Member
import bookstore.playground.domain.UnvalidatedMember
import bookstore.playground.port.MemberIdPort
import bookstore.playground.port.MemberPort

sealed interface RegisterNewMemberError {
    data class InvalidMemberError(val invalidMemberNel: NonEmptyList<InvalidMember>) : RegisterNewMemberError
    object MemberAlreadyExistsError : RegisterNewMemberError
}

fun registerNewMemberUsecase(memberIdPort: MemberIdPort, memberPort: MemberPort, unvalidatedMember: UnvalidatedMember): Either<RegisterNewMemberError, Unit> = either {
    val member = Member.createNew(memberIdPort, unvalidatedMember)
        .mapLeft { RegisterNewMemberError.InvalidMemberError(it) }
        .bind()
    ensure(Member.exist(memberPort, member).not()) { RegisterNewMemberError.MemberAlreadyExistsError }
    Member.registerAsNewMember(memberPort, member)
}