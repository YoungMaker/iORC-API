package edu.ycp.cs482.iorcapi.factories

import edu.ycp.cs482.iorcapi.error.QueryException
import edu.ycp.cs482.iorcapi.model.Character
import edu.ycp.cs482.iorcapi.model.Race
import edu.ycp.cs482.iorcapi.model.attributes.Ability
import edu.ycp.cs482.iorcapi.repositories.CharacterRepository
import graphql.ErrorType
import org.springframework.stereotype.Component
import java.util.*

@Component
class CharacterFactory(

    private val characterRepo: CharacterRepository
)  {
    //TODO: Security! Access control checks! Associate with users
    fun createNewCharacter(name: String, abilityPoints: Ability, race: Race) : Character {
        val char = Character(UUID.randomUUID().toString(), name = name, abilityPoints = abilityPoints, race = race)
        characterRepo.insert(char)
        return char
    }

    fun updateName(id: String, name: String) : Character {
        val char = characterRepo.findById(id) ?: throw QueryException("Character does not exist with that id", ErrorType.DataFetchingException)

        val newChar = Character(id, name, char.abilityPoints, char.race) // creates new one based on old one
        characterRepo.save(newChar) // this should write over the old one with the new name
        return newChar
    }
}