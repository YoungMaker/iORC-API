package edu.ycp.cs482.iorcapi.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed

data class User(
        @Indexed
        val username: String, //IS THIS RIGHT??
        val password: String,
        val authorities: String
)