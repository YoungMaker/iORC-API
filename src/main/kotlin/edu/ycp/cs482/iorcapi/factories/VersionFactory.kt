package edu.ycp.cs482.iorcapi.factories


import edu.ycp.cs482.iorcapi.error.QueryException
import edu.ycp.cs482.iorcapi.model.Version
import edu.ycp.cs482.iorcapi.model.attributes.Stat
import edu.ycp.cs482.iorcapi.model.attributes.StatQL
import edu.ycp.cs482.iorcapi.repositories.StatRepository
import graphql.ErrorType
import org.springframework.stereotype.Component

@Component
class VersionFactory(
        private val statRepository: StatRepository
) {

    fun getVersionSkills(version: String): Version{
        return Version(version,
            statRepository.findByVersionAndSkill(version, true).map{StatQL(it)})
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

    fun addStatToVersion(name: String, description: String, version: String, skill: Boolean): Version {
        val stat = Stat((name+version), name, description, version, skill)
        statRepository.findById("str" + version) ?: initializeVersion(version)
        statRepository.save(stat)
        return constructVersionSheet(version)
    }

    fun initializeVersion(version: String){
        statRepository.save(Stat("str"+version, "str", "Strength", version, false))
        statRepository.save(Stat("con"+version, "con", "Constitution", version, false))
        statRepository.save(Stat("dex"+version, "dex", "Dexterity", version, false))
        statRepository.save(Stat("int"+version, "int", "Intelligence", version, false))
        statRepository.save(Stat("wis"+version, "wis", "Wisdom", version, false))
        statRepository.save(Stat("cha"+version, "cha", "Charisma", version, false))
    }

    fun addStatModifiers(name : String, version: String, mods: HashMap<String, Float>): StatQL {
        val stat = statRepository.findById((name+version)) ?: throw QueryException("Stat does not exist with that id", ErrorType.DataFetchingException)

        stat.unionModifiers(mods)
        statRepository.save(stat) // this should write over the old one with the new parameters
        return StatQL(stat)
    }

    fun removeStatModifier(name : String, version: String,  key: String): StatQL {
        val stat = statRepository.findById((name+version)) ?: throw QueryException("Stat does not exist with that id", ErrorType.DataFetchingException)

        stat.removeModifier(key)
        statRepository.save(stat) // this should write over the old one with the new parameters
        return StatQL(stat)
    }

    fun constructVersionSheet(version: String) : Version {
        val versionStats = statRepository.findByVersion(version)

        val qlStats = mutableListOf<StatQL>()
        versionStats.mapTo(qlStats){StatQL(it)}

       return Version(version, qlStats)
    }

}