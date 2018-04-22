package edu.ycp.cs482.iorcapi.resolvers

import com.coxautodev.graphql.tools.GraphQLQueryResolver
import edu.ycp.cs482.iorcapi.factories.CharacterFactory
import edu.ycp.cs482.iorcapi.factories.UserFactory
import edu.ycp.cs482.iorcapi.model.authentication.Context
import edu.ycp.cs482.iorcapi.repositories.CharacterRepository
import org.springframework.stereotype.Component

@Component
class CharacterQueryResolver(
        private val characterFactory: CharacterFactory,
        private val userFactory: UserFactory
) : GraphQLQueryResolver {
    fun getCharacterById(id: String, context: Context) =
            characterFactory.getCharacterById(id, userFactory.hydrateUser(context))

    fun getCharactersByName(name: String, context: Context) = characterFactory.getCharactersByName(name, userFactory.hydrateUser(context))
    fun getCharactersByVersion(version: String, context: Context) = characterFactory.getCharactersByVersion(version, userFactory.hydrateUser(context))
}
