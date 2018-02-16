package edu.ycp.cs482.iorcapi.resolvers

import com.coxautodev.graphql.tools.GraphQLQueryResolver
import edu.ycp.cs482.iorcapi.factories.DetailFactory
import org.springframework.stereotype.Component

@Component
class RaceQueryResolver(
        private val detailFactory: DetailFactory
) : GraphQLQueryResolver {
    fun getRaceById(id: String) = detailFactory.getRaceById(id)
    fun getRacesByName(name: String) = detailFactory.getRacesByName(name)
    fun getAllRaces() = detailFactory.getAllRaces()
}