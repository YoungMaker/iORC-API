package edu.ycp.cs482.iorcapi.factories

import edu.ycp.cs482.iorcapi.model.Item
import edu.ycp.cs482.iorcapi.model.ItemQL
import edu.ycp.cs482.iorcapi.model.Version
import edu.ycp.cs482.iorcapi.model.attributes.ObjType
import edu.ycp.cs482.iorcapi.model.authentication.AuthorityMode
import edu.ycp.cs482.iorcapi.model.authentication.Authorizer
import edu.ycp.cs482.iorcapi.model.authentication.User
import edu.ycp.cs482.iorcapi.repositories.ItemRepository
import graphql.ErrorType
import graphql.GraphQLException
import org.springframework.stereotype.Component

@Component
class ItemFactory(
        private val itemRepository: ItemRepository,
        private val authorizer: Authorizer
) {
    //TODO: Enforce validation of item types?
    fun addItem(name: String, description: String, price: Float,
                itemClasses: List<String>, type: ObjType, version: Version,  context: User): ItemQL{
        authorizer.authorizeVersion(version, context, AuthorityMode.MODE_EDIT) ?: throw GraphQLException("Forbidden")
        val itemId = (name.trim()+version.version)
        //check if item already exists
        if(itemRepository.exists(itemId)){
            throw GraphQLException("Item already exists in repository")
        }

        val item = Item(itemId, name, description, price, mapOf(), itemClasses, version.version, type)
        itemRepository.save(item)
        return ItemQL(item)
    }

    //TODO: update for ACL
    fun deleteItem(id:String):String{
        if(!itemRepository.exists(id)){
            return "Item %S does not exist".format(id)
        }
        itemRepository.delete(id)
        return "Item %S has been deleted".format(id)
    }

    fun getVersionItems(version: Version, context: User): List<ItemQL> {
        authorizer.authorizeVersion(version, context, AuthorityMode.MODE_VIEW) ?: throw GraphQLException("Forbidden")
        return itemRepository.findByVersion(version.version).map { ItemQL(it) }
    }

    fun getVersionItemType(type: ObjType, version: Version, context: User): List<ItemQL> {
        authorizer.authorizeVersion(version, context, AuthorityMode.MODE_VIEW) ?: throw GraphQLException("Forbidden")
        return itemRepository.findByVersionAndType(version.version, type).map { ItemQL(it) }
    }
    fun getItemsByClasses(classes: List<String>,version: Version, context: User): List<ItemQL> {
        authorizer.authorizeVersion(version, context, AuthorityMode.MODE_VIEW) ?: throw GraphQLException("Forbidden")
        return itemRepository.findByVersionAndItemClasses(version.version, classes).map { ItemQL(it) }
    }
    fun getItemsByClassesIn(classes: List<String>, version: Version, context: User): List<ItemQL> {
        authorizer.authorizeVersion(version, context, AuthorityMode.MODE_VIEW) ?: throw GraphQLException("Forbidden")
        return itemRepository.findByVersionAndItemClassesIn(version.version, classes).map { ItemQL(it) }
    }
    fun addItemModifier(id: String, mods:HashMap<String, Float>, version: Version, context: User): ItemQL{
        val item =itemRepository.findById(id) ?:
                            throw GraphQLException("Item Does not exist in that version with that name")
        authorizer.authorizeVersion(version, item.version, context, AuthorityMode.MODE_EDIT) ?: throw GraphQLException("Forbidden")

        item.unionModifiers(mods)
        itemRepository.save(item)
        return ItemQL(item)
    }

    fun getItemById(id: String, version: Version, context: User): ItemQL{
        val item = itemRepository.findById(id) ?: throw GraphQLException("Item Does not exist in that version with that name")
        authorizer.authorizeVersion(version, item.version, context, AuthorityMode.MODE_VIEW) ?: throw GraphQLException("Forbidden")
        return ItemQL(item)
    }

    fun removeItemModifier(id: String, key: String, version: Version, context: User): ItemQL{
        val item =itemRepository.findById(id) ?:
        throw GraphQLException("Item Does not exist in that version with that name")
        authorizer.authorizeVersion(version, item.version, context, AuthorityMode.MODE_EDIT) ?: throw GraphQLException("Forbidden")

        item.removeModifier(key)
        itemRepository.save(item)
        return ItemQL(item)
    }
}