package edu.ycp.cs482.iorcapi.factories

import edu.ycp.cs482.iorcapi.error.QueryException
import edu.ycp.cs482.iorcapi.model.Item
import edu.ycp.cs482.iorcapi.model.ItemQL
import edu.ycp.cs482.iorcapi.model.attributes.ObjType
import edu.ycp.cs482.iorcapi.repositories.ItemRepository
import graphql.ErrorType
import org.springframework.stereotype.Component

@Component
class ItemFactory(
        private val itemRepository: ItemRepository
) {
    //TODO: Enforce validation of item types?
    fun addItem(name: String, description: String, price: Float,
                itemClasses: List<String>, version: String, type: ObjType): ItemQL{
        val item = Item((name+version), name, description, price, mapOf(), itemClasses, version, type)
        itemRepository.save(item)
        return ItemQL(item)
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
                            throw QueryException("Item Does not exist in that version with that name", ErrorType.DataFetchingException)

        item.unionModifiers(mods)
        itemRepository.save(item)
        return ItemQL(item)
    }

    fun getItemById(id: String): ItemQL{
      val item = itemRepository.findById(id) ?: throw QueryException("Item Does not exist in that version with that name", ErrorType.DataFetchingException)
        return ItemQL(item)
    }

    fun removeItemModifier(id: String, key: String): ItemQL{
        val item =itemRepository.findById(id) ?:
        throw QueryException("Item Does not exist in that version with that name", ErrorType.DataFetchingException)

        item.removeModifier(key)
        itemRepository.save(item)
        return ItemQL(item)
    }
}