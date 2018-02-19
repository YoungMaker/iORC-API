package edu.ycp.cs482.iorcapi.resolvers

import com.coxautodev.graphql.tools.GraphQLMutationResolver
import edu.ycp.cs482.iorcapi.factories.CharacterFactory
import edu.ycp.cs482.iorcapi.model.Race
import edu.ycp.cs482.iorcapi.model.attributes.Ability
import edu.ycp.cs482.iorcapi.repositories.CharacterRepository
import org.springframework.stereotype.Component
import edu.ycp.cs482.iorcapi.model.Character
import edu.ycp.cs482.iorcapi.model.CharacterQL
import java.util.*


@Component
class CharacterMutationResolver(
        private val characterFactory: CharacterFactory
) : GraphQLMutationResolver {
    //TODO: add validation so that the scalar values submitted with the AbilityPoints cannot be negative.

    fun createCharacter(name: String, abilityPoints: Ability, raceid: String, classid: String )
            = characterFactory.createNewCharacter(name, abilityPoints, raceid, classid)
    fun updateName(id: String, name: String) = characterFactory.updateName(id, name)
}