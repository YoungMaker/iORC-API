package edu.ycp.cs482.iorcapi.factories

import edu.ycp.cs482.iorcapi.model.Item
import edu.ycp.cs482.iorcapi.model.ItemQL
import edu.ycp.cs482.iorcapi.model.attributes.ObjType
import edu.ycp.cs482.iorcapi.repositories.ItemRepository
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
}