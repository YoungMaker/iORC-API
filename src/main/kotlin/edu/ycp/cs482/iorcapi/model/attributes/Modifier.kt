package edu.ycp.cs482.iorcapi.model.attributes

import edu.ycp.cs482.iorcapi.model.Accessible
import edu.ycp.cs482.iorcapi.model.authentication.AccessData
import edu.ycp.cs482.iorcapi.model.authentication.AuthorityLevel
import edu.ycp.cs482.iorcapi.model.authentication.AuthorityMode

open class Modifiable(
        var modifiers: Map<String, Float> = mapOf(),
        val access: AccessData = AccessData("", mapOf(
                Pair(AuthorityLevel.ROLE_USER, AuthorityMode.MODE_VIEW),
                Pair(AuthorityLevel.ROLE_ADMIN, AuthorityMode.MODE_EDIT)))
): Accessible() { //TODO: we need to be able to get accessible data into here, like a new owner
    fun unionModifiers(mods: Map<String, Float>){
        val finalMods = HashMap<String, Float>(modifiers)
        finalMods.putAll(mods)
        modifiers = finalMods
    }

    fun removeModifiers(mods: Map<String, Float>){
        val finalMods = HashMap<String, Float>(modifiers)
        for((key, value) in mods) {
            if(finalMods.containsKey(key))
                finalMods.remove(key)
        }
        modifiers = finalMods
    }

    fun removeModifier(key: String) {
        val rMap = mapOf(Pair(key, 0.0f))
         removeModifiers(rMap)
    }

    fun convertToModifiers() : List<Modifier> {
        val outputList = mutableListOf<Modifier>()
        for((key, value) in  modifiers) {
            outputList.add(Modifier(key, value))
        }
        return outputList
    }

    fun convertToHashMap(mList: List<Modifier>) : Map<String, Float> {
        val outputMap = mutableMapOf<String, Float>()
        for(modifier in mList){
            outputMap[modifier.key] = modifier.value
        }
        return outputMap
    }

}

//these are now floats because we have multiplier modifiers for the stat sheet.
//stores modifiers in key,value format. such as "AC",+5
data class Modifier(
       // @Id val id: String,
        val key: String,
        val value: Float
)