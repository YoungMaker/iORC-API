package edu.ycp.cs482.iorcapi.factories

import edu.ycp.cs482.iorcapi.error.QueryException
import edu.ycp.cs482.iorcapi.model.Character
import edu.ycp.cs482.iorcapi.model.CharacterQL
import edu.ycp.cs482.iorcapi.model.Race
import edu.ycp.cs482.iorcapi.model.attributes.Ability
import edu.ycp.cs482.iorcapi.repositories.CharacterRepository
import edu.ycp.cs482.iorcapi.repositories.RaceRepository
import graphql.ErrorType
import org.springframework.stereotype.Component
import java.util.*

@Component
class CharacterFactory(

    private val characterRepo: CharacterRepository,
    private val raceRepository: RaceRepository
)  {
    //TODO: Security! Access control checks! Associate with users
//    fun createNewCharacter(name: String, abilityPoints: Ability, race: Race) : Character {
//        val char = Character(UUID.randomUUID().toString(), name = name, abilityPoints = abilityPoints, race = race)
//        characterRepo.insert(char)
//        return char
//    }

    fun createNewCharacter(name: String, abilityPoints: Ability, raceid: String) : CharacterQL {
        val race = raceRepository.findById(raceid) ?: throw QueryException("Race does not exist with that id", ErrorType.DataFetchingException)

        val char = Character(UUID.randomUUID().toString(), name = name, abilityPoints = abilityPoints, raceid = race.id)
        characterRepo.insert(char)

        return CharacterQL(char.id, char.name, char.abilityPoints, race)
    }

    fun updateName(id: String, name: String) : CharacterQL {
        val char = characterRepo.findById(id) ?: throw QueryException("Character does not exist with that id", ErrorType.DataFetchingException)

        val newChar = Character(id, name, char.abilityPoints, char.raceid) // creates new one based on old one
        characterRepo.save(newChar) // this should write over the old one with the new name
        return hydrateChar(newChar)
    }

    fun getCharacterById(id:String) : CharacterQL {
        val char = characterRepo.findById(id) ?: throw QueryException("Character does not exist with that id", ErrorType.DataFetchingException)


        return hydrateChar(char)
    }

    fun getCharactersByName(name: String) : List<CharacterQL> {
        val chars = characterRepo.findByName(name)
        val output = mutableListOf<CharacterQL>()

        chars.mapTo(output) { hydrateChar(it) }
        return output
    }

    //converts referential persistence object to graphQL full representation
    fun hydrateChar(char: Character) : CharacterQL {
        val race = raceRepository.findById(char.raceid) ?:
            throw QueryException("Race does not exist with that id", ErrorType.DataFetchingException)

        return CharacterQL(char.id, char.name, char.abilityPoints, race)
    }
}