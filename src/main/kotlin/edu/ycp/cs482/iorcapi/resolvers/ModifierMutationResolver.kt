package edu.ycp.cs482.iorcapi.resolvers

import com.coxautodev.graphql.tools.GraphQLMutationResolver
import edu.ycp.cs482.iorcapi.factories.DetailFactory
import edu.ycp.cs482.iorcapi.factories.ItemFactory
import edu.ycp.cs482.iorcapi.factories.UserFactory
import edu.ycp.cs482.iorcapi.factories.VersionFactory
import edu.ycp.cs482.iorcapi.model.attributes.ObjType
import edu.ycp.cs482.iorcapi.model.authentication.Context
import org.springframework.stereotype.Component

@Component
class ModifierMutationResolver(
    private val detailFactory: DetailFactory,
    private val itemFactory: ItemFactory,
    private val userFactory: UserFactory,
    private val versionFactory: VersionFactory

) : GraphQLMutationResolver{

    fun addModifier(id: String, type: ObjType, key: String, value: Float, version: String, context: Context): Any? {
        val versionObj = versionFactory.hydrateVersion(version.toLowerCase().trim())
        val userObj = userFactory.hydrateUser(context)
        val keyMap = hashMapOf(Pair(key.toLowerCase().trim(), value))
        
        
        return when(type) {
            ObjType.RACE -> detailFactory.addRaceModifiers(id,keyMap, versionObj, userObj)
            ObjType.CLASS -> detailFactory.addClassModifiers(id,keyMap, versionObj, userObj)

            ObjType.ITEM  -> itemFactory.addItemModifier(id,keyMap, versionObj, userObj)
            ObjType.ITEM_SPELL  -> itemFactory.addItemModifier(id,keyMap, versionObj, userObj)
            ObjType.ITEM_FEAT  -> itemFactory.addItemModifier(id,keyMap, versionObj, userObj)
            ObjType.ITEM_WEAPON  -> itemFactory.addItemModifier(id,keyMap, versionObj, userObj)
            ObjType.ITEM_ARMOR  -> itemFactory.addItemModifier(id,keyMap, versionObj, userObj)
        }
    }
    //TODO: Support list removal/addition for fewer queries

    fun removeModifier(id: String, type: ObjType, key: String, version: String, context: Context): Any? {
        val versionObj = versionFactory.hydrateVersion(version.toLowerCase().trim())
        val userObj = userFactory.hydrateUser(context)

        return when (type) {
            ObjType.RACE -> detailFactory.removeRaceModifier(id, key.toLowerCase(),  versionObj, userObj)
            ObjType.CLASS -> detailFactory.removeClassModifier(id, key.toLowerCase(), versionObj, userObj)
            
            ObjType.ITEM -> itemFactory.removeItemModifier(id, key.toLowerCase(),  versionObj, userObj)
            ObjType.ITEM_ARMOR -> itemFactory.removeItemModifier(id, key.toLowerCase(),  versionObj, userObj)
            ObjType.ITEM_SPELL -> itemFactory.removeItemModifier(id, key.toLowerCase(),  versionObj, userObj)
            ObjType.ITEM_WEAPON -> itemFactory.removeItemModifier(id, key.toLowerCase(),  versionObj, userObj)
            ObjType.ITEM_FEAT -> itemFactory.removeItemModifier(id, key.toLowerCase(),  versionObj, userObj)

        }
    }
}