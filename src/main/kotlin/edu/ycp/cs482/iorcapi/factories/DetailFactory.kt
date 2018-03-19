package edu.ycp.cs482.iorcapi.factories

import edu.ycp.cs482.iorcapi.model.*
import edu.ycp.cs482.iorcapi.model.attributes.Modifiable
import edu.ycp.cs482.iorcapi.model.attributes.Modifier
import edu.ycp.cs482.iorcapi.repositories.ClassRepository
import edu.ycp.cs482.iorcapi.repositories.RaceRepository
import graphql.ErrorType
import graphql.GraphQLException
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class DetailFactory(
    private val raceRepository: RaceRepository,
    private val classRepository: ClassRepository,
    private val versionFactory: VersionFactory
){

    /** Race functionality **/

    //TODO: validation?
    fun createNewRace(name: String, description: String, version: String = "") : RaceQL {
        val race = Race(UUID.randomUUID().toString(), name = name, description = description, version = version)
        raceRepository.save(race) //should this be insert??
        return RaceQL(race)
    }

    fun updateRace(id: String, name: String, description: String, version: String = "") : RaceQL {
        raceRepository.findById(id) ?: throw GraphQLException("Race does not exist with that id")

        val newRace = Race(id, name = name, description = description, version = version) // creates new one based on old one
        raceRepository.save(newRace) // this should write over the old one with the new parameters
        return RaceQL(newRace)
    }

    fun addRaceModifiers(id : String, mods: HashMap<String, Float>): RaceQL {
       val race = raceRepository.findById(id) ?: throw GraphQLException("Race does not exist with that id")

        if(!versionFactory.checkStatsInVersion(mods, race.version)){
            throw GraphQLException("This Modifier is not in the version sheet!")
        }

        race.unionModifiers(mods)
        raceRepository.save(race) // this should write over the old one with the new parameters
        return RaceQL(race)
    }

    fun removeRaceModifier(id: String, key: String): RaceQL {
        val race = raceRepository.findById(id) ?: throw GraphQLException("Race does not exist with that id")

        race.removeModifier(key)
        raceRepository.save(race) // this should write over the old one with the new parameters
        return RaceQL(race)
    }

    fun getRaceById(id: String) : RaceQL{
        val race = raceRepository.findById(id) ?: throw throw GraphQLException("Race does not exist with that id")
        return RaceQL(race)
    }

    fun getRacesByName(name: String) = hydrateRaces(raceRepository.findByName(name))

    fun getRacesByVersion(version: String) = hydrateRaces(raceRepository.findByVersion(version))


    fun hydrateRaces(races: List<Race>) : List<RaceQL> {
        val output = mutableListOf<RaceQL>()
        races.mapTo(output){RaceQL(it)}
        return output
    }

    //depreciated
//    fun hydrateRace(race: Race): RaceQL =
//            RaceQL(race.id, race.name,
//                    race.description,
//                    race.version,
//                    modTools.convertToModifiers(race.modifiers))

    /** Class functionality: **/

    fun createNewClass(name: String, role: String,  version: String, description: String): ClassQL {
        val rpgClass = ClassRpg(id = UUID.randomUUID().toString(),
                name = name,
                role = role,
                version = version,
                description =  description)

        classRepository.save(rpgClass) //should this be insert??
        return ClassQL(rpgClass)
    }

    fun updateClass(id: String, name: String, role: String, version: String, description: String): ClassQL {
        classRepository.findById(id) ?: throw GraphQLException("Class does not exist with that id")
        val rpgClass = ClassRpg(id = id,
                name = name,
                role = role,
                version = version,
                description =  description)

        classRepository.save(rpgClass)
        return ClassQL(rpgClass)
    }

    fun addClassModifiers(id: String, mods: HashMap<String, Float>): ClassQL {
        val rpgClass = classRepository.findById(id) ?: throw GraphQLException("Class does not exist with that id")

        if(!versionFactory.checkStatsInVersion(mods, rpgClass.version)){
            throw GraphQLException("This Modifier is not in the version sheet!")
        }

        rpgClass.unionModifiers(mods)

        classRepository.save(rpgClass) // this should write over the old one with the new parameters
        return ClassQL(rpgClass)
    }


    fun removeClassModifier(id: String, key: String ): ClassQL {
        val rpgClass = classRepository.findById(id) ?: throw GraphQLException("Class does not exist with that id")

        rpgClass.removeModifier(key)

        classRepository.save(rpgClass) // this should write over the old one with the new parameters
        return ClassQL(rpgClass)
    }

    fun getClassById(id: String) : ClassQL{
        val rpgClass = classRepository.findById(id) ?: throw throw GraphQLException("Race does not exist with that id")
        return ClassQL(rpgClass)
    }

    fun getClassesByName(name: String) = hydrateClasses(classRepository.findByName(name))
    fun getClassesByVersion(version: String) = hydrateClasses(classRepository.findByVersion(version))


    fun hydrateClasses(classes: List<ClassRpg>) : List<ClassQL> {
        val output = mutableListOf<ClassQL>()
        classes.mapTo(output){ClassQL(it)}
        return output
    }

    //depreciated
//    fun hydrateClass(rpgClass: ClassRpg): ClassQL {
//
//        return ClassQL(id = rpgClass.id,
//                name = rpgClass.name,
//                role = rpgClass.role,
//                description = rpgClass.description,
//                version = rpgClass.version,
//                modifiers = modTools.convertToModifiers(rpgClass.modifiers))
//    }


    /** additional helper method **/




}