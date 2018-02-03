package edu.ycp.cs482.iorcapi.resolvers

import com.coxautodev.graphql.tools.GraphQLMutationResolver
import edu.ycp.cs482.iorcapi.model.Race
import edu.ycp.cs482.iorcapi.model.attributes.Ability
import edu.ycp.cs482.iorcapi.repositories.CharacterRepository
import org.springframework.stereotype.Component
import edu.ycp.cs482.iorcapi.model.Character


@Component
class CharacterMutationResolver(
        private val characterRepo: CharacterRepository
) : GraphQLMutationResolver {
    fun createCharacter(name: String, abilityPoints: Ability, race: Race ) : Character {
        val char = Character(4, name = name, abilityPoints = abilityPoints, race = race)
        characterRepo.save(char)
        return char
    }
}