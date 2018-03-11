package edu.ycp.cs482.iorcapi.model.attributes

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.PersistenceConstructor

class Stat(
        @Id
        val id: String,
        val name: String,
        val fname: String = name,
        val description: String,
        val version: String,
        val skill: Boolean = false,
        modifiers: Map<String, Float> = mapOf()
): Modifiable(modifiers) {
    //taken from an anecdote here
    //https://stackoverflow.com/questions/14624982/how-exactly-does-spring-data-mongodb-handle-constructors-when-rehydrating-object
    // to have mongodb use a non-default constructor you must mark it with a @PersistanceConstructor annotation
    @PersistenceConstructor
    constructor(id: String,
                name: String,
                description: String,
                version: String,
                skill: Boolean)
                :this(id, name, name.capitalize(), description, version, skill)
}



data class StatQL( //doesn't need version because it will be added to the version object sent out to QL TODO: That's false
        val key: String,
        val name: String,
        val description: String,
        val skill: Boolean,
        val modifiers: List<Modifier>
){
    constructor(stat: Stat) : this(stat.name, stat.fname, stat.description, stat.skill, stat.convertToModifiers())
}