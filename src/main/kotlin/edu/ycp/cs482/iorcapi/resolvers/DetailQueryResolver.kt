package edu.ycp.cs482.iorcapi.resolvers

import com.coxautodev.graphql.tools.GraphQLQueryResolver
import edu.ycp.cs482.iorcapi.factories.DetailFactory
import org.springframework.stereotype.Component

@Component
class DetailQueryResolver(
        private val detailFactory: DetailFactory
) : GraphQLQueryResolver {
    /*** race queries **/
    fun getRaceById(id: String) = detailFactory.getRaceById(id)
    fun getRacesByName(name: String) = detailFactory.getRacesByName(name)
    fun getRacesByVersion(version: String) = detailFactory.getRacesByVersion(version)

    /*** class queries **/
    fun getClassById(id: String) = detailFactory.getClassById(id)
    fun getClassesByName(name: String) = detailFactory.getClassesByName(name)
    fun getClassesByVersion(version: String) = detailFactory.getClassesByVersion(version)
}