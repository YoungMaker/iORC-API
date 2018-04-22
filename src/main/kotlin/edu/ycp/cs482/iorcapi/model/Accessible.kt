package edu.ycp.cs482.iorcapi.model

import edu.ycp.cs482.iorcapi.model.authentication.AccessData
import edu.ycp.cs482.iorcapi.model.authentication.AuthorityLevel
import edu.ycp.cs482.iorcapi.model.authentication.AuthorityMode

open class Accessible( //all db objects should extend this.
        val authority: AccessData =
                        AccessData("", mapOf( //default level is user view, admin edit for version objects
                                Pair(AuthorityLevel.ROLE_USER, AuthorityMode.MODE_VIEW),
                                Pair(AuthorityLevel.ROLE_ADMIN, AuthorityMode.MODE_EDIT)))
)
{
}