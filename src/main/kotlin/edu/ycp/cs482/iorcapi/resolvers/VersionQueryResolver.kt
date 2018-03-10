package edu.ycp.cs482.iorcapi.resolvers

import com.coxautodev.graphql.tools.GraphQLQueryResolver
import edu.ycp.cs482.iorcapi.factories.VersionFactory
import org.springframework.stereotype.Component

@Component
class VersionQueryResolver(
        private val versionFactory: VersionFactory
) : GraphQLQueryResolver {

    fun getVersionSheet(version: String) = versionFactory.constructVersionSheet(version)
    fun getVersionSkills(version: String) = versionFactory.getVersionSkills(version)
    fun getVersionInfoType(version: String, type: String) = versionFactory.getVersionInfoByType(version, type)
}