package edu.ycp.cs482.iorcapi.resolvers

import com.coxautodev.graphql.tools.GraphQLMutationResolver
import edu.ycp.cs482.iorcapi.factories.CharacterFactory
import edu.ycp.cs482.iorcapi.model.Race
import edu.ycp.cs482.iorcapi.model.attributes.Ability
import edu.ycp.cs482.iorcapi.repositories.CharacterRepository
import org.springframework.stereotype.Component
import edu.ycp.cs482.iorcapi.model.Character
import edu.ycp.cs482.iorcapi.model.CharacterQL
import edu.ycp.cs482.iorcapi.model.attributes.AbilityInput
import java.util.*


@Component
class CharacterMutationResolver(
        private val characterFactory: CharacterFactory
) : GraphQLMutationResolver {
    //TODO: add validation so that the scalar values submitted with the AbilityPoints cannot be negative.

    fun createCharacter(name: String, abilityPoints: AbilityInput, raceid: String, classid: String, version: String)
            = characterFactory.createNewCharacter(name, abilityPoints, raceid, classid, version)
    fun updateCharacter(id: String, name: String, abilityPoints: AbilityInput, raceid: String, classid: String)
            = characterFactory.updateCharacter(id, name, abilityPoints, raceid, classid)
    //fun updateName(id: String, name: String) = characterFactory.updateName(id, name)
    fun deleteCharacter(id: String) = characterFactory.deleteCharacter(id)
    fun addItemToCharacter(id: String, itemid: String) = characterFactory.addItemToCharacter(id, itemid)
    fun removeItemFromCharacter(id:String,itemid:String) = characterFactory.removeItemFromCharacter(id,itemid)
    fun equipItem(id: String, itemid: String, slotid: String) = characterFactory.equipItem(id, itemid, slotid)
    fun unequipItem(id:String,itemid:String,slotname:String) = characterFactory.unequipItem(id, itemid, slotname)
    fun purchaseItem(id: String, itemid: String) = characterFactory.purchaseItem(id, itemid)
    fun setCharacterMoney(id: String, money: Float) = characterFactory.setCharacterMoney(id, money)
}