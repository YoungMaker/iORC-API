package edu.ycp.cs482.iorcapi.model

import edu.ycp.cs482.iorcapi.model.attributes.Modifiable
import edu.ycp.cs482.iorcapi.model.attributes.Modifier
import edu.ycp.cs482.iorcapi.model.attributes.ObjType
import org.springframework.data.annotation.Id

class Item(
    @Id val id: String,
    val name: String,
    val description: String,
    val price: Float,
    modifiers: Map<String, Float> = mapOf(),
    val itemClasses: List<String> = listOf(),
    val version: String,
    val type: ObjType = ObjType.ITEM

) : Modifiable(modifiers) {

}

data class ItemQL(
        @Id val id: String,
        val name: String,
        val description: String,
        val price: Float,
        val modifiers: List<Modifier> = listOf(),
        val version: String,
        val type: ObjType = ObjType.ITEM,
        val equipped: Boolean
){
    constructor(item: Item) :
            this(id = item.id,
                    name = item.name,
                    price = item.price,
                    description = item.description,
                    type = item.type,
                    version = item.version,
                    modifiers = item.convertToModifiers(),
                    equipped = false)

}