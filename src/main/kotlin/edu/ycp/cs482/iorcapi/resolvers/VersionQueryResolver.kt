package edu.ycp.cs482.iorcapi.resolvers

import com.coxautodev.graphql.tools.GraphQLQueryResolver
import edu.ycp.cs482.iorcapi.factories.UserFactory
import edu.ycp.cs482.iorcapi.factories.VersionFactory
import edu.ycp.cs482.iorcapi.model.authentication.Context
import org.springframework.stereotype.Component

@Component
class VersionQueryResolver(
        private val versionFactory: VersionFactory,
        private val userFactory: UserFactory
) : GraphQLQueryResolver {

    fun getVersionSheet(version: String, context: Context) =
            versionFactory.getVersionSheet(versionFactory.hydrateVersion(version.toLowerCase().trim()), userFactory.hydrateUser(context))
    fun getVersionSkills(version: String, context: Context) =
            versionFactory.getVersionSkills(versionFactory.hydrateVersion(version.toLowerCase().trim()), userFactory.hydrateUser(context))
    fun getVersionInfoType(version: String, type: String, context: Context) =
            versionFactory.getVersionInfoByType(versionFactory.hydrateVersion(version.toLowerCase().trim()), type, userFactory.hydrateUser(context))
}