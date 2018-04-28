package edu.ycp.cs482.iorcapi.factories

import edu.ycp.cs482.iorcapi.model.Item
import edu.ycp.cs482.iorcapi.model.ItemQL
import edu.ycp.cs482.iorcapi.model.attributes.ObjType
import edu.ycp.cs482.iorcapi.repositories.ItemRepository
import graphql.ErrorType
import graphql.GraphQLException
import org.springframework.stereotype.Component

@Component
class ItemFactory(
        private val itemRepository: ItemRepository
) {
    //TODO: Enforce validation of item types?
    fun addItem(name: String, description: String, price: Float,
                itemClasses: List<String>, version: String, type: ObjType): ItemQL{
        //create item id by adding name and version strings
        val itemId = (name.trim()+version.trim())
        //check if item already exists
        if(itemRepository.exists(itemId)){
            throw GraphQLException("Item already exists in repository")
        }

        val item = Item(itemId, name, description, price, mapOf(), itemClasses, version, type)
        itemRepository.save(item)
        return ItemQL(item)
    }

    fun deleteItem(id:String):String{
        if(!itemRepository.exists(id)){
            return "Item %S does not exist".format(id)
        }
        itemRepository.delete(id)
        return "Item %S has been deleted".format(id)
    }

    fun getVersionItems(version: String)
            = itemRepository.findByVersion(version).map { ItemQL(it) }

    fun getVersionItemType(version: String, type: ObjType)
            = itemRepository.findByVersionAndType(version, type).map {ItemQL(it)}

    fun getItemsByClasses(version: String, classes: List<String>)
            = itemRepository.findByVersionAndItemClasses(version, classes).map { ItemQL(it) }

    fun getItemsByClassesIn(version: String, classes: List<String>)
            = itemRepository.findByVersionAndItemClassesIn(version, classes).map { ItemQL(it) }

    fun addItemModifier(id: String, mods:HashMap<String, Float>): ItemQL{
        val item =itemRepository.findById(id) ?:
                            throw GraphQLException("Item Does not exist in that version with that name")

        item.unionModifiers(mods)
        itemRepository.save(item)
        return ItemQL(item)
    }

    fun getItemById(id: String): ItemQL{
      val item = itemRepository.findById(id) ?: throw GraphQLException("Item Does not exist in that version with that name")
        return ItemQL(item)
    }

    fun removeItemModifier(id: String, key: String): ItemQL{
        val item =itemRepository.findById(id) ?:
        throw GraphQLException("Item Does not exist in that version with that name")

        item.removeModifier(key)
        itemRepository.save(item)
        return ItemQL(item)
    }
}