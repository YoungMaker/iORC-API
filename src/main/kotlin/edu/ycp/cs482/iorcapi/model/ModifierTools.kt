package edu.ycp.cs482.iorcapi.model

import edu.ycp.cs482.iorcapi.model.attributes.Modifier
import org.springframework.stereotype.Component

@Component
class ModTools{

    fun unionModifiers(modifiers: Map<String, Int>, mods: Map<String, Int>): Map<String, Int> {
        val finalMods = HashMap<String, Int>(modifiers)
        finalMods.putAll(mods)
        return finalMods
    }

    fun removeModifiers(modifiers: Map<String, Int>, mods: Map<String, Int>): Map<String, Int> {
        val finalMods = HashMap<String, Int>(modifiers)
        for((key, value) in mods) {
            if(finalMods.containsKey(key))
                finalMods.remove(key)
        }
        return finalMods
    }

    fun removeModifier(modifiers: Map<String, Int>, key: String): Map<String, Int> {
        val rMap = mapOf(Pair(key, 0))
        return removeModifiers(modifiers,rMap)
    }

    fun convertToModifiers(modifiers: Map<String, Int>) : List<Modifier> {
        val outputList = mutableListOf<Modifier>()
        for((key, value) in  modifiers) {
            outputList.add(Modifier(key, value))
        }
        return outputList
    }
}