package bookstore.playground.usecase

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.raise.either
import bookstore.playground.domain.InvalidMember
import bookstore.playground.domain.Member
import bookstore.playground.domain.UnvalidatedMember
import bookstore.playground.port.MemberPort

sealed interface RegisterNewMemberError {
    data class InvalidMemberError(val invalidMemberNel: NonEmptyList<InvalidMember>) : RegisterNewMemberError
}

fun registerNewMemberUsecase(memberPort: MemberPort, unvalidatedMember: UnvalidatedMember): Either<RegisterNewMemberError, Unit> = either {
    val member = Member.create(unvalidatedMember)
        .mapLeft { RegisterNewMemberError.InvalidMemberError(it) }
        .bind()
    Member.registerAsNewMember(memberPort, member)
}