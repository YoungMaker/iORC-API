package edu.ycp.cs482.iorcapi.model.user

enum class AuthorityLevel {
    ROLE_USER,
    ROLE_OWNER,
    ROLE_ADMIN
}

enum class AuthorityMode {
    MODE_VIEW, //can view object
    MODE_EDIT // can edit object
}