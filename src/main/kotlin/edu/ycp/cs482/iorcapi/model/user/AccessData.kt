package edu.ycp.cs482.iorcapi.model.user

data class AccessData( //object attached to all objects for access control.
        val owner: User?,
        val controlList: Map<AuthorityLevel, AuthorityMode> // Eg: LEVEL_USERS, MODE_READ
)