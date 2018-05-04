package edu.ycp.cs482.iorcapi.factories


import edu.ycp.cs482.iorcapi.model.VersionQL
import edu.ycp.cs482.iorcapi.model.Version
import edu.ycp.cs482.iorcapi.model.attributes.Stat
import edu.ycp.cs482.iorcapi.model.attributes.StatQL
import edu.ycp.cs482.iorcapi.model.attributes.VersionInfo
import edu.ycp.cs482.iorcapi.model.authentication.*
import edu.ycp.cs482.iorcapi.repositories.StatRepository
import edu.ycp.cs482.iorcapi.repositories.VersionInfoRepository
import edu.ycp.cs482.iorcapi.repositories.VersionRepository
import graphql.ErrorType
import graphql.GraphQLException
import org.springframework.stereotype.Component

@Component
class VersionFactory(
        private val statRepository: StatRepository,
        private val versionInfoRepository: VersionInfoRepository,
        private val versionRepository: VersionRepository,
        private val authorizer: Authorizer
) {

    fun getVersionSkills(version: Version, context: User): VersionQL{
        authorizer.authorizeVersion(version, context, AuthorityMode.MODE_VIEW) ?: throw  GraphQLException("Forbidden")
        return VersionQL(version.version,
            statRepository.findByVersionAndSkill(version.version, true).map{StatQL(it)})
    }

    fun getVersionInfoByType(version: Version, type: String, context: User): VersionQL{
        authorizer.authorizeVersion(version, context, AuthorityMode.MODE_VIEW) ?: throw  GraphQLException("Forbidden")
        return VersionQL(version.version, listOf(),  versionInfoRepository.findByVersionAndType(version.version, type))
    }

    private fun getVersionStatList(version: Version): List<String>{
        val statList = statRepository.findByVersion(version.version)
        return statList.map { it.name }
    }

    //TODO: Do we need to check access here?
    fun checkStatsInVersion(mods: HashMap<String, Float>, version: Version): Boolean {
        val statsList = getVersionStatList(version)
        for((key, value) in mods) {
            if(!statsList.contains(key)){
                if(key != "*") {
                    return false
                }
            }
        }
        return true
    }

    fun addStatToVersion(key:String, name: String, description: String, version: Version, skill: Boolean, context: User): VersionQL {
        authorizer.authorizeVersion(version, context, AuthorityMode.MODE_EDIT) ?: throw  GraphQLException("Forbidden")
        val stat = Stat((key+version.version), key, name,  description, version.version, skill)
//        statRepository.findById("str" + version) ?: initializeVersion(version)
        statRepository.save(stat)
        return constructVersionSheet(version)
    }

    //TODO: update for ACL
    fun removeStatFromVersion(key:String, version: Version, context: User):String{
        val stat =statRepository.findById((key+version)) ?: throw GraphQLException("Stat does not exist with that id")
        authorizer.authorizeVersion(version, stat.version, context, AuthorityMode.MODE_EDIT) ?: throw GraphQLException("Forbidden")

        statRepository.delete(stat)
        return "Stat %s deleted from version".format(stat.id)
    }

    fun addInfoToVersion(name: String, type: String, value: String, version: Version, context: User): VersionQL {
        authorizer.authorizeVersion(version, context, AuthorityMode.MODE_EDIT) ?: throw  GraphQLException("Forbidden")
        val info = VersionInfo((name+version.version), version.version, name, type,  value)
//        versionInfoRepository.findById("currency" + version) ?: initializeVersion(version)
        versionInfoRepository.save(info)
        return constructVersionSheet(version)
    }

        //TODO update for ACL
    fun removeInfoFromVersion(id:String, version: Version, context: User):String{
            val info =versionInfoRepository.findById(id) ?: throw GraphQLException("Info does not exist with that id")
            authorizer.authorizeVersion(version, info.version, context, AuthorityMode.MODE_EDIT) ?: throw GraphQLException("Forbidden")
            versionInfoRepository.delete(info)
            return "Info %S deleted from version".format(info.id)
    }

    fun createVersion(version: String, context: User): VersionQL {
        //Checks version repo, creates version object and stores in db.
        if(versionRepository.findByVersion(version)  == null) { // if no current version with this name exists
            val versionObj = Version( version = version,
                    access =  listOf(AccessData(context.id, mapOf()),
                            AccessData("", mapOf(Pair(AuthorityLevel.ROLE_USER, AuthorityMode.MODE_VIEW)))

                    ))
            versionRepository.save(versionObj)
            initializeVersion(versionObj)
            return constructVersionSheet(versionObj)
        } else {
            throw GraphQLException("Version with this name already exists!")
        }
    }


    private fun initializeVersion(version: Version): VersionQL {
        statRepository.save(Stat("str"+version.version, "str", "Strength", "Strength", version.version, false))
        statRepository.save(Stat("con"+version.version, "con", "Constitution", "Constitution", version.version, false))
        statRepository.save(Stat("dex"+version.version, "dex", "Dexterity","Dexterity", version.version, false))
        statRepository.save(Stat("int"+version.version, "int", "Intelligence","Intelligence", version.version, false))
        statRepository.save(Stat("wis"+version.version, "wis", "Wisdom", "Wisdom", version.version, false))
        statRepository.save(Stat("cha"+version.version, "cha", "Charisma","Charisma", version.version, false))
        versionInfoRepository.save(VersionInfo("currency"+ version.version, version.version, "currency", "currency", "Replace this with the versions currency" ))
        return constructVersionSheet(version)
    }

    fun addStatModifiers(key : String, version: Version, mods: HashMap<String, Float>, context: User): StatQL {
        authorizer.authorizeVersion(version, context, AuthorityMode.MODE_EDIT) ?: throw  GraphQLException("Forbidden")
        val stat = statRepository.findById((key+version.version)) ?: throw GraphQLException("Stat does not exist with that id")

        stat.unionModifiers(mods)
        statRepository.save(stat) // this should write over the old one with the new parameters
        return StatQL(stat)
    }

    fun removeStatModifier(statKey : String, version: Version,  key: String, context: User): StatQL {
        authorizer.authorizeVersion(version, context, AuthorityMode.MODE_EDIT) ?: throw  GraphQLException("Forbidden")
        val stat = statRepository.findById((statKey+version.version)) ?: throw GraphQLException("Stat does not exist with that id")

        stat.removeModifier(key)
        statRepository.save(stat) // this should write over the old one with the new parameters
        return StatQL(stat)
    }

    fun getVersionSheet(version: Version, context: User): VersionQL{
        authorizer.authorizeVersion(version, context, AuthorityMode.MODE_VIEW) ?: throw GraphQLException("Forbidden")
        return constructVersionSheet(version)
    }

    fun addUserToVersion(user: User, version: Version, context: User): VersionQL {
        authorizer.authorizeVersion(version, context, AuthorityMode.MODE_EDIT) ?: throw GraphQLException("Forbidden")
        val dbVersion = versionRepository.findByVersion(version.version) ?: throw GraphQLException("Version Does not exist")
        val access = mutableListOf<AccessData>()
        access.addAll(dbVersion.access)
        access.add(AccessData(user.id, mapOf() ))
        val newVersion = Version(dbVersion.version,  access)
        versionRepository.save(newVersion) //writes over old db object with new user
        return constructVersionSheet(newVersion)
    }

    private fun constructVersionSheet(version: Version) : VersionQL {
        val versionStats = statRepository.findByVersion(version.version)
        val versionInfo = versionInfoRepository.findByVersion(version.version)

        val qlStats = mutableListOf<StatQL>()
        versionStats.mapTo(qlStats){StatQL(it)}

       return VersionQL(version.version, qlStats, versionInfo)
    }

    fun hydrateVersion(version: String): Version {
        return versionRepository.findByVersion(version) ?: throw GraphQLException("That version does not exist")
    }

}