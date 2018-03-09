package edu.ycp.cs482.iorcapi.resolvers

import com.coxautodev.graphql.tools.GraphQLMutationResolver
import edu.ycp.cs482.iorcapi.factories.VersionFactory
import org.springframework.stereotype.Component

@Component
class VersionMutationResolver(
        private val versionFactory: VersionFactory
): GraphQLMutationResolver{
    fun addStatToVersion(key:String, name: String, description: String, version: String, skill: Boolean)
            = versionFactory.addStatToVersion(key.toLowerCase(), name, description, version.toLowerCase(), skill)

    fun addStatBaseCalcModifier(statKey: String, version: String, key: String, value: Float)
            = versionFactory.addStatModifiers(statKey.toLowerCase(), version.toLowerCase(),
                                                    hashMapOf(Pair(key.toLowerCase(), value)))

    fun removeStatBaseCalcModifier(statKey: String, version: String, key: String)
            = versionFactory.removeStatModifier(statKey.toLowerCase(), version.toLowerCase(), key)
}