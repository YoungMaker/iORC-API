package edu.ycp.cs482.iorcapi.resolvers

import com.coxautodev.graphql.tools.GraphQLMutationResolver
import edu.ycp.cs482.iorcapi.factories.DetailFactory
import edu.ycp.cs482.iorcapi.model.RaceQL
import org.springframework.stereotype.Component

@Component
class RaceMutationResolver(
        private val detailFactory: DetailFactory
) : GraphQLMutationResolver {
    fun createRace(name: String, version: String, description: String )
             = detailFactory.createNewRace(name, version, description)
    fun updateRace(id: String, name: String, version: String, description: String )
            = detailFactory.updateRace(id, name, version, description)
//    fun addRaceModifier(id: String, key: String, value: Int) =
//            detailFactory.addRaceModifiers(id, hashMapOf(Pair(key.toLowerCase(), value)))
//    fun removeRaceModifier(id: String, key: String) =
//            detailFactory.removeRaceModifier(id, key.toLowerCase())
}