package edu.ycp.cs482.iorcapi.resolvers

import com.coxautodev.graphql.tools.GraphQLMutationResolver
import edu.ycp.cs482.iorcapi.factories.UserFactory
import edu.ycp.cs482.iorcapi.factories.VersionFactory
import edu.ycp.cs482.iorcapi.model.authentication.Context
import edu.ycp.cs482.iorcapi.repositories.UserRepository
import org.springframework.stereotype.Component

@Component
class VersionMutationResolver(
        private val versionFactory: VersionFactory,
        private val userFactory: UserFactory
): GraphQLMutationResolver{

    fun createVersion(version: String, context: Context)
        = versionFactory.createVersion(version.toLowerCase().trim(), userFactory.hydrateUser(context))

    fun addInfoToVersion(name: String, version: String, type: String, value: String)
        = versionFactory.addInfoToVersion(name.trim(), type.trim(), value.trim(), versionFactory.hydrateVersion(version.toLowerCase().trim()))

    fun addStatToVersion(key:String, name: String, description: String, version: String, skill: Boolean)
            = versionFactory.addStatToVersion(key.toLowerCase().trim(), name, description, versionFactory.hydrateVersion(version.toLowerCase().trim()), skill)

    fun addStatBaseCalcModifier(statKey: String, version: String, key: String, value: Float)
            = versionFactory.addStatModifiers(statKey.toLowerCase().trim(), versionFactory.hydrateVersion(version.toLowerCase().trim()),
                                                    hashMapOf(Pair(key.toLowerCase(), value)))

    fun removeStatBaseCalcModifier(statKey: String, version: String, key: String)
            = versionFactory.removeStatModifier(statKey.toLowerCase().trim(), versionFactory.hydrateVersion(version.toLowerCase().trim()), key)
}