package edu.ycp.cs482.iorcapi.factories

import edu.ycp.cs482.iorcapi.error.QueryException
import edu.ycp.cs482.iorcapi.model.*
import edu.ycp.cs482.iorcapi.model.attributes.Modifier
import edu.ycp.cs482.iorcapi.repositories.ClassRepository
import edu.ycp.cs482.iorcapi.repositories.RaceRepository
import graphql.ErrorType
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class DetailFactory(
    private val raceRepository: RaceRepository,
    private val classRepository: ClassRepository,
    private val modTools: ModTools
){

    /** Race functionality **/

    //TODO: validation?
    fun createNewRace(name: String, description: String, version: String = "") : RaceQL {
        val race = Race(UUID.randomUUID().toString(), name = name, description = description, version = version)
        raceRepository.save(race)
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

        val newRace = Race(id,
                name = race.name,
                description = race.description,
                version = race.version,
                modifiers =  modTools.unionModifiers(race.modifiers, mods))
        // creates new one based on old one with new modifer(s)
        raceRepository.save(newRace) // this should write over the old one with the new parameters
        return hydrateRace(newRace)
    }

    fun removeRaceModifier(id: String, key: String): RaceQL {
        val race = raceRepository.findById(id) ?: throw QueryException("Race does not exist with that id", ErrorType.DataFetchingException)

        val newRace = Race(id,
                name = race.name,
                description = race.description,
                version = race.version,
                modifiers = modTools.removeModifier(race.modifiers, key)) // creates new one based on old one with new modifer(s)
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

    fun hydrateRace(race: Race): RaceQL =
            RaceQL(race.id, race.name,
                    race.description,
                    race.version,
                    modTools.convertToModifiers(race.modifiers))

    /** Class functionality: **/

    fun createNewClass(name: String, role: String,  version: String, description: String): ClassQL {
        val rpgClass = ClassRpg(id = UUID.randomUUID().toString(),
                name = name,
                role = role,
                version = version,
                description =  description)

        classRepository.insert(rpgClass)
        return hydrateClass(rpgClass)
    }

    fun updateClass(id: String, name: String, role: String, version: String, description: String): ClassQL {
        val oldClass = classRepository.findById(id) ?: throw QueryException("Class does not exist with that id", ErrorType.DataFetchingException)
        val rpgClass = ClassRpg(id = id,
                name = name,
                role = role,
                version = version,
                description =  description)

        classRepository.save(rpgClass)
        return hydrateClass(rpgClass)
    }

    fun addClassModifiers(id: String, mods: HashMap<String, Int>): ClassQL {
        val rpgClass = classRepository.findById(id) ?: throw QueryException("Class does not exist with that id", ErrorType.DataFetchingException)

        val newClass = ClassRpg(id,
                name = rpgClass.name,
                role = rpgClass.role,
                description =rpgClass.description,
                version = rpgClass.version,
                modifiers = modTools.unionModifiers(rpgClass.modifiers, mods)) // creates new one based on old one with new modifer(s)

        classRepository.save(newClass) // this should write over the old one with the new parameters
        return hydrateClass(newClass)
    }


    fun removeClassModifier(id: String, key: String ): ClassQL {
        val rpgClass = classRepository.findById(id) ?: throw QueryException("Class does not exist with that id", ErrorType.DataFetchingException)

        val newClass = ClassRpg(id,
                name = rpgClass.name,
                role = rpgClass.role,
                description =rpgClass.description,
                version = rpgClass.version,
                modifiers = modTools.removeModifier(rpgClass.modifiers, key)) // creates new one based on old one with new modifer(s)

        classRepository.save(newClass) // this should write over the old one with the new parameters
        return hydrateClass(newClass)
    }

    fun getClassById(id: String) : ClassQL{
        val rpgClass = classRepository.findById(id) ?: throw throw QueryException("Race does not exist with that id", ErrorType.DataFetchingException)
        return hydrateClass(rpgClass)
    }

    fun getClassesByName(name: String) = hydrateClasses(classRepository.findByName(name))
    fun getClassesByVersion(version: String) = hydrateClasses(classRepository.findByVersion(version))


    fun hydrateClasses(classes: List<ClassRpg>) : List<ClassQL> {
        val output = mutableListOf<ClassQL>()
        classes.mapTo(output){hydrateClass(it)}
        return output
    }

    fun hydrateClass(rpgClass: ClassRpg): ClassQL {
        val outputList = mutableListOf<Modifier>()
        for((key, value) in rpgClass.modifiers) {
            outputList.add(Modifier(key, value))
        }
        return ClassQL(id = rpgClass.id,
                name = rpgClass.name,
                role = rpgClass.role,
                description = rpgClass.description,
                version = rpgClass.version,
                modifiers = outputList)
    }


    /** additional helper method **/




}