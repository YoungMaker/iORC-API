package edu.ycp.cs482.iorcapi.model

import edu.ycp.cs482.iorcapi.model.attributes.Ability
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.PersistenceConstructor
import edu.ycp.cs482.iorcapi.model.attributes.Slot
import edu.ycp.cs482.iorcapi.model.attributes.SlotQL

data class Character( //DB type
        @Id val id: String,
        val name: String,
        val abilityPoints: Ability,
        val raceid: String,
        val classid: String,
        val version: String = "",
        val inventory: List<String>,
        val slots: List<Slot>
) {
    @PersistenceConstructor
    constructor(        id: String,
                        version: String,
                        name: String,
                        abilityPoints: Ability,
                        raceid: String, classid: String) :
            this(id, name, abilityPoints, raceid, classid, version, listOf(), listOf() )
}

data class CharacterQL ( //output type
        @Id val id: String,
        val version: String,
        val name: String,
        val abilityPoints: Ability,
        val race: RaceQL,
        val classql: ClassQL,
        val inventory: List<ItemQL>,
        val slots: List<SlotQL>
) {
    constructor(        id: String,
                        version: String,
                        name: String,
                        abilityPoints: Ability,
                        race: RaceQL, classql: ClassQL) :
            this(id, version, name, abilityPoints, race, classql, listOf(), listOf() )
}