package edu.ycp.cs482.iorcapi.resolvers

import com.coxautodev.graphql.tools.GraphQLMutationResolver
import com.coxautodev.graphql.tools.ObjectMapperConfigurerContext
import edu.ycp.cs482.iorcapi.factories.CharacterFactory
import edu.ycp.cs482.iorcapi.factories.UserFactory
import edu.ycp.cs482.iorcapi.model.Race
import edu.ycp.cs482.iorcapi.model.attributes.Ability
import edu.ycp.cs482.iorcapi.repositories.CharacterRepository
import org.springframework.stereotype.Component
import edu.ycp.cs482.iorcapi.model.Character
import edu.ycp.cs482.iorcapi.model.CharacterQL
import edu.ycp.cs482.iorcapi.model.attributes.AbilityInput
import edu.ycp.cs482.iorcapi.model.authentication.Context
import java.util.*


@Component
class CharacterMutationResolver(
        private val characterFactory: CharacterFactory,
        private val userFactory: UserFactory
) : GraphQLMutationResolver {
    //TODO: add validation so that the scalar values submitted with the AbilityPoints cannot be negative.

    fun createCharacter(name: String, abilityPoints: AbilityInput, raceid: String, classid: String, version: String, context: Context)
            = characterFactory.createNewCharacter(name, abilityPoints, raceid, classid, version, userFactory.hydrateUser(context))

    fun updateCharacter(id: String, name: String, abilityPoints: AbilityInput, raceid: String, classid: String, context: Context)
            = characterFactory.updateCharacter(id, name, abilityPoints, raceid, classid, userFactory.hydrateUser(context))

    fun deleteCharacter(id: String, context: Context) =
            characterFactory.deleteCharacter(id, userFactory.hydrateUser(context))

    fun addItemToCharacter(id: String, itemid: String, context: Context) =
            characterFactory.addItemToCharacter(id, itemid,  false, userFactory.hydrateUser(context))

    fun equipItem(id: String, itemid: String, slotid: String, context: Context) =
            characterFactory.equipItem(id, itemid, slotid,  userFactory.hydrateUser(context))

    fun purchaseItem(id: String, itemid: String, context: Context) =
            characterFactory.purchaseItem(id, itemid, userFactory.hydrateUser(context))

    fun setCharacterMoney(id: String, money: Float, context: Context) =
            characterFactory.setCharacterMoney(id, money,  userFactory.hydrateUser(context))
}