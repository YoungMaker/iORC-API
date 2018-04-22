package edu.ycp.cs482.iorcapi.resolvers

import com.coxautodev.graphql.tools.GraphQLMutationResolver
import edu.ycp.cs482.iorcapi.factories.UserFactory
import org.springframework.stereotype.Component

@Component
class UserMutationResolver(
        private val userFactory: UserFactory
): GraphQLMutationResolver {

    fun createUser(email:String, name:String, password: String) = userFactory.createUserAccount(email, name, password)
    fun loginUser(email: String, password: String) = userFactory.loginUser(email, password)
}