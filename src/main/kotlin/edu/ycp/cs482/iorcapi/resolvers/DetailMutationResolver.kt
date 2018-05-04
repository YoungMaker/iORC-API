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

    fun addFeatsToClass(id:String, feats:List<String>, version: String, context: Context)
            = detailFactory.addClassFeats(id, feats, versionFactory.hydrateVersion(version.toLowerCase().trim()),
            userFactory.hydrateUser(context))

    fun deleteClass(id:String, version: String, context: Context) =
            detailFactory.deleteClass(id, versionFactory.hydrateVersion(version.toLowerCase().trim()), userFactory.hydrateUser(context))
    fun reformatClass(version:String, context: Context) =
            detailFactory.reformatClasses(versionFactory.hydrateVersion(version.toLowerCase().trim()), userFactory.hydrateUser(context))


    /*** race mutations **/
    fun createRace(name: String, description: String, version: String, context: Context )
            = detailFactory.createNewRace(name, description,
            versionFactory.hydrateVersion(version.toLowerCase().trim()), userFactory.hydrateUser(context))
    fun updateRace(id: String, name: String, description: String,  version: String, context: Context )
            = detailFactory.updateRace(id, name, description,
            versionFactory.hydrateVersion(version.toLowerCase().trim()), userFactory.hydrateUser(context))

    fun addFeatsToRace(id:String, feats:List<String>, version: String, context: Context) =
            detailFactory.addRaceFeats(id, feats,  versionFactory.hydrateVersion(version.toLowerCase().trim()),
                    userFactory.hydrateUser(context))

    fun removeFeatsFromRace(id:String, feats: List<String>, version: String, context: Context)=
            detailFactory.removeRaceFeats(id, feats, versionFactory.hydrateVersion(version.toLowerCase().trim()),
                    userFactory.hydrateUser(context) )
    fun removeFeatsFromClass(id:String, feats: List<String>, version: String, context: Context)=
            detailFactory.removeClassFeats(id, feats, versionFactory.hydrateVersion(version.toLowerCase().trim()),
                    userFactory.hydrateUser(context))

    fun deleteRace(id:String,version:String, context: Context) =
            detailFactory.deleteRace(id, versionFactory.hydrateVersion(version.toLowerCase().trim()),
                    userFactory.hydrateUser(context))

    fun reformatRace(version:String, context: Context) =
            detailFactory.reformatRaces(versionFactory.hydrateVersion(version.toLowerCase().trim()),
                    userFactory.hydrateUser(context))



}