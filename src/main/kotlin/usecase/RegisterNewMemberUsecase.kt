package bookstore.playground.usecase

import arrow.core.EitherNel
import arrow.core.raise.either
import bookstore.playground.domain.InvalidMember
import bookstore.playground.domain.Member
import bookstore.playground.domain.UnvalidatedMember

fun registerNewMemberUsecase(unvalidatedMember: UnvalidatedMember): EitherNel<InvalidMember, Unit> = either {
    Member.create(unvalidatedMember).bind()
    Unit
}