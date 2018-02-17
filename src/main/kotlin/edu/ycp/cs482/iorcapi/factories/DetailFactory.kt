package edu.ycp.cs482.iorcapi.factories

import edu.ycp.cs482.iorcapi.error.QueryException
import edu.ycp.cs482.iorcapi.model.Race
import edu.ycp.cs482.iorcapi.model.RaceQL
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
    fun createNewRace(name: String, description: String, version: String = "") : RaceQL {
        val race = Race(UUID.randomUUID().toString(), name = name, description = description, version = version)
        raceRepository.insert(race)
        return hydrateRace(race)
    }

    fun updateRace(id: String, name: String, description: String, version: String = "") : RaceQL {
        raceRepository.findById(id) ?: throw QueryException("Race does not exist with that id", ErrorType.DataFetchingException)

        val newRace = Race(id, name = name, description = description, version = version) // creates new one based on old one
        raceRepository.save(newRace) // this should write over the old one with the new parameters
        return hydrateRace(newRace)
    }

    fun addRaceModifiers(id : String, mods: HashMap<String, Int>): RaceQL {
       val race = raceRepository.findById(id) ?: throw QueryException("Race does not exist with that id", ErrorType.DataFetchingException)

        val finalMods = HashMap<String, Int>(race.modifiers)
        finalMods.putAll(mods)

        val newRace = Race(id,
                name = race.name,
                description = race.description,
                version = race.version,
                modifiers = finalMods) // creates new one based on old one with new modifer(s)
        raceRepository.save(newRace) // this should write over the old one with the new parameters
        return hydrateRace(newRace)
    }

    fun removeRaceModifier(id: String, key: String): RaceQL {
        val race = raceRepository.findById(id) ?: throw QueryException("Race does not exist with that id", ErrorType.DataFetchingException)

        val finalMods = HashMap<String, Int>(race.modifiers)
        finalMods.remove(key)

        val newRace = Race(id,
                name = race.name,
                description = race.description,
                version = race.version,
                modifiers = finalMods) // creates new one based on old one with new modifer(s)
        raceRepository.save(newRace) // this should write over the old one with the new parameters
        return hydrateRace(newRace)
    }

    fun getRaceById(id: String) : RaceQL{
        val race = raceRepository.findById(id) ?: throw throw QueryException("Race does not exist with that id", ErrorType.DataFetchingException)
        return hydrateRace(race)
    }

    fun getRacesByName(name: String) = hydrateRaces(raceRepository.findByName(name))

    fun getRacesByVersion(version: String) = hydrateRaces(raceRepository.findByVersion(version))


    fun hydrateRaces(races: List<Race>) : List<RaceQL> {
        val output = mutableListOf<RaceQL>()
        races.mapTo(output){hydrateRace(it)}
        return output
    }

    fun hydrateRace(race: Race): RaceQL {
        val outputList = mutableListOf<Modifier>()
        for((key, value) in race.modifiers) {
            outputList.add(Modifier(key, value))
        }
        return RaceQL(race.id, race.name, race.description, race.version, outputList)
    }


}