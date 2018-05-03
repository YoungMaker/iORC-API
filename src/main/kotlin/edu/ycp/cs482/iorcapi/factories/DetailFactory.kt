package edu.ycp.cs482.iorcapi.factories

import edu.ycp.cs482.iorcapi.model.*
import edu.ycp.cs482.iorcapi.repositories.ClassRepository
import edu.ycp.cs482.iorcapi.repositories.RaceRepository
import graphql.GraphQLException
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class DetailFactory(
    private val raceRepository: RaceRepository,
    private val classRepository: ClassRepository,
    private val versionFactory: VersionFactory,
    private val itemFactory: ItemFactory
){

    /** Race functionality **/

    //TODO: validation?
    fun createNewRace(name: String, description: String, version: String = "") : RaceQL {
        val race = Race(UUID.randomUUID().toString(), name = name, description = description, version = version,
                feats= listOf())
        raceRepository.save(race) //should this be insert??
        return hydrateRace(race)
    }

    fun updateRace(id: String, name: String, description: String, version: String = "") : RaceQL {
        raceRepository.findById(id) ?: throw GraphQLException("Race does not exist with that id")

        val newRace = Race(id, name = name, description = description, version = version,
                feats= listOf()) // creates new one based on old one
        raceRepository.save(newRace) // this should write over the old one with the new parameters
        return hydrateRace(newRace)
    }

    fun addRaceModifiers(id : String, mods: HashMap<String, Float>): RaceQL {
       val race = raceRepository.findById(id) ?: throw GraphQLException("Race does not exist with that id")

        if(!versionFactory.checkStatsInVersion(mods, race.version)){
            throw GraphQLException("This Modifier is not in the version sheet!")
        }

        race.unionModifiers(mods)
        raceRepository.save(race) // this should write over the old one with the new parameters
        return hydrateRace(race)
    }

    fun addRaceFeats(id:String, feats:List<String>):RaceQL{
        val race = raceRepository.findById(id) ?: throw GraphQLException("Race does not exist with that id")
        // db items are immutable, re-create a new race obj and save over the old
        for(feat in feats){
            itemFactory.getItemById(feat) // this will check if they exist and throw an error if it does not
        }
        val newFeats = mutableListOf<String>()
        newFeats.addAll(race.feats)
        newFeats.addAll(feats)
        val newRace = Race(
                id= race.id,
                name = race.name,
                description = race.description,
                modifiers = race.modifiers,
                version = race.version,
                type = race.type,
                feats = newFeats)


        raceRepository.save(newRace)
        return hydrateRace(newRace)
    }

    fun removeRaceFeats(id:String, feats:List<String>):RaceQL{
        val race = raceRepository.findById(id) ?: throw GraphQLException("Race does not exist with that id")
        // db items are immutable, re-create a new race obj and save over the old
        if(!race.feats.containsAll(feats)) { throw GraphQLException("Race does not contain that feat!")}
        val newFeats = mutableListOf<String>()
        newFeats.addAll(race.feats)
        newFeats.removeAll(feats)
        val newRace = Race(
                id= race.id,
                name = race.name,
                description = race.description,
                modifiers = race.modifiers,
                version = race.version,
                type = race.type,
                feats = newFeats)


        raceRepository.save(newRace)
        return hydrateRace(newRace)
    }

    fun removeRaceModifier(id: String, key: String): RaceQL {
        val race = raceRepository.findById(id) ?: throw GraphQLException("Race does not exist with that id")

        race.removeModifier(key)
        raceRepository.save(race) // this should write over the old one with the new parameters
        return hydrateRace(race)
    }

    fun getRaceById(id: String) : RaceQL{
        val race = raceRepository.findById(id) ?: throw throw GraphQLException("Race does not exist with that id")
        return hydrateRace(race)
    }

    fun getRacesByName(name: String) = hydrateRaces(raceRepository.findByName(name))

    fun getRacesByVersion(version: String) = hydrateRaces(raceRepository.findByVersion(version))


    fun hydrateRaces(races: List<Race>) : List<RaceQL> {
        val output = mutableListOf<RaceQL>()
        races.mapTo(output){hydrateRace(it)}
        return output
    }

    fun hydrateRace(race: Race):RaceQL{
        val featsList = hydrateFeats(race.feats)
        return RaceQL(race, featsList)
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
                description =  description,
                feats= listOf())

        classRepository.save(rpgClass) //should this be insert??
        return hydrateClass(rpgClass)
    }

    fun updateClass(id: String, name: String, role: String, version: String, description: String): ClassQL {
        classRepository.findById(id) ?: throw GraphQLException("Class does not exist with that id")
        val rpgClass = ClassRpg(id = id,
                name = name,
                role = role,
                version = version,
                description =  description,
                feats= listOf())

        classRepository.save(rpgClass)
        return hydrateClass(rpgClass)
    }

    fun addClassModifiers(id: String, mods: HashMap<String, Float>): ClassQL {
        val rpgClass = classRepository.findById(id) ?: throw GraphQLException("Class does not exist with that id")

        if(!versionFactory.checkStatsInVersion(mods, rpgClass.version)){
            throw GraphQLException("This Modifier is not in the version sheet!")
        }

        rpgClass.unionModifiers(mods)

        classRepository.save(rpgClass) // this should write over the old one with the new parameters
        return hydrateClass(rpgClass)
    }

    fun addClassFeats(id:String, feats:List<String>):ClassQL{
        val classObj = classRepository.findById(id) ?: throw GraphQLException("Class does not exist with that id")
        // db items are immutable, re-create a new class obj and save over the old
        for(feat in feats){
            itemFactory.getItemById(feat) // this will check if they exist and throw an error if it does not
        }
        val newFeats = mutableListOf<String>()
        newFeats.addAll(classObj.feats)
        newFeats.addAll(feats)
        val newClassObj = ClassRpg(
                id= classObj.id,
                name = classObj.name,
                role = classObj.role,
                description = classObj.description,
                modifiers = classObj.modifiers,
                version = classObj.version,
                type = classObj.type,
                feats = newFeats)


        classRepository.save(newClassObj)
        return hydrateClass(newClassObj)
    }

    fun removeClassFeats(id:String, feats:List<String>):ClassQL{
        val classObj = classRepository.findById(id) ?: throw GraphQLException("Class does not exist with that id")
        // db items are immutable, re-create a new class obj and save over the old

        if(!classObj.feats.containsAll(feats)) {throw GraphQLException("Class does not contain that feat!")}
        val newFeats = mutableListOf<String>()
        newFeats.addAll(classObj.feats)
        newFeats.removeAll(feats)
        val newClassObj = ClassRpg(
                id= classObj.id,
                name = classObj.name,
                role = classObj.role,
                description = classObj.description,
                modifiers = classObj.modifiers,
                version = classObj.version,
                type = classObj.type,
                feats = newFeats)


        classRepository.save(newClassObj)
        return hydrateClass(newClassObj)
    }


    fun removeClassModifier(id: String, key: String ): ClassQL {
        val rpgClass = classRepository.findById(id) ?: throw GraphQLException("Class does not exist with that id")

        rpgClass.removeModifier(key)

        classRepository.save(rpgClass) // this should write over the old one with the new parameters
        return hydrateClass(rpgClass)
    }

    fun getClassById(id: String) : ClassQL{
        val rpgClass = classRepository.findById(id) ?: throw throw GraphQLException("Race does not exist with that id")
        return hydrateClass(rpgClass)
    }

    fun getClassesByName(name: String) = hydrateClasses(classRepository.findByName(name))
    fun getClassesByVersion(version: String) = hydrateClasses(classRepository.findByVersion(version))


    fun hydrateClasses(classes: List<ClassRpg>) : List<ClassQL> {
        val output = mutableListOf<ClassQL>()
        classes.mapTo(output){hydrateClass(it)}
        return output
    }

    fun hydrateClass(classRPG: ClassRpg):ClassQL{
        val featsList = hydrateFeats(classRPG.feats)
        return ClassQL(classRPG, featsList)
    }

    fun hydrateFeats(featIDs: List<String>): List<ItemQL>{
        val outputList = mutableListOf<ItemQL>()
        for(featID in featIDs){
            try {
                outputList.add(itemFactory.getItemById(featID))
            }
            catch(e: GraphQLException){
                outputList.add(ItemQL(id= "ERR ITEM", name= "ITEM ERROR",
                        description = "" + e.message,
                        modifiers = listOf(), price= -1f, version = "ERR"))
            }
        }
        return outputList
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