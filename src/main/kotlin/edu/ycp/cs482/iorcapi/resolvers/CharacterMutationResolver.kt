package edu.ycp.cs482.iorcapi.resolvers

import com.coxautodev.graphql.tools.GraphQLMutationResolver
import edu.ycp.cs482.iorcapi.model.Race
import edu.ycp.cs482.iorcapi.model.attributes.Ability
import edu.ycp.cs482.iorcapi.repositories.CharacterRepository
import org.springframework.stereotype.Component
import edu.ycp.cs482.iorcapi.model.Character
import java.util.*


@Component
class CharacterMutationResolver(
        private val characterRepo: CharacterRepository
) : GraphQLMutationResolver {
    //TODO: Move this to the controller? Or factory? Do not create objects in the resolver
    //TODO: Remove
    fun createCharacter(name: String, abilityPoints: Ability, race: Race ) : Character {
        val char = Character(UUID.randomUUID().toString(), name = name, abilityPoints = abilityPoints, race = race)
        characterRepo.insert(char)
        return char
    }
}