package edu.ycp.cs482.iorcapi.model.user

import org.springframework.data.annotation.Id
import java.util.*

data  class User(
        @Id val id: String,
        val email: String,
        val authorityLevels: List<AuthorityLevel>,
        val passwordHash: ByteArray,
        val passwordSalt: ByteArray
)