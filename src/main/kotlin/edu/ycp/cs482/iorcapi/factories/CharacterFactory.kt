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
    private val detailFactory: DetailFactory
)  {
    //TODO: Security! Access control checks! Associate with users
//    fun createNewCharacter(name: String, abilityPoints: Ability, race: Race) : Character {
//        val char = Character(UUID.randomUUID().toString(), name = name, abilityPoints = abilityPoints, race = race)
//        characterRepo.insert(char)
//        return char
//    }
    //TODO: check if UUID already used. See issue #16
    fun createNewCharacter(name: String, abilityPoints: Ability, raceid: String, classid: String, version: String) : CharacterQL {
        val race = detailFactory.getRaceById(raceid) //this is to check if it exists, this will throw a query exception
        val classql = detailFactory.getClassById(classid)

        val char = Character(UUID.randomUUID().toString(),
                name = name,
                abilityPoints = abilityPoints,
                raceid = race.id,
                classid = classql.id,
                version = version)
        characterRepo.save(char) //should this be insert?

        return hydrateChar(char)
    }

    fun updateCharacter(id: String, name: String, abilityPoints: Ability, raceid: String, classid: String) : CharacterQL {
        val char = characterRepo.findById(id) ?: throw QueryException("Character does not exist with that id", ErrorType.DataFetchingException)
        val race = detailFactory.getRaceById(raceid) //this is to check if it exists, this will throw a query exception
        val classql = detailFactory.getClassById(classid)

        val charNew = Character(id,
                name = name,
                abilityPoints = abilityPoints,
                raceid = race.id,
                classid = classql.id,
                version = char.version)
        characterRepo.save(charNew) //should this be insert?

        return hydrateChar(charNew)
    }

    //depreciated.
    fun updateName(id: String, name: String) : CharacterQL {
        val char = characterRepo.findById(id) ?: throw QueryException("Character does not exist with that id", ErrorType.DataFetchingException)

        val newChar = Character(id, name, char.abilityPoints, char.raceid, char.classid, char.version) // creates new one based on old one
        characterRepo.save(newChar) // this should write over the old one with the new name
        return hydrateChar(newChar)
    }

    fun getCharacterById(id:String) : CharacterQL {
        val char = characterRepo.findById(id) ?: throw QueryException("Character does not exist with that id", ErrorType.DataFetchingException)
        return hydrateChar(char)
    }

    fun getCharactersByName(name: String) = hydrateChars(characterRepo.findByName(name))

    fun getCharactersByVersion(version: String) = hydrateChars(characterRepo.findByVersion(version))

    ///maps a list to an output lits of CharacterQL graphQL objects
    fun hydrateChars(chars: List<Character>) : List<CharacterQL> {
        val output = mutableListOf<CharacterQL>()

        chars.mapTo(output) { hydrateChar(it) }
        return output
    }

    //converts referential persistence object to graphQL full representation
    fun hydrateChar(char: Character) : CharacterQL {
        val race = detailFactory.getRaceById(char.raceid)
        val classql = detailFactory.getClassById(char.classid)
        return CharacterQL(char.id, char.version, char.name, char.abilityPoints, race, classql)
    }



}