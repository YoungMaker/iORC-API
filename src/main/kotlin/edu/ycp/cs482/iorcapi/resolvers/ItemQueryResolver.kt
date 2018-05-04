package edu.ycp.cs482.iorcapi.resolvers

import com.coxautodev.graphql.tools.GraphQLQueryResolver
import edu.ycp.cs482.iorcapi.factories.ItemFactory
import edu.ycp.cs482.iorcapi.factories.UserFactory
import edu.ycp.cs482.iorcapi.factories.VersionFactory
import edu.ycp.cs482.iorcapi.model.attributes.ObjType
import edu.ycp.cs482.iorcapi.model.authentication.Context
import org.springframework.stereotype.Component


@Component
class ItemQueryResolver(
        private val itemFactory: ItemFactory,
        private val userFactory: UserFactory,
        private val versionFactory: VersionFactory
): GraphQLQueryResolver {
    fun getVersionItems(version: String, context: Context)
        = itemFactory.getVersionItems(versionFactory.hydrateVersion(version.toLowerCase().trim()), userFactory.hydrateUser(context))

    fun getVersionItemType(type: ObjType, version: String,  context: Context)
        = itemFactory.getVersionItemType(type, versionFactory.hydrateVersion(version.toLowerCase().trim()), userFactory.hydrateUser(context))

    fun getItemsByClasses(classes: List<String>, version: String, context: Context)
            = itemFactory.getItemsByClasses(classes, versionFactory.hydrateVersion(version.toLowerCase().trim()), userFactory.hydrateUser(context))

    fun getItemsByClassesIn(classes: List<String>, version: String, context: Context)
            = itemFactory.getItemsByClassesIn(classes, versionFactory.hydrateVersion(version.toLowerCase().trim()), userFactory.hydrateUser(context))
}