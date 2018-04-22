package edu.ycp.cs482.iorcapi.resolvers

import com.coxautodev.graphql.tools.GraphQLMutationResolver
import edu.ycp.cs482.iorcapi.factories.DetailFactory
import org.springframework.stereotype.Component


@Component
class DetailMutationResolver(
        private val detailFactory: DetailFactory
) : GraphQLMutationResolver {

    /*** class mutations **/
    fun createClass(name: String, role: String, version: String, description: String )
            = detailFactory.createNewClass(name, role, version, description)
    fun updateClass(id: String, name: String, role: String , version: String, description: String )
            = detailFactory.updateClass(id, name, role, version, description)
    fun deleteClass(id:String) = detailFactory.deleteClass(id)

    /*** race mutations **/
    fun createRace(name: String, version: String, description: String )
            = detailFactory.createNewRace(name, version, description)
    fun updateRace(id: String, name: String, version: String, description: String )
            = detailFactory.updateRace(id, name, version, description)
    fun deleteRace(id:String) = detailFactory.deleteRace(id)

}