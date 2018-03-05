package edu.ycp.cs482.iorcapi.model

import edu.ycp.cs482.iorcapi.model.attributes.Modifier
import org.springframework.stereotype.Component

@Component
class ModTools{

    fun unionModifiers(modifiers: Map<String, Float>, mods: Map<String, Float>): Map<String, Float> {
        val finalMods = HashMap<String, Float>(modifiers)
        finalMods.putAll(mods)
        return finalMods
    }

    fun removeModifiers(modifiers: Map<String, Float>, mods: Map<String, Float>): Map<String, Float> {
        val finalMods = HashMap<String, Float>(modifiers)
        for((key, value) in mods) {
            if(finalMods.containsKey(key))
                finalMods.remove(key)
        }
        return finalMods
    }

    fun removeModifier(modifiers: Map<String, Float>, key: String): Map<String, Float> {
        val rMap = mapOf(Pair(key, 0.0f))
        return removeModifiers(modifiers,rMap)
    }

    fun convertToModifiers(modifiers: Map<String, Float>) : List<Modifier> {
        val outputList = mutableListOf<Modifier>()
        for((key, value) in  modifiers) {
            outputList.add(Modifier(key, value))
        }
        return outputList
    }
}