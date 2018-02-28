package edu.ycp.cs482.iorcapi.model.attributes

import org.springframework.data.annotation.Id

open class Modifiable(
        var modifiers: Map<String, Int> = mapOf()
) {
    fun unionModifiers(mods: Map<String, Int>){
        val finalMods = HashMap<String, Int>(modifiers)
        finalMods.putAll(mods)
        modifiers = finalMods
    }

    fun removeModifiers(mods: Map<String, Int>){
        val finalMods = HashMap<String, Int>(modifiers)
        for((key, value) in mods) {
            if(finalMods.containsKey(key))
                finalMods.remove(key)
        }
        modifiers = finalMods
    }

    fun removeModifier(key: String) {
        val rMap = mapOf(Pair(key, 0))
         removeModifiers(rMap)
    }

    fun convertToModifiers() : List<Modifier> {
        val outputList = mutableListOf<Modifier>()
        for((key, value) in  modifiers) {
            outputList.add(Modifier(key, value))
        }
        return outputList
    }
}

//stores modifiers in key,value format. such as "AC",+5
data class Modifier(
       // @Id val id: String,
        val key: String,
        val value: Int
)