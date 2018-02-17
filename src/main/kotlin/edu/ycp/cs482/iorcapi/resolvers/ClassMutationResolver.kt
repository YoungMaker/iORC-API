package edu.ycp.cs482.iorcapi.resolvers

import com.coxautodev.graphql.tools.GraphQLMutationResolver
import edu.ycp.cs482.iorcapi.factories.DetailFactory
import org.springframework.stereotype.Component


@Component
class ClassMutationResolver(
        private val detailFactory: DetailFactory
) : GraphQLMutationResolver {

    fun createClass(name: String, role: String, version: String, description: String )
            = detailFactory.createNewClass(name, role, version, description)
    fun updateClass(id: String, name: String, role: String , version: String, description: String )
            = detailFactory.updateClass(id, name, role, version, description)
}