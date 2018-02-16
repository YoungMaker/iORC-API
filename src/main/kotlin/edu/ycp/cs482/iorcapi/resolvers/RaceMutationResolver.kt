package edu.ycp.cs482.iorcapi.resolvers

import com.coxautodev.graphql.tools.GraphQLMutationResolver
import edu.ycp.cs482.iorcapi.factories.DetailFactory
import edu.ycp.cs482.iorcapi.model.Race
import edu.ycp.cs482.iorcapi.repositories.RaceRepository
import org.springframework.stereotype.Component

@Component
class RaceMutationResolver(
        private val detailFactory: DetailFactory
) : GraphQLMutationResolver {
    fun createRace(name: String, version: String, description: String )
            : Race = detailFactory.createNewRace(name, version, description)
    fun updateRace(id: String, name: String, version: String, description: String )
            : Race = detailFactory.updateRace(id, name, version, description)
}