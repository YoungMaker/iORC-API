package edu.ycp.cs482.iorcapi.factories

import edu.ycp.cs482.iorcapi.model.*
import edu.ycp.cs482.iorcapi.model.attributes.Modifiable
import edu.ycp.cs482.iorcapi.model.attributes.Modifier
import edu.ycp.cs482.iorcapi.model.authentication.AuthorityMode
import edu.ycp.cs482.iorcapi.model.authentication.Authorizer
import edu.ycp.cs482.iorcapi.model.authentication.User
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
    private val versionFactory: VersionFactory,
    private val authorizer: Authorizer
){

    /** Race functionality **/

    //TODO: validation?
    fun createNewRace(name: String, description: String, version: Version, context: User) : RaceQL {
        authorizer.authorizeVersion(version, context, AuthorityMode.MODE_EDIT) ?: throw GraphQLException("Forbidden")
        val race = Race(UUID.randomUUID().toString(), name = name, description = description, version = version.version)
        raceRepository.save(race) //should this be insert??
        return RaceQL(race)
    }

    fun updateRace(id: String, name: String, description: String, version: Version, context: User) : RaceQL {
        val oldrace = raceRepository.findById(id) ?: throw GraphQLException("Race does not exist with that id")
        authorizer.authorizeVersion(version, oldrace.version, context, AuthorityMode.MODE_EDIT) ?: throw GraphQLException("Forbidden")
        val newRace = Race(id, name = name, description = description, version = version.version) // creates new one based on old one
        raceRepository.save(newRace) // this should write over the old one with the new parameters
        return RaceQL(newRace)
    }

    fun addRaceModifiers(id : String, mods: HashMap<String, Float>, version: Version, context: User): RaceQL {
        val race = raceRepository.findById(id) ?: throw GraphQLException("Race does not exist with that id")
        authorizer.authorizeVersion(version, race.version, context, AuthorityMode.MODE_EDIT) ?: throw GraphQLException("Forbidden")

        if(!versionFactory.checkStatsInVersion(mods, versionFactory.hydrateVersion(race.version))){
            throw GraphQLException("This Modifier is not in the version sheet!")
        }

        race.unionModifiers(mods)
        raceRepository.save(race) // this should write over the old one with the new parameters
        return RaceQL(race)
    }

    fun removeRaceModifier(id: String, key: String, version: Version, context: User): RaceQL {
        val race = raceRepository.findById(id) ?: throw GraphQLException("Race does not exist with that id")
        authorizer.authorizeVersion(version, race.version, context, AuthorityMode.MODE_EDIT) ?: throw GraphQLException("Forbidden")

        race.removeModifier(key)
        raceRepository.save(race) // this should write over the old one with the new parameters
        return RaceQL(race)
    }

    fun getRaceById(id: String, version: Version, context: User) : RaceQL{
        val race = raceRepository.findById(id) ?: throw throw GraphQLException("Race does not exist with that id")
        authorizer.authorizeVersion(version, race.version, context, AuthorityMode.MODE_VIEW) ?: throw GraphQLException("Forbidden")
        return RaceQL(race)
    }

    fun getRacesByName(name: String, version: Version, context: User): List<RaceQL> {
        authorizer.authorizeVersion(version, context, AuthorityMode.MODE_VIEW) ?: throw GraphQLException("Forbidden")
        return hydrateRaces(raceRepository.findByNameAndVersion(name, version.version))
    }

    fun getRacesByVersion(version: Version, context: User): List<RaceQL> {
        authorizer.authorizeVersion(version, context, AuthorityMode.MODE_VIEW) ?: throw GraphQLException("Forbidden")
        return hydrateRaces(raceRepository.findByVersion(version.version))
    }


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

    fun createNewClass(name: String, role: String,  description: String, version: Version, context: User): ClassQL {
        authorizer.authorizeVersion(version, context, AuthorityMode.MODE_EDIT) ?: throw GraphQLException("Forbidden")
        val rpgClass = ClassRpg(id = UUID.randomUUID().toString(),
                name = name,
                role = role,
                version = version.version,
                description =  description)

        classRepository.save(rpgClass) //should this be insert??
        return ClassQL(rpgClass)
    }

    fun updateClass(id: String, name: String, role: String, description: String, version: Version, context: User): ClassQL {

        val oldClass = classRepository.findById(id) ?: throw GraphQLException("Class does not exist with that id")
        authorizer.authorizeVersion(version, oldClass.version, context, AuthorityMode.MODE_VIEW) ?: throw GraphQLException("Forbidden")
        val rpgClass = ClassRpg(id = id,
                name = name,
                role = role,
                version = version.version,
                description =  description)

        classRepository.save(rpgClass)
        return ClassQL(rpgClass)
    }

    fun addClassModifiers(id: String, mods: HashMap<String, Float>, version: Version, context: User): ClassQL {
        val rpgClass = classRepository.findById(id) ?: throw GraphQLException("Class does not exist with that id")
        authorizer.authorizeVersion(version, rpgClass.version, context, AuthorityMode.MODE_EDIT) ?: throw GraphQLException("Forbidden")
        if(!versionFactory.checkStatsInVersion(mods, versionFactory.hydrateVersion(rpgClass.version))){
            throw GraphQLException("This Modifier is not in the version sheet!")
        }

        rpgClass.unionModifiers(mods)

        classRepository.save(rpgClass) // this should write over the old one with the new parameters
        return ClassQL(rpgClass)
    }


    fun removeClassModifier(id: String, key: String, version: Version, context: User): ClassQL {
        val rpgClass = classRepository.findById(id) ?: throw GraphQLException("Class does not exist with that id")
        authorizer.authorizeVersion(version, rpgClass.version, context, AuthorityMode.MODE_EDIT) ?: throw GraphQLException("Forbidden")
        rpgClass.removeModifier(key)

        classRepository.save(rpgClass) // this should write over the old one with the new parameters
        return ClassQL(rpgClass)
    }

    fun getClassById(id: String, version: Version, context: User) : ClassQL{
        val rpgClass = classRepository.findById(id) ?: throw throw GraphQLException("Class does not exist with that id")
        authorizer.authorizeVersion(version, rpgClass.version, context, AuthorityMode.MODE_VIEW) ?: throw GraphQLException("Forbidden")
        return ClassQL(rpgClass)
    }

    fun getClassesByName(name: String, version: Version, context: User): List<ClassQL> {
        authorizer.authorizeVersion(version, context, AuthorityMode.MODE_VIEW) ?: throw GraphQLException("Forbidden")
        return hydrateClasses(classRepository.findByNameAndVersion(name, version.version))
    }
    fun getClassesByVersion(version: Version, context: User): List<ClassQL> {
        authorizer.authorizeVersion(version, context, AuthorityMode.MODE_VIEW) ?: throw GraphQLException("Forbidden")
        return hydrateClasses(classRepository.findByVersion(version.version))
    }


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






}