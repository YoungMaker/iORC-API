package edu.ycp.cs482.iorcapi.factories

import edu.ycp.cs482.iorcapi.model.*
import edu.ycp.cs482.iorcapi.model.attributes.Modifiable
import edu.ycp.cs482.iorcapi.model.attributes.Modifier
import edu.ycp.cs482.iorcapi.model.authentication.AuthorityLevel
import edu.ycp.cs482.iorcapi.model.authentication.AuthorityMode
import edu.ycp.cs482.iorcapi.model.authentication.Authorizer
import edu.ycp.cs482.iorcapi.model.authentication.User
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
    private val authorizer: Authorizer,
    private val itemFactory: ItemFactory
){

    /** Race functionality **/

    //TODO: validation?
    fun createNewRace(name: String, description: String, version: Version, context: User) : RaceQL {
        authorizer.authorizeVersion(version, context, AuthorityMode.MODE_EDIT) ?: throw GraphQLException("Forbidden")
        //create ID
        val raceId = (name.trim()+version.version)
        //check for duplicate IDs
        if(raceRepository.exists(raceId)){
            throw GraphQLException("Item already exists in repository")
        }

        val race = Race(raceId, name = name, description = description, version = version.version,
                feats= listOf())

        raceRepository.save(race) //should this be insert??
        return hydrateRace(race, context)
    }

    fun updateRace(id: String, name: String, description: String, version: Version, context: User) : RaceQL {
        val oldrace = raceRepository.findById(id) ?: throw GraphQLException("Race does not exist with that id")
        authorizer.authorizeVersion(version, oldrace.version, context, AuthorityMode.MODE_EDIT) ?: throw GraphQLException("Forbidden")
        val newRace = Race(id, name = name, description = description, version = version.version,
                feats= listOf()) // creates new one based on old one
        raceRepository.save(newRace) // this should write over the old one with the new parameters
        return hydrateRace(newRace, context)
    }

    //transform old race ids to new system (can be modified for other purposes later)
    //this is only meant to be run when needed and should not be able to be called normally
    //thus only admins have authority now
    //TODO: Add ACL
    fun reformatRaces(version: Version, context: User):List<RaceQL>{
        if(!context.authorityLevels.contains(AuthorityLevel.ROLE_ADMIN)){throw GraphQLException("Forbidden")}
        //get races from repo
        val races = raceRepository.findByVersion(version.version)

        for(race in races){
            val raceID = (race.name.trim()+race.version.trim())
            if(race.id != raceID){
                val newRace = Race(raceID, name=race.name,
                        description=race.description,
                        version=race.version,
                        feats = race.feats)
                //add the new race object to repo
                raceRepository.save(newRace)
                addRaceModifiers(raceID, race.modifiers as HashMap<String, Float>, version, context)

                //delete old race object from repo
                raceRepository.delete(race.id)
            }
        }
        return getRacesByVersion(version, context)
    }

    fun deleteRace(id: String, version: Version, context: User):String{
        if(!raceRepository.exists(id)){
            return "Race %S has does not exist".format(id)
        }
        val race = raceRepository.findById(id)
        authorizer.authorizeVersion(version, race!!.version, context, AuthorityMode.MODE_EDIT )
        raceRepository.delete(id)
        return "Race %S has been deleted".format(id)
    }

    fun addRaceModifiers(id : String, mods: HashMap<String, Float>, version: Version, context: User): RaceQL {
        val race = raceRepository.findById(id) ?: throw GraphQLException("Race does not exist with that id")
        authorizer.authorizeVersion(version, race.version, context, AuthorityMode.MODE_EDIT) ?: throw GraphQLException("Forbidden")

        if(!versionFactory.checkStatsInVersion(mods, versionFactory.hydrateVersion(race.version))){
            throw GraphQLException("This Modifier is not in the version sheet!")
        }

        race.unionModifiers(mods)
        raceRepository.save(race) // this should write over the old one with the new parameters
        return hydrateRace(race,context)
    }

    //TODO: add ACL
    fun addRaceFeats(id:String, feats:List<String>, version: Version, context: User):RaceQL{
        val race = raceRepository.findById(id) ?: throw GraphQLException("Race does not exist with that id")
        authorizer.authorizeVersion(version, race.version, context, AuthorityMode.MODE_EDIT) ?: throw GraphQLException("Forbidden")
        // db items are immutable, re-create a new race obj and save over the old
        for(feat in feats){
            itemFactory.getItemById(feat, version, context) // this will check if they exist and throw an error if it does not
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
        return hydrateRace(newRace, context)
    }

    fun removeRaceFeats(id:String, feats:List<String>, version: Version, context: User):RaceQL{
        val race = raceRepository.findById(id) ?: throw GraphQLException("Race does not exist with that id")
        authorizer.authorizeVersion(version, race.version, context, AuthorityMode.MODE_EDIT) ?: throw GraphQLException("Forbidden")
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
        return hydrateRace(newRace, context)
    }

    fun removeRaceModifier(id: String, key: String, version: Version, context: User): RaceQL {
        val race = raceRepository.findById(id) ?: throw GraphQLException("Race does not exist with that id")
        authorizer.authorizeVersion(version, race.version, context, AuthorityMode.MODE_EDIT) ?: throw GraphQLException("Forbidden")

        race.removeModifier(key)
        raceRepository.save(race) // this should write over the old one with the new parameters
        return hydrateRace(race, context)
    }

    fun getRaceById(id: String, version: Version, context: User) : RaceQL{
        val race = raceRepository.findById(id) ?: throw throw GraphQLException("Race does not exist with that id")
        authorizer.authorizeVersion(version, race.version, context, AuthorityMode.MODE_VIEW) ?: throw GraphQLException("Forbidden")
        return hydrateRace(race, context)
    }

    fun getRacesByName(name: String, version: Version, context: User): List<RaceQL> {
        authorizer.authorizeVersion(version, context, AuthorityMode.MODE_VIEW) ?: throw GraphQLException("Forbidden")
        return hydrateRaces(raceRepository.findByNameAndVersion(name, version.version), context)
    }

    fun getRacesByVersion(version: Version, context: User): List<RaceQL> {
        authorizer.authorizeVersion(version, context, AuthorityMode.MODE_VIEW) ?: throw GraphQLException("Forbidden")
        return hydrateRaces(raceRepository.findByVersion(version.version), context)
    }


    fun hydrateRaces(races: List<Race>, context: User) : List<RaceQL> {
        val output = mutableListOf<RaceQL>()
        races.mapTo(output){hydrateRace(it, context)}
        return output
    }

    fun hydrateRace(race: Race, context: User):RaceQL{
        val featsList = hydrateFeats(race.feats, versionFactory.hydrateVersion(race.version), context)
        return RaceQL(race, featsList)
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
        //create ID
        val classID = (name.trim() + version.version)

        //check for duplicate based on ID
        if(raceRepository.exists(classID)){
            throw GraphQLException("Item already exists in repository")
        }

        val rpgClass = ClassRpg(id = classID,
                name = name,
                role = role,
                version = version.version,
                description =  description,
                feats= listOf())

        classRepository.save(rpgClass) //should this be insert??
        return hydrateClass(rpgClass, context)
    }

    fun updateClass(id: String, name: String, role: String, description: String, version: Version, context: User): ClassQL {

        val oldClass = classRepository.findById(id) ?: throw GraphQLException("Class does not exist with that id")
        authorizer.authorizeVersion(version, oldClass.version, context, AuthorityMode.MODE_EDIT) ?: throw GraphQLException("Forbidden")
        val rpgClass = ClassRpg(id = id,
                name = name,
                role = role,
                version = version.version,
                description =  description,
                feats= listOf())

        classRepository.save(rpgClass)
        return hydrateClass(rpgClass, context)
    }

    //TODO: Add ACL!
    //used to change old class ids to new id format (can be modified for other purposes later)
    //only admins can do this
    fun reformatClasses(version: Version, context: User):List<ClassQL>{
        if(!context.authorityLevels.contains(AuthorityLevel.ROLE_ADMIN)) {throw GraphQLException("Forbidden")}
        //get classes from repo
        val classes = classRepository.findByVersion(version.version)

        for(classObj in classes){
            //check for classes with old method of ID creation
            val classID = (classObj.name.trim()+classObj.version.trim())
            if(classObj.id != classID){
                val newClass = ClassRpg(classID, name=classObj.name,
                        description=classObj.description,
                        version=classObj.version,
                        role=classObj.role,
                        type=classObj.type,
                        feats = classObj.feats)
                //add the new race object to repo
                classRepository.save(newClass)
                addClassModifiers(classID, classObj.modifiers as HashMap<String, Float>, version, context)

                //delete old race object from repo
                classRepository.delete(classObj.id)
            }
        }
        return getClassesByVersion(version, context)
    }

    fun deleteClass(id: String, version: Version, context: User):String{
        if(!classRepository.exists(id)){
            return "Class %S does not exist".format(id)
        }
        classRepository.delete(id)
        return "Class %S has been deleted".format(id)
    }

    fun addClassModifiers(id: String, mods: HashMap<String, Float>, version: Version, context: User): ClassQL {
        val rpgClass = classRepository.findById(id) ?: throw GraphQLException("Class does not exist with that id")
        authorizer.authorizeVersion(version, rpgClass.version, context, AuthorityMode.MODE_EDIT) ?: throw GraphQLException("Forbidden")
        if(!versionFactory.checkStatsInVersion(mods, versionFactory.hydrateVersion(rpgClass.version))){
            throw GraphQLException("This Modifier is not in the version sheet! %s".format(mods))
        }

        rpgClass.unionModifiers(mods)

        classRepository.save(rpgClass) // this should write over the old one with the new parameters
        return hydrateClass(rpgClass, context)
    }

    fun addClassFeats(id:String, feats:List<String>, version: Version, context: User):ClassQL{
        val classObj = classRepository.findById(id) ?: throw GraphQLException("Class does not exist with that id")
        // db items are immutable, re-create a new class obj and save over the old
        for(feat in feats){
            itemFactory.getItemById(feat, version, context) // this will check if they exist and throw an error if it does not
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
        return hydrateClass(newClassObj, context)
    }

    fun removeClassFeats(id:String, feats:List<String>, version: Version, context: User):ClassQL{
        val classObj = classRepository.findById(id) ?: throw GraphQLException("Class does not exist with that id")
        // db items are immutable, re-create a new class obj and save over the old
        authorizer.authorizeVersion(version, classObj.version, context, AuthorityMode.MODE_EDIT)
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
        return hydrateClass(newClassObj, context)
    }


    fun removeClassModifier(id: String, key: String, version: Version, context: User): ClassQL {
        val rpgClass = classRepository.findById(id) ?: throw GraphQLException("Class does not exist with that id")
        authorizer.authorizeVersion(version, rpgClass.version, context, AuthorityMode.MODE_EDIT) ?: throw GraphQLException("Forbidden")
        rpgClass.removeModifier(key)

        classRepository.save(rpgClass) // this should write over the old one with the new parameters
        return hydrateClass(rpgClass, context)
    }

    fun getClassById(id: String, version: Version, context: User) : ClassQL{
        val rpgClass = classRepository.findById(id) ?: throw throw GraphQLException("Class does not exist with that id")
        authorizer.authorizeVersion(version, rpgClass.version, context, AuthorityMode.MODE_VIEW) ?: throw GraphQLException("Forbidden")
        return hydrateClass(rpgClass, context)
    }

    fun getClassesByName(name: String, version: Version, context: User): List<ClassQL> {
        authorizer.authorizeVersion(version, context, AuthorityMode.MODE_VIEW) ?: throw GraphQLException("Forbidden")
        return hydrateClasses(classRepository.findByNameAndVersion(name, version.version), context)
    }
    fun getClassesByVersion(version: Version, context: User): List<ClassQL> {
        authorizer.authorizeVersion(version, context, AuthorityMode.MODE_VIEW) ?: throw GraphQLException("Forbidden")
        return hydrateClasses(classRepository.findByVersion(version.version), context)
    }


    fun hydrateClasses(classes: List<ClassRpg>, context: User) : List<ClassQL> {
        val output = mutableListOf<ClassQL>()
        classes.mapTo(output){hydrateClass(it, context)}
        return output
    }

    fun hydrateClass(classRPG: ClassRpg, context: User):ClassQL{
        val featsList = hydrateFeats(classRPG.feats, versionFactory.hydrateVersion(classRPG.version), context)
        return ClassQL(classRPG, featsList)
    }


    //TODO: Add ACL!
    fun hydrateFeats(featIDs: List<String>, version: Version, context: User): List<ItemQL>{
        val outputList = mutableListOf<ItemQL>()
        for(featID in featIDs){
            try {
                outputList.add(itemFactory.getItemById(featID,version, context))
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






}