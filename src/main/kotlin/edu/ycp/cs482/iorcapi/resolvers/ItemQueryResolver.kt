package edu.ycp.cs482.iorcapi.resolvers

import com.coxautodev.graphql.tools.GraphQLQueryResolver
import edu.ycp.cs482.iorcapi.factories.ItemFactory
import edu.ycp.cs482.iorcapi.model.attributes.ObjType
import org.springframework.stereotype.Component

@Component
class ItemQueryResolver(
        private val itemFactory: ItemFactory
): GraphQLQueryResolver {
    fun getVersionItems(version: String)
        = itemFactory.getVersionItems(version)

    fun getVersionItemType(version: String, type: ObjType)
        = itemFactory.getVersionItemType(version, type)

    fun getItemsByClasses(version: String, classes: List<String>)
            = itemFactory.getItemsByClasses(version, classes)

    fun getItemsByClassesIn(version: String, classes: List<String>)
            = itemFactory.getItemsByClassesIn(version, classes)
}