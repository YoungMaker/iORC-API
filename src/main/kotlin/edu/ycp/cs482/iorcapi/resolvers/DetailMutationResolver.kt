package edu.ycp.cs482.iorcapi.resolvers

import com.coxautodev.graphql.tools.GraphQLMutationResolver
import edu.ycp.cs482.iorcapi.factories.DetailFactory
import edu.ycp.cs482.iorcapi.factories.UserFactory
import edu.ycp.cs482.iorcapi.factories.VersionFactory
import edu.ycp.cs482.iorcapi.model.authentication.Context
import org.springframework.stereotype.Component


@Component
class DetailMutationResolver(
        private val detailFactory: DetailFactory,
        private val userFactory: UserFactory,
        private val versionFactory: VersionFactory
) : GraphQLMutationResolver {

    /*** class mutations **/
    fun createClass(name: String, role: String, description: String, version: String, context: Context )
            = detailFactory.createNewClass(name, role, description,
            versionFactory.hydrateVersion(version.toLowerCase().trim()), userFactory.hydrateUser(context))
    fun updateClass(id: String, name: String, role: String , description: String, version: String, context: Context )
            = detailFactory.updateClass(id, name, role, description,
            versionFactory.hydrateVersion(version.toLowerCase().trim()), userFactory.hydrateUser(context))

    /*** race mutations **/
    fun createRace(name: String, description: String, version: String, context: Context )
            = detailFactory.createNewRace(name, description,
            versionFactory.hydrateVersion(version.toLowerCase().trim()), userFactory.hydrateUser(context))
    fun updateRace(id: String, name: String, description: String,  version: String, context: Context )
            = detailFactory.updateRace(id, name, description,
            versionFactory.hydrateVersion(version.toLowerCase().trim()), userFactory.hydrateUser(context))


}