package edu.ycp.cs482.iorcapi.factories


import edu.ycp.cs482.iorcapi.model.Version
import edu.ycp.cs482.iorcapi.model.attributes.Stat
import edu.ycp.cs482.iorcapi.model.attributes.StatQL
import edu.ycp.cs482.iorcapi.model.attributes.VersionInfo
import edu.ycp.cs482.iorcapi.repositories.StatRepository
import edu.ycp.cs482.iorcapi.repositories.VersionInfoRepository
import graphql.ErrorType
import graphql.GraphQLException
import org.springframework.stereotype.Component

@Component
class VersionFactory(
        private val statRepository: StatRepository,
        private val versionInfoRepository: VersionInfoRepository
) {

    fun getVersionSkills(version: String): Version{
        return Version(version,
            statRepository.findByVersionAndSkill(version, true).map{StatQL(it)})
    }

    fun getVersionInfoByType(version: String, type: String): Version{
        return Version(version, listOf(),  versionInfoRepository.findByVersionAndType(version, type))
    }

    fun getVersionStatList(version: String): List<String>{
        val statList = statRepository.findByVersion(version)
        return statList.map { it.name }
    }

    fun checkStatsInVersion(mods: HashMap<String, Float>, version: String): Boolean {
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

    fun addStatToVersion(key:String, name: String, description: String, version: String, skill: Boolean): Version {
        val stat = Stat((key+version), key, name,  description, version, skill)
        statRepository.findById("str" + version) ?: initializeVersion(version)
        statRepository.save(stat)
        return constructVersionSheet(version)
    }

    fun addInfoToVersion(name: String, type: String, value: String, version: String): Version {
        val info = VersionInfo((name+version), version, name, type,  value)
        versionInfoRepository.findById("currency" + version) ?: initializeVersion(version)
        versionInfoRepository.save(info)
        return constructVersionSheet(version)
    }

    fun initializeVersion(version: String): Version {
        statRepository.save(Stat("str"+version, "str", "Strength", "Strength", version, false))
        statRepository.save(Stat("con"+version, "con", "Constitution", "Constitution", version, false))
        statRepository.save(Stat("dex"+version, "dex", "Dexterity","Dexterity", version, false))
        statRepository.save(Stat("int"+version, "int", "Intelligence","Intelligence", version, false))
        statRepository.save(Stat("wis"+version, "wis", "Wisdom", "Wisdom", version, false))
        statRepository.save(Stat("cha"+version, "cha", "Charisma","Charisma", version, false))
        versionInfoRepository.save(VersionInfo("currency"+ version, version, "currency", "currency", "Replace this with the versions currency" ))
        return constructVersionSheet(version)
    }

    fun addStatModifiers(key : String, version: String, mods: HashMap<String, Float>): StatQL {
        val stat = statRepository.findById((key+version)) ?: throw GraphQLException("Stat does not exist with that id")

        stat.unionModifiers(mods)
        statRepository.save(stat) // this should write over the old one with the new parameters
        return StatQL(stat)
    }

    fun removeStatModifier(statKey : String, version: String,  key: String): StatQL {
        val stat = statRepository.findById((statKey+version)) ?: throw GraphQLException("Stat does not exist with that id")

        stat.removeModifier(key)
        statRepository.save(stat) // this should write over the old one with the new parameters
        return StatQL(stat)
    }

    fun constructVersionSheet(version: String) : Version {
        val versionStats = statRepository.findByVersion(version)
        val versionInfo = versionInfoRepository.findByVersion(version)

        val qlStats = mutableListOf<StatQL>()
        versionStats.mapTo(qlStats){StatQL(it)}

       return Version(version, qlStats, versionInfo)
    }

}