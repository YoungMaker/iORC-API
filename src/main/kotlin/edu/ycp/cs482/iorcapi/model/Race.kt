package edu.ycp.cs482.iorcapi.model

import edu.ycp.cs482.iorcapi.model.attributes.Modifiable
import edu.ycp.cs482.iorcapi.model.attributes.Modifier
import edu.ycp.cs482.iorcapi.model.attributes.ObjType
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.PersistenceConstructor


class Race(
    @Id val id: String,
    val name: String,
    val description: String,
    val version: String,
    val feats: List<String> = listOf(),
    modifiers: Map<String, Float> = mapOf(),
    val type : ObjType = ObjType.RACE
) : Modifiable(modifiers) {
    @PersistenceConstructor
    constructor(id: String, name: String,description: String,version: String,
                     modifiers: Map<String, Float> = mapOf(), type : ObjType = ObjType.RACE):
    this(id, name, description, version, listOf(), modifiers, type)
}


data class RaceQL(
        @Id val id: String,
        val name: String,
        val description: String,
        val version: String,
        val feats: List<ItemQL> = listOf(),
        val modifiers: List<Modifier> = listOf(),
        val type: ObjType = ObjType.RACE
){
    constructor(race : Race, feats: List<ItemQL>):
            this(id = race.id,
                    name = race.name,
                    description = race.description,
                    version = race.version,
                    feats = feats,
                    modifiers =  race.convertToModifiers())
}