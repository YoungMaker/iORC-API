package edu.ycp.cs482.iorcapi.resolvers

import com.coxautodev.graphql.tools.GraphQLQueryResolver
import edu.ycp.cs482.iorcapi.factories.DetailFactory
import edu.ycp.cs482.iorcapi.factories.UserFactory
import edu.ycp.cs482.iorcapi.factories.VersionFactory
import edu.ycp.cs482.iorcapi.model.authentication.Context
import org.springframework.stereotype.Component

@Component
class DetailQueryResolver(
        private val detailFactory: DetailFactory,
        private val userFactory: UserFactory,
        private val versionFactory: VersionFactory
) : GraphQLQueryResolver {
    /*** race queries **/
    fun getRaceById(id: String, version: String, context: Context) =
            detailFactory.getRaceById(id, versionFactory.hydrateVersion(version.toLowerCase().trim()),
                    userFactory.hydrateUser(context))
    fun getRacesByName(name: String, version: String, context: Context) =
            detailFactory.getRacesByName(name, versionFactory.hydrateVersion(version.toLowerCase().trim()),
                    userFactory.hydrateUser(context))
    fun getRacesByVersion( version: String, context: Context) =
            detailFactory.getRacesByVersion(versionFactory.hydrateVersion(version.toLowerCase().trim()),
                    userFactory.hydrateUser(context))

    /*** class queries **/
    fun getClassById(id: String, version: String, context: Context) =
            detailFactory.getClassById(id, versionFactory.hydrateVersion(version.toLowerCase().trim()),
                    userFactory.hydrateUser(context))
    fun getClassesByName(name: String, version: String, context: Context) =
            detailFactory.getClassesByName(name, versionFactory.hydrateVersion(version.toLowerCase().trim()),
                    userFactory.hydrateUser(context))
    fun getClassesByVersion( version: String, context: Context) =
            detailFactory.getClassesByVersion(versionFactory.hydrateVersion(version.toLowerCase().trim()),
                    userFactory.hydrateUser(context))

}