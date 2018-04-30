package edu.ycp.cs482.iorcapi.factories

import edu.ycp.cs482.iorcapi.model.Character
import edu.ycp.cs482.iorcapi.model.CharacterQL
import edu.ycp.cs482.iorcapi.model.ItemQL
import edu.ycp.cs482.iorcapi.model.Race
import edu.ycp.cs482.iorcapi.model.attributes.*
import edu.ycp.cs482.iorcapi.model.authentication.*
import edu.ycp.cs482.iorcapi.repositories.CharacterRepository
import edu.ycp.cs482.iorcapi.repositories.RaceRepository
import graphql.ErrorType
import graphql.GraphQLException
import org.springframework.stereotype.Component
import java.util.*

@Component
class CharacterFactory(

    private val characterRepo: CharacterRepository,
    private val detailFactory: DetailFactory,
    private val versionFactory: VersionFactory,
    private val itemFactory: ItemFactory,
    private val authorizer: Authorizer
    )  {


    //TODO: check if UUID already used. See issue #16
    fun createNewCharacter(name: String, abilityPoints: AbilityInput, raceid: String, classid: String, version: String, owner: User) : CharacterQL {
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
                slots = getSlots(version),
                access = AccessData(owner.id, mapOf(Pair(AuthorityLevel.ROLE_ADMIN, AuthorityMode.MODE_VIEW))) // only owner has r/w and admin has r
                )
        characterRepo.save(char) //should this be insert?

        return hydrateChar(char)
    }

    fun updateCharacter(id: String, name: String, abilityPoints: AbilityInput, raceid: String, classid: String, context: User) : CharacterQL {
        val char = characterRepo.findById(id) ?: throw GraphQLException("Character does not exist with that id")
        authorizer.authorizeObject(char, context, AuthorityMode.MODE_EDIT) ?: throw GraphQLException("Forbidden")
        val race = detailFactory.getRaceById(raceid) //this is to check if it exists, this will throw a query exception
        val classql = detailFactory.getClassById(classid)

        val abils = Ability(abilityPoints.str, abilityPoints.con, abilityPoints.dex, abilityPoints._int, abilityPoints.wis, abilityPoints.cha)


        val charNew = Character(id,
                name = name,
                abilityPoints = abils,
                raceid = race.id,
                classid = classql.id,
                version = char.version,
                inventory = char.inventory,
                slots = updateSlotsIfEmpty(char),
                money = char.money,
                access = char.authority
            )
        characterRepo.save(charNew) //should this be insert?

        return hydrateChar(charNew)
    }

    private fun updateSlotsIfEmpty(char: Character): MutableList<Slot>{
        val versionSlots = mutableListOf<Slot>()
        if(char.slots.isEmpty()) {
            versionSlots.addAll(getSlots(char.version))
        }
        else {
            versionSlots.clear()
            versionSlots.addAll(char.slots)
        }
        return versionSlots
    }

    fun setCharacterMoney(id: String, money: Float, context: User): CharacterQL{
        val char = characterRepo.findById(id) ?: throw GraphQLException("Character does not exist with that id")
        authorizer.authorizeObject(char, context, AuthorityMode.MODE_EDIT) ?: throw GraphQLException("Forbidden")
        val charNew  = Character(
                id =char.id,
                name = char.name,
                abilityPoints = char.abilityPoints,
                raceid = char.raceid,
                classid = char.classid,
                version = char.version,
                inventory = char.inventory,
                slots = updateSlotsIfEmpty(char),
                money =  money,
                access = char.authority
                )
        characterRepo.save(charNew) //should this be insert?
        return hydrateChar(charNew)
    }

    fun purchaseItem(id: String, itemid: String, context: User): CharacterQL{
        val char = characterRepo.findById(id) ?: throw GraphQLException("Character does not exist with that id")
        authorizer.authorizeObject(char, context, AuthorityMode.MODE_EDIT) ?: throw GraphQLException("Forbidden")
        val item = itemFactory.getItemById(itemid) //checks if item exits, throws exception if it does not
        if((char.money - item.price) >= 0f) {
           return addItemToCharacter(id, itemid, true, context) //purchases item and adds to characters inventory.
        }
        else{
            throw GraphQLException("Not enough money to purchase that item!")
        }

    }

    fun getCharacterById(id:String, context: User) : CharacterQL {
        val char = characterRepo.findById(id) ?: throw GraphQLException("Character does not exist with that id")
        authorizer.authorizeObject(char, context, AuthorityMode.MODE_VIEW) ?: throw GraphQLException("Forbidden")
        return hydrateChar(char)
    }

    fun getCharactersByName(name: String, context: User) =
            hydrateChars(characterRepo.findByNameAndAuthority_Owner(name, context.id))

    fun getUserCharacters(context: User)
        = hydrateChars(characterRepo.findByAuthority_Owner(context.id))

    fun purgeUsersCharacters(context: User) { //called when a user deletes his account
        val charList = characterRepo.findByAuthority_Owner(context.id)
        for(char in charList) {
            characterRepo.delete(char.id)
        }
    }

    fun getCharactersByVersion(version: String, context: User): List<CharacterQL> {
        val chars = characterRepo.findByVersion(version)
        authorizer.authorizeObjects(chars, context, AuthorityMode.MODE_VIEW) ?: throw GraphQLException("Forbidden")
        return hydrateChars(chars)
    }

    private fun getSlots(version: String): List<Slot>{
        val vInfoSlots = versionFactory.getVersionInfoByType(versionFactory.hydrateVersion(version), "slot").infoList
        val outputList = mutableListOf<Slot>()
        vInfoSlots.mapTo(outputList) { Slot(it.name, "", true) }
        return outputList
    }

    fun deleteCharacter(id: String, context: User): String {
        val char = characterRepo.findById(id) ?: throw GraphQLException("Character does not exist with that id")
        authorizer.authorizeObject(char, context, AuthorityMode.MODE_EDIT) ?: throw GraphQLException("Forbidden")
        characterRepo.delete(id)
        return "Character %s deleted".format(id)
    }

    fun equipItem(id:String, itemid: String, slotname: String, context: User): CharacterQL{
        val char = characterRepo.findById(id) ?: throw GraphQLException("Character does not exist with that id")
        authorizer.authorizeObject(char, context, AuthorityMode.MODE_EDIT) ?: throw GraphQLException("Forbidden")
        val item = itemFactory.getItemById(itemid) //checks if item exits, throws exception if it does not
        val theSlotType = Slot(name=slotname, itemId = "", empty = true)
        if(char.slots.contains(theSlotType) && char.inventory.contains(itemid)){ //if slot is empty and you own the item
            if(item.itemClasses.contains(slotname)) { //if item cn be put in slot
                val newSlots = mutableListOf<Slot>() //new item slots for new character object
                newSlots.addAll(char.slots) // add all the characters current slots
                newSlots.remove(theSlotType) //remove current slot from old list
                newSlots.add(Slot(name = slotname, itemId = itemid, empty = false)) //add a new occupied slot object
                val charNew = Character(id= char.id,
                        name = char.name,
                        abilityPoints = char.abilityPoints,
                        raceid = char.raceid,
                        classid = char.classid,
                        version = char.version,
                        inventory = char.inventory,
                        money = char.money,
                        slots = newSlots,
                        access = char.authority
                )
                characterRepo.save(charNew) //overwrite the character
                return hydrateChar(charNew)
            }
            else {
                throw GraphQLException("Character cannot put that item in slot")
            }
         } else {
            throw GraphQLException("Character does not have empty slot")
        }
    }

    //adding item to character in non-buying mode does not interact with money. This is good for later trading system.
    fun addItemToCharacter(id: String, itemid: String, buy: Boolean = false, context: User): CharacterQL{
        val char = characterRepo.findById(id) ?: throw GraphQLException("Character does not exist with that id")
        authorizer.authorizeObject(char, context, AuthorityMode.MODE_EDIT) ?: throw GraphQLException("Forbidden")
        val item = itemFactory.getItemById(itemid) //checks if item exits, throws exception if it does not
        val newInventory = mutableListOf<String>()
        newInventory.addAll(char.inventory)//take your current inventory
        newInventory.add(itemid) //and add the new item


        val leftMoney =
        if(buy){
            (char.money - item.price)
        } else{
            char.money
        }

        val charNew = Character(id= char.id,
                name = char.name,
                abilityPoints = char.abilityPoints,
                raceid = char.raceid,
                classid = char.classid,
                version = char.version,
                inventory = newInventory,
                slots = updateSlotsIfEmpty(char),
                money = leftMoney,
                access = char.authority
                )
        characterRepo.save(charNew)
        return hydrateChar(charNew)
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
            }catch (e: GraphQLException){
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
                }catch (e: GraphQLException) {
                    outputList.add(SlotQL(name = slot.name, item = ItemQL(id = "ERR ITEM", name = "ITEM ERROR",
                            description = "" + e.message,
                            modifiers = listOf(), price = -1f, version = "ERR"), empty = slot.empty))
                }
            }
            else {
                outputList.add(
                        SlotQL(name = slot.name, item = null, empty = slot.empty)
                )
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
                slots = hydrateSlots(char.slots),
                money = char.money

            )
    }



}