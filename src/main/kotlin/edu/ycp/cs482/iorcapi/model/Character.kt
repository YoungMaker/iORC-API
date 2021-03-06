package edu.ycp.cs482.iorcapi.model

import edu.ycp.cs482.iorcapi.model.attributes.Ability
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.PersistenceConstructor
import edu.ycp.cs482.iorcapi.model.attributes.Slot
import edu.ycp.cs482.iorcapi.model.attributes.SlotQL
import edu.ycp.cs482.iorcapi.model.authentication.AccessData
import edu.ycp.cs482.iorcapi.model.authentication.AuthorityLevel
import edu.ycp.cs482.iorcapi.model.authentication.AuthorityMode

class Character( //DB type
        @Id val id: String,
        val name: String,
        val abilityPoints: Ability,
        val raceid: String,
        val classid: String,
        val version: String = "",
        val inventory: List<String>,
        val slots: List<Slot>,
        val money: Float = 0f,
        access: AccessData
): Accessible(access) {
    @PersistenceConstructor
    constructor(        id: String,
                        version: String,
                        name: String,
                        abilityPoints: Ability,
                        raceid: String, classid: String,
                        inventory: List<String>,
                        slots: List<Slot>
                        ) :
            this(id, name, abilityPoints, raceid, classid, version, inventory, slots,0f,  AccessData("", mapOf(
                    Pair(AuthorityLevel.ROLE_ADMIN, AuthorityMode.MODE_EDIT))))

    @PersistenceConstructor
    constructor(        id: String,
                        version: String,
                        name: String,
                        abilityPoints: Ability,
                        raceid: String, classid: String) :
            this(id, name, abilityPoints, raceid, classid, version, listOf(), listOf(),0f,  AccessData("", mapOf(
                    Pair(AuthorityLevel.ROLE_ADMIN, AuthorityMode.MODE_EDIT))) )

    @PersistenceConstructor
    constructor(        id: String,
                        version: String,
                        name: String,
                        abilityPoints: Ability,
                        raceid: String, classid: String, money: Float) :
            this(id, name, abilityPoints, raceid, classid, version, listOf(), listOf(), money,  AccessData("", mapOf(
                    Pair(AuthorityLevel.ROLE_ADMIN, AuthorityMode.MODE_EDIT))) )
}

data class CharacterQL ( //output type
        @Id val id: String,
        val version: String,
        val name: String,
        val abilityPoints: Ability,
        val race: RaceQL,
        val classql: ClassQL,
        val inventory: List<ItemQL>,
        val slots: List<SlotQL>,
        val money: Float
) {
    constructor(        id: String,
                        version: String,
                        name: String,
                        abilityPoints: Ability,
                        race: RaceQL, classql: ClassQL, money: Float) :
            this(id, version, name, abilityPoints, race, classql, listOf(), listOf(), money )
}