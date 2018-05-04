package edu.ycp.cs482.iorcapi.error

import graphql.GraphQLError
import graphql.servlet.GenericGraphQLError
import graphql.servlet.GraphQLErrorHandler
import org.springframework.stereotype.Component

@Component
class CustomGraphQlErrorHandler: GraphQLErrorHandler {

    override fun processErrors(errors: MutableList<GraphQLError>?): MutableList<GraphQLError> {
        val errorList = mutableListOf<GraphQLError>()
        errors!!.mapTo(errorList) { GenericGraphQLError(it.message) }
        return errorList
    }

}