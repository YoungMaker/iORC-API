package edu.ycp.cs482.iorcapi.resolvers

import com.coxautodev.graphql.tools.GraphQLMutationResolver
import edu.ycp.cs482.iorcapi.factories.UserFactory
import edu.ycp.cs482.iorcapi.model.authentication.Context
import org.springframework.stereotype.Component

@Component
class UserMutationResolver(
        private val userFactory: UserFactory
): GraphQLMutationResolver {

    fun createUser(email:String, name:String, password: String) = userFactory.createUserAccount(email, name, password)
    fun loginUser(email: String, password: String) = userFactory.loginUser(email, password)
    fun createAdmin(email: String, name: String, password: String, context: Context)
            = userFactory.createAdminAccount(email, name, password, userFactory.hydrateUser(context))
}