package edu.ycp.cs482.iorcapi.resolvers

import com.coxautodev.graphql.tools.GraphQLMutationResolver
import edu.ycp.cs482.iorcapi.factories.CharacterFactory
import edu.ycp.cs482.iorcapi.factories.UserFactory
import edu.ycp.cs482.iorcapi.model.authentication.Context
import org.springframework.stereotype.Component

@Component
class UserMutationResolver(
        private val userFactory: UserFactory,
        private val characterFactory: CharacterFactory
): GraphQLMutationResolver {

    fun createUser(email:String, name:String, password: String) = userFactory.createUserAccount(email, name, password)
    fun loginUser(email: String, password: String) = userFactory.loginUser(email, password)
    fun createAdmin(email: String, name: String, password: String, context: Context)
            = userFactory.createAdminAccount(email, name, password, userFactory.hydrateUser(context))

    fun deleteAccount(email: String, password: String): String  { //NOTE that this does not remove them from "owner" of any version items. People still using those should still have them
        val context = userFactory.loginUser(email, password)
        val user = userFactory.hydrateUser(context)
        characterFactory.purgeUsersCharacters(user) //removes all characters
        userFactory.deleteUser(email, user) //removes user account data
        return "User: " + user.id + " was deleted. Thank you for using iOrc. Goodbye!"
    }

    fun banAccount(id: String, context: Context) =
            userFactory.banUserAccount(id, userFactory.hydrateUser(context))

    fun unbanAccount(id: String, context: Context) =
            userFactory.unbanUserAccount(id, userFactory.hydrateUser(context))

    fun elevateUserAccount(id: String, context: Context) =
            userFactory.elevateUserAccount(id, userFactory.hydrateUser(context))

    fun updatePassword(email: String, password: String, newPassword: String)=
            userFactory.updateUserPassword(email, password, newPassword)

    fun logoutUser(email: String, password: String) =
            userFactory.logout(email, password)

    fun logout(context: Context) =
            userFactory.deregisterToken(context)
}