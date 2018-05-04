package edu.ycp.cs482.iorcapi.model.authentication

import edu.ycp.cs482.iorcapi.model.Accessible
import edu.ycp.cs482.iorcapi.model.Version
import org.springframework.stereotype.Component

@Component
class Authorizer {
    //for characters/userdata
    fun authorizeObject(obj: Accessible, context: User, mode: AuthorityMode): AccessData? {
        if(obj.authority.owner == context.id) {return obj.authority} //if we're the owner, short circuit. we have full control
        else {
            for(level in context.authorityLevels){ //for all levels in the users access level
                if(obj.authority.controlList.containsKey(level)){ //if that access level is entered in the control list of the object
                    if(obj.authority.controlList[level] == mode ||
                            (obj.authority.controlList[level] == AuthorityMode.MODE_EDIT && mode == AuthorityMode.MODE_VIEW) ) { //and the control mode associated is the mode we're doing
                        return obj.authority //we can access this item
                    }
                }
            }
        }
        return null //we cannot access this item
    }

    fun authorizeObjects(objects: List<Accessible>, context: User, mode: AuthorityMode): List<AccessData>? {
        val retList = mutableListOf<AccessData>()
        for(obj in objects) {
            val access = authorizeObject(obj, context, mode)
            access ?: return null //if access was null short circuit we can't access it all
            retList.add(access) //otherwise add it to the list of access data
        }
        return retList
    }

    //for version items
    fun authorizeVersion(version: Version, context: User, mode: AuthorityMode) : AccessData?
    {
        for (access in version.access) {//if we're the owner, short circuit. we have full control
            if (access.owner == context.id) {return access }
        }
        //if we're a user we can view. This does not allow for private versions rn.
        if(context.authorityLevels.contains(AuthorityLevel.ROLE_USER) && mode == AuthorityMode.MODE_VIEW) {
            return AccessData("", mapOf(Pair(AuthorityLevel.ROLE_USER, AuthorityMode.MODE_VIEW)))
        }
        if(context.authorityLevels.contains(AuthorityLevel.ROLE_ADMIN)) { //we're admin- short circuit
            return AccessData("", mapOf(Pair(AuthorityLevel.ROLE_ADMIN, AuthorityMode.MODE_EDIT)))
        }

        return null
    }

    fun authorizeVersion(version: Version, objVersion: String, context: User, mode: AuthorityMode): AccessData?{
        if(version.version != objVersion){return null} //if the versions do not match- for access by id/search
        return authorizeVersion(version, context, mode)
    }
}