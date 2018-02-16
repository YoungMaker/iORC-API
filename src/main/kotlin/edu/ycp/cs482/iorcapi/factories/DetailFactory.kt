package edu.ycp.cs482.iorcapi.factories

import edu.ycp.cs482.iorcapi.error.QueryException
import edu.ycp.cs482.iorcapi.model.Race
import edu.ycp.cs482.iorcapi.model.attributes.Modifier
import edu.ycp.cs482.iorcapi.repositories.RaceRepository
import graphql.ErrorType
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class DetailFactory(
    private val raceRepository: RaceRepository
){
    //TODO: validation?
    fun createNewRace(name: String, description: String, version: String = "") : Race {
        val race = Race(UUID.randomUUID().toString(), name = name, description = description, version = version)
        raceRepository.insert(race)
        return race
    }

    fun updateRace(id: String, name: String, description: String, version: String = "") : Race {
        raceRepository.findById(id) ?: throw QueryException("Race does not exist with that id", ErrorType.DataFetchingException)

        val newRace = Race(id, name = name, description = description, version = version) // creates new one based on old one
        raceRepository.save(newRace) // this should write over the old one with the new parameters
        return newRace
    }

    fun addRaceModifiers(id : String, mods: List<Modifier>): Race {
       val race = raceRepository.findById(id) ?: throw QueryException("Race does not exist with that id", ErrorType.DataFetchingException)

        val newRace = Race(id,
                name = race.name,
                description = race.description,
                version = race.version,
                modifiers = (race.modifiers + mods)) // creates new one based on old one with new modifer(s)
        raceRepository.save(newRace) // this should write over the old one with the new parameters
        return newRace
    }

    fun getRaceById(id: String) = raceRepository.findById(id)
    fun getRacesByName(name: String) = raceRepository.findByName(name)
    fun getRacesByVersion(version: String) = raceRepository.findByVersion(version)

}