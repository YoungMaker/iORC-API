package edu.ycp.cs482.iorcapi.resolvers

import com.coxautodev.graphql.tools.GraphQLQueryResolver
import edu.ycp.cs482.iorcapi.factories.CharacterFactory
import edu.ycp.cs482.iorcapi.repositories.CharacterRepository
import org.springframework.stereotype.Component

@Component
class CharacterQueryResolver(
        private val characterFactory: CharacterFactory
) : GraphQLQueryResolver {
    fun getCharacterById(id: String) = characterFactory.getCharacterById(id)
    fun getCharactersByName(name: String) = characterFactory.getCharactersByName(name)
    fun getCharactersByVersion(version: String) = characterFactory.getCharactersByVersion(version)
}
