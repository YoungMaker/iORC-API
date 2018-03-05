package edu.ycp.cs482.iorcapi.resolvers

import com.coxautodev.graphql.tools.GraphQLQueryResolver
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class QueryResolver : GraphQLQueryResolver {

    @Value("\${spring.application.name}")
    private val appName: String = ""

    fun version() = appName

}