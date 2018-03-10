package edu.ycp.cs482.iorcapi.resolvers

import com.coxautodev.graphql.tools.GraphQLMutationResolver
import edu.ycp.cs482.iorcapi.factories.ItemFactory
import edu.ycp.cs482.iorcapi.model.attributes.ObjType
import org.springframework.stereotype.Component

@Component
class ItemMutationResolver(
        private val itemFactory: ItemFactory
): GraphQLMutationResolver {
    fun addItemToVersion(name: String, description: String, price: Float,
    itemClasses: List<String>, version: String, type: ObjType)
        = itemFactory.addItem(name, description,price, itemClasses, version, type)
}