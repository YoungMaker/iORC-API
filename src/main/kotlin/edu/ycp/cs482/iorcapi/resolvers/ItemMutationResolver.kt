package edu.ycp.cs482.iorcapi.resolvers

import com.coxautodev.graphql.tools.GraphQLMutationResolver
import edu.ycp.cs482.iorcapi.factories.ItemFactory
import edu.ycp.cs482.iorcapi.factories.UserFactory
import edu.ycp.cs482.iorcapi.factories.VersionFactory
import edu.ycp.cs482.iorcapi.model.attributes.ObjType
import edu.ycp.cs482.iorcapi.model.authentication.Context
import org.springframework.stereotype.Component

@Component
class ItemMutationResolver(
        private val itemFactory: ItemFactory,
        private val versionFactory: VersionFactory,
        private val userFactory: UserFactory
): GraphQLMutationResolver {
    fun addItemToVersion(name: String, description: String, price: Float,
    itemClasses: List<String>, type: ObjType,  version: String, context: Context)
        = itemFactory.addItem(name, description,price, itemClasses, type,
            versionFactory.hydrateVersion(version.toLowerCase().trim()), userFactory.hydrateUser(context))
}