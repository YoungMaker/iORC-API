package edu.ycp.cs482.iorcapi.factories

import edu.ycp.cs482.iorcapi.error.QueryException
import edu.ycp.cs482.iorcapi.model.Character
import edu.ycp.cs482.iorcapi.model.CharacterQL
import edu.ycp.cs482.iorcapi.model.ItemQL
import edu.ycp.cs482.iorcapi.model.Race
import edu.ycp.cs482.iorcapi.model.attributes.*
import edu.ycp.cs482.iorcapi.repositories.CharacterRepository
import edu.ycp.cs482.iorcapi.repositories.RaceRepository
import graphql.ErrorType
import org.springframework.stereotype.Component
import java.util.*

@Component
class CharacterFactory(

    private val characterRepo: CharacterRepository,
    private val detailFactory: DetailFactory,
    private val versionFactory: VersionFactory,
    private val itemFactory: ItemFactory
    )  {
    //TODO: Security! Access control checks! Associate with users
//    fun createNewCharacter(name: String, abilityPoints: Ability, race: Race) : Character {
//        val char = Character(UUID.randomUUID().toString(), name = name, abilityPoints = abilityPoints, race = race)
//        characterRepo.insert(char)
//        return char
//    }
    //TODO: check if UUID already used. See issue #16
    fun createNewCharacter(name: String, abilityPoints: AbilityInput, raceid: String, classid: String, version: String) : CharacterQL {
        val race = detailFactory.getRaceById(raceid) //this is to check if it exists, this will throw a query exception
        val classql = detailFactory.getClassById(classid)

        val abils = Ability(abilityPoints.str, abilityPoints.con, abilityPoints.dex, abilityPoints._int, abilityPoints.wis, abilityPoints.cha)

        val char = Character(UUID.randomUUID().toString(),
                name = name,
                abilityPoints = abils,
                raceid = race.id,
                classid = classql.id,
                version = version,
                inventory = listOf(),
                slots = getSlots(version))
        characterRepo.save(char) //should this be insert?

        return hydrateChar(char)
    }

    fun updateCharacter(id: String, name: String, abilityPoints: AbilityInput, raceid: String, classid: String) : CharacterQL {
        val char = characterRepo.findById(id) ?: throw QueryException("Character does not exist with that id", ErrorType.DataFetchingException)
        val race = detailFactory.getRaceById(raceid) //this is to check if it exists, this will throw a query exception
        val classql = detailFactory.getClassById(classid)

        val abils = Ability(abilityPoints.str, abilityPoints.con, abilityPoints.dex, abilityPoints._int, abilityPoints.wis, abilityPoints.cha)

        val versionSlots = mutableListOf<Slot>()
        if(char.slots.isEmpty()) {
            versionSlots.addAll(getSlots(char.version))
        }
        else {
            versionSlots.clear()
            versionSlots.addAll(char.slots)
        }

        val charNew = Character(id,
                name = name,
                abilityPoints = abils,
                raceid = race.id,
                classid = classql.id,
                version = char.version,
                inventory = char.inventory,
                slots = versionSlots)
        characterRepo.save(charNew) //should this be insert?

        return hydrateChar(charNew)
    }

//    //depreciated.
//    fun updateName(id: String, name: String) : CharacterQL {
//        val char = characterRepo.findById(id) ?: throw QueryException("Character does not exist with that id", ErrorType.DataFetchingException)
//
//        val newChar = Character(id, name, char.abilityPoints, char.raceid, char.classid, char.version) // creates new one based on old one
//        characterRepo.save(newChar) // this should write over the old one with the new name
//        return hydrateChar(newChar)
//    }

    fun getCharacterById(id:String) : CharacterQL {
        val char = characterRepo.findById(id) ?: throw QueryException("Character does not exist with that id", ErrorType.DataFetchingException)
        return hydrateChar(char)
    }

    fun getCharactersByName(name: String) = hydrateChars(characterRepo.findByName(name))

    fun getCharactersByVersion(version: String) = hydrateChars(characterRepo.findByVersion(version))

    fun getSlots(version: String): List<Slot>{
        val vInfoSlots = versionFactory.getVersionInfoByType(version, "slot").infoList
        val outputList = mutableListOf<Slot>()
        vInfoSlots.mapTo(outputList) { Slot(it.name, "", true) }
        return outputList
    }

    fun deleteCharacter(id: String): String {
        characterRepo.delete(id)
        return "Character %s deleted".format(id)
    }

    ///maps a list to an output lits of CharacterQL graphQL objects
    fun hydrateChars(chars: List<Character>) : List<CharacterQL> {
        val output = mutableListOf<CharacterQL>()

        chars.mapTo(output) { hydrateChar(it) }
        return output
    }

    //todo: when item isn't found an error will be thrown. Do we want that?
    fun hydrateItems(itemids: List<String>): List<ItemQL>{
        val outputList = mutableListOf<ItemQL>()
        for(itemid in itemids){
            try {
                outputList.add(itemFactory.getItemById(itemid))
            }catch (e: QueryException){
                outputList.add(ItemQL(id= "ERR ITEM", name= "ITEM ERROR",
                        description = "" + e.message,
                        modifiers = listOf(), price= -1f, version = "ERR"))
            }
        }
        return outputList
    }

    fun hydrateSlots(slots: List<Slot>): List<SlotQL> {
        val outputList = mutableListOf<SlotQL>()
        for(slot in slots){
            if(!slot.empty){
                try {
                    outputList.add(
                        SlotQL(name = slot.name, item = itemFactory.getItemById(slot.itemId), empty = slot.empty))
                }catch (e: QueryException) {
                    outputList.add(SlotQL(name = slot.name, item = ItemQL(id = "ERR ITEM", name = "ITEM ERROR",
                            description = "" + e.message,
                            modifiers = listOf(), price = -1f, version = "ERR"), empty = false))
                }
            }

        }
        return outputList
    }

    //converts referential persistence object to graphQL full representation
    fun hydrateChar(char: Character) : CharacterQL {
        val race = detailFactory.getRaceById(char.raceid)
        val classql = detailFactory.getClassById(char.classid)
        return CharacterQL(id = char.id,
                version =  char.version,
                name = char.name,
                abilityPoints =  char.abilityPoints,
                race = race,
                classql = classql,
                inventory = hydrateItems(char.inventory),
                slots = hydrateSlots(char.slots)
            )
    }



}