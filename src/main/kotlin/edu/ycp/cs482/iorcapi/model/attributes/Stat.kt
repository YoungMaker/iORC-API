package edu.ycp.cs482.iorcapi.model.attributes

import org.springframework.data.annotation.Id

class Stat(
        @Id
        val id: String,
        val description: String,
        val version: String,
        modifiers: Map<String, Float> = mapOf()
): Modifiable(modifiers)


data class StatQL( //doesn't need version because it will be added to the version object sent out to QL
        private val id: String,
        private val description: String,
        private val modifiers: List<Modifier>
){
    constructor(stat: Stat) : this(stat.id, stat.description, stat.convertToModifiers())
}