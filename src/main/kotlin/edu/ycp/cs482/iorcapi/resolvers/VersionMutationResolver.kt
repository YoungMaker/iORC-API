package edu.ycp.cs482.iorcapi.resolvers

import com.coxautodev.graphql.tools.GraphQLMutationResolver
import edu.ycp.cs482.iorcapi.factories.VersionFactory
import org.springframework.stereotype.Component

@Component
class VersionMutationResolver(
        private val versionFactory: VersionFactory
): GraphQLMutationResolver{
    fun addStatToVersion(name: String, description: String, version: String)
            = versionFactory.addStatToVersion(name, description,version)

    fun addStatBaseCalcModifier(name: String, key: String, value: Float)
            = versionFactory.addStatModifiers(name, hashMapOf(Pair(key.toLowerCase(), value)))

    fun removeStatBaseCalcModifier(name: String, key: String)
            = versionFactory.removeStatModifier(name, key)
}