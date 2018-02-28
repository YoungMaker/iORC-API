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

    fun addStatToVersion(id: String, description: String, version: String): Version {
        val stat = Stat(id, description, version)
        statRepository.save(stat)
        return constructVersionSheet(version)
    }

    fun addStatModifiers(id : String, mods: HashMap<String, Float>): StatQL {
        val stat = statRepository.findById(id) ?: throw QueryException("Stat does not exist with that id", ErrorType.DataFetchingException)

        stat.unionModifiers(mods)
        statRepository.save(stat) // this should write over the old one with the new parameters
        return StatQL(stat)
    }

    fun removeStatModifier(id: String, key: String): StatQL {
        val stat = statRepository.findById(id) ?: throw QueryException("Stat does not exist with that id", ErrorType.DataFetchingException)

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