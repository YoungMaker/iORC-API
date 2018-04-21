package edu.ycp.cs482.iorcapi.model

import edu.ycp.cs482.iorcapi.model.user.AccessData
import edu.ycp.cs482.iorcapi.model.user.AuthorityLevel
import edu.ycp.cs482.iorcapi.model.user.AuthorityMode

open class Accessible( //all db objects should extend this.
        val authority: AccessData =
                        AccessData(null, mapOf<AuthorityLevel, //default level is userers can view, admin can edit
                                AuthorityMode>(
                                Pair(AuthorityLevel.ROLE_USER, AuthorityMode.MODE_VIEW),
                                Pair(AuthorityLevel.ROLE_ADMIN, AuthorityMode.MODE_EDIT)))
)
{
}