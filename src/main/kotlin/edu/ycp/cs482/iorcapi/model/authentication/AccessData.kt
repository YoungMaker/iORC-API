package edu.ycp.cs482.iorcapi.model.authentication

data class AccessData( //object attached to all objects for access control.
        val owner: String,
        val controlList: Map<AuthorityLevel, AuthorityMode> // Eg: LEVEL_USERS, MODE_READ
)