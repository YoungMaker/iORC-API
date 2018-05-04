package edu.ycp.cs482.iorcapi.model.authentication

import org.springframework.data.annotation.Id

data class User(
        @Id val id: String,
        val email: String,
        val uname: String,
        val authorityLevels: List<AuthorityLevel>,
        val passwordHash: ByteArray,
        val passwordSalt: ByteArray,
        val tokenList: List<Context> = listOf()
)

data class UserQL(
        @Id val id: String,
        val uname: String,
        val email: String,
        val authorityLevels: List<AuthorityLevel>
) {
    constructor(user: User): this(user.id, user.uname, user.email, user.authorityLevels)
}