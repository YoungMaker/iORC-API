package edu.ycp.cs482.iorcapi.resolvers

import com.coxautodev.graphql.tools.GraphQLMutationResolver
import edu.ycp.cs482.iorcapi.factories.DetailFactory
import edu.ycp.cs482.iorcapi.factories.ItemFactory
import edu.ycp.cs482.iorcapi.model.attributes.ObjType
import org.springframework.stereotype.Component

@Component
class ModifierMutationResolver(
    private val detailFactory: DetailFactory,
    private val itemFactory: ItemFactory

) : GraphQLMutationResolver{

    fun addModifier(id: String, type: ObjType, key: String, value: Float): Any? {

        return when(type) {
            ObjType.RACE -> detailFactory.addRaceModifiers(id, hashMapOf(Pair(key.toLowerCase(), value)))
            ObjType.CLASS -> detailFactory.addClassModifiers(id, hashMapOf(Pair(key.toLowerCase(), value)))
            ObjType.ITEM  -> itemFactory.addItemModifier(id, hashMapOf(Pair(key.toLowerCase(), value)))
            ObjType.ITEM_SPELL  -> itemFactory.addItemModifier(id, hashMapOf(Pair(key.toLowerCase(), value)))
            ObjType.ITEM_FEAT  -> itemFactory.addItemModifier(id, hashMapOf(Pair(key.toLowerCase(), value)))
            ObjType.ITEM_WEAPON  -> itemFactory.addItemModifier(id, hashMapOf(Pair(key.toLowerCase(), value)))
            ObjType.ITEM_ARMOR  -> itemFactory.addItemModifier(id, hashMapOf(Pair(key.toLowerCase(), value)))
            //TODO: Add the rest when implemented
        }
    }
    //TODO: Support list removal/addition for fewer queries

    fun removeModifier(id: String, type: ObjType, key: String): Any? {

        return when (type) {
            ObjType.RACE -> detailFactory.removeRaceModifier(id, key.toLowerCase())
            ObjType.CLASS -> detailFactory.removeClassModifier(id, key.toLowerCase())
            ObjType.ITEM -> itemFactory.removeItemModifier(id, key.toLowerCase())
            ObjType.ITEM_ARMOR -> itemFactory.removeItemModifier(id, key.toLowerCase())
            ObjType.ITEM_SPELL -> itemFactory.removeItemModifier(id, key.toLowerCase())
            ObjType.ITEM_WEAPON -> itemFactory.removeItemModifier(id, key.toLowerCase())
            ObjType.ITEM_FEAT -> itemFactory.removeItemModifier(id, key.toLowerCase())
        //TODO: Add the rest when implemented

        }
    }
}