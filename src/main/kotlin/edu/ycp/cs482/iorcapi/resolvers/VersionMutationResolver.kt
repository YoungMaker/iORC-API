package edu.ycp.cs482.iorcapi.resolvers

import com.coxautodev.graphql.tools.GraphQLMutationResolver
import edu.ycp.cs482.iorcapi.factories.VersionFactory
import org.springframework.stereotype.Component

@Component
class VersionMutationResolver(
        private val versionFactory: VersionFactory
): GraphQLMutationResolver{
    fun addStatToVersion(name: String, description: String, version: String, skill: Boolean)
            = versionFactory.addStatToVersion(name.toLowerCase(), description, version.toLowerCase(), skill)

    fun addStatBaseCalcModifier(name: String, version: String, key: String, value: Float)
            = versionFactory.addStatModifiers(name.toLowerCase(), version.toLowerCase(),
                                                    hashMapOf(Pair(key.toLowerCase(), value)))

    fun removeStatBaseCalcModifier(name: String, version: String, key: String)
            = versionFactory.removeStatModifier(name.toLowerCase(), version.toLowerCase(), key)
}