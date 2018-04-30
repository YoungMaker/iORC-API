package edu.ycp.cs482.iorcapi.factories


import edu.ycp.cs482.iorcapi.model.VersionQL
import edu.ycp.cs482.iorcapi.model.Version
import edu.ycp.cs482.iorcapi.model.attributes.Stat
import edu.ycp.cs482.iorcapi.model.attributes.StatQL
import edu.ycp.cs482.iorcapi.model.attributes.VersionInfo
import edu.ycp.cs482.iorcapi.model.authentication.AccessData
import edu.ycp.cs482.iorcapi.model.authentication.AuthorityLevel
import edu.ycp.cs482.iorcapi.model.authentication.AuthorityMode
import edu.ycp.cs482.iorcapi.model.authentication.User
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
        private val versionRepository: VersionRepository
) {

    fun getVersionSkills(version: Version): VersionQL{
        return VersionQL(version.version,
            statRepository.findByVersionAndSkill(version.version, true).map{StatQL(it)})
    }

    fun getVersionInfoByType(version: Version, type: String): VersionQL{
        return VersionQL(version.version, listOf(),  versionInfoRepository.findByVersionAndType(version.version, type))
    }

    fun getVersionStatList(version: Version): List<String>{
        val statList = statRepository.findByVersion(version.version)
        return statList.map { it.name }
    }

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

    fun addStatToVersion(key:String, name: String, description: String, version: Version, skill: Boolean): VersionQL {
        val stat = Stat((key+version.version), key, name,  description, version.version, skill)
//        statRepository.findById("str" + version) ?: initializeVersion(version)
        statRepository.save(stat)
        return constructVersionSheet(version.version)
    }

    fun addInfoToVersion(name: String, type: String, value: String, version: Version): VersionQL {
        val info = VersionInfo((name+version.version), version.version, name, type,  value)
//        versionInfoRepository.findById("currency" + version) ?: initializeVersion(version)
        versionInfoRepository.save(info)
        return constructVersionSheet(version.version)
    }

    fun createVersion(version: String, context: User): VersionQL {
        //TODO: Check version repo, create version object and store in db.
        if(versionRepository.findByVersion(version)  == null) { // if no current version with this name exists
            val versionObj = Version( version = version,
                    access =  listOf(AccessData(context.id, mapOf()),
                            AccessData("", mapOf(Pair(AuthorityLevel.ROLE_USER, AuthorityMode.MODE_VIEW)))

                    ))
            versionRepository.save(versionObj)
            initializeVersion(version)
            return constructVersionSheet(version)
        } else {
            throw GraphQLException("Version with this name already exists!")
        }
    }


    private fun initializeVersion(version: String): VersionQL {
        statRepository.save(Stat("str"+version, "str", "Strength", "Strength", version, false))
        statRepository.save(Stat("con"+version, "con", "Constitution", "Constitution", version, false))
        statRepository.save(Stat("dex"+version, "dex", "Dexterity","Dexterity", version, false))
        statRepository.save(Stat("int"+version, "int", "Intelligence","Intelligence", version, false))
        statRepository.save(Stat("wis"+version, "wis", "Wisdom", "Wisdom", version, false))
        statRepository.save(Stat("cha"+version, "cha", "Charisma","Charisma", version, false))
        versionInfoRepository.save(VersionInfo("currency"+ version, version, "currency", "currency", "Replace this with the versions currency" ))
        return constructVersionSheet(version)
    }

    fun addStatModifiers(key : String, version: Version, mods: HashMap<String, Float>): StatQL {
        val stat = statRepository.findById((key+version)) ?: throw GraphQLException("Stat does not exist with that id")

        stat.unionModifiers(mods)
        statRepository.save(stat) // this should write over the old one with the new parameters
        return StatQL(stat)
    }

    fun removeStatModifier(statKey : String, version: Version,  key: String): StatQL {
        val stat = statRepository.findById((statKey+version)) ?: throw GraphQLException("Stat does not exist with that id")

        stat.removeModifier(key)
        statRepository.save(stat) // this should write over the old one with the new parameters
        return StatQL(stat)
    }

    fun constructVersionSheet(version: String) : VersionQL {
        hydrateVersion(version) //will error if version does not exist
        val versionStats = statRepository.findByVersion(version)
        val versionInfo = versionInfoRepository.findByVersion(version)

        val qlStats = mutableListOf<StatQL>()
        versionStats.mapTo(qlStats){StatQL(it)}

       return VersionQL(version, qlStats, versionInfo)
    }

    fun hydrateVersion(version: String): Version {
        return versionRepository.findByVersion(version) ?: throw GraphQLException("That version does not exist")
    }

}