package edu.ycp.cs482.iorcapi.resolvers

import com.coxautodev.graphql.tools.GraphQLMutationResolver
import edu.ycp.cs482.iorcapi.factories.VersionFactory
import org.springframework.stereotype.Component

@Component
class VersionMutationResolver(
        private val versionFactory: VersionFactory
): GraphQLMutationResolver{

    fun initalizeVersion(version: String)
        = versionFactory.initializeVersion(version)

    fun addInfoToVersion(name: String, version: String, type: String, value: String)
        = versionFactory.addInfoToVersion(name, type, value, version)

    fun removeInfoFromVersion(id:String) = versionFactory.removeInfoFromVersion(id)

    fun addStatToVersion(key:String, name: String, description: String, version: String, skill: Boolean)
            = versionFactory.addStatToVersion(key.toLowerCase(), name, description, version.toLowerCase(), skill)

    fun removeStatFromVersion(key:String, version:String)
            = versionFactory.removeStatFromVersion(key.toLowerCase(), version.toLowerCase())

    fun addStatBaseCalcModifier(statKey: String, version: String, key: String, value: Float)
            = versionFactory.addStatModifiers(statKey.toLowerCase(), version.toLowerCase(),
                                                    hashMapOf(Pair(key.toLowerCase(), value)))

    fun removeStatBaseCalcModifier(statKey: String, version: String, key: String)
            = versionFactory.removeStatModifier(statKey.toLowerCase(), version.toLowerCase(), key)
}