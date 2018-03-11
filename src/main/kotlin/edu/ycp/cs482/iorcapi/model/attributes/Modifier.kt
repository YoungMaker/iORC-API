package edu.ycp.cs482.iorcapi.model.attributes

import org.springframework.data.annotation.Id

open class Modifiable(
        var modifiers: Map<String, Float> = mapOf()
) {
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