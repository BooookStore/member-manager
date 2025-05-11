package bookstore.playground.domain

data class UnvalidatedMember(val unvalidatedName: UnvalidatedName, val unvalidatedEmailAddress: UnvalidatedEmailAddress)

data class UnvalidatedEmailAddress(val rawEmailAddress: String)

data class UnvalidatedName(val rawName: String)