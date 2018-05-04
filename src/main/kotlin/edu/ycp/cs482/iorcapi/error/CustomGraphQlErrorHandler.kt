package edu.ycp.cs482.iorcapi.error

import graphql.GraphQLError
import graphql.servlet.GenericGraphQLError
import graphql.servlet.GraphQLErrorHandler
import org.springframework.stereotype.Component

@Component
class CustomGraphQlErrorHandler: GraphQLErrorHandler {

    override fun processErrors(errors: MutableList<GraphQLError>?): MutableList<GraphQLError> {
        val errorList = mutableListOf<GraphQLError>()
        for(error in errors!!){
            errorList.add(GenericGraphQLError(error.message))
        }
        return errorList
    }

}