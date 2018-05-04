package edu.ycp.cs482.iorcapi.resolvers

import com.coxautodev.graphql.tools.GraphQLQueryResolver
import edu.ycp.cs482.iorcapi.factories.UserFactory
import edu.ycp.cs482.iorcapi.model.authentication.Context
import org.springframework.stereotype.Component

@Component
class UserQueryResolver(
        private val userFactory: UserFactory
) : GraphQLQueryResolver {

    fun getUserInfo(email: String, context: Context) = userFactory.getUserInfo(email, userFactory.hydrateUser(context))

}