package edu.ycp.cs482.iorcapi.resolvers

import com.coxautodev.graphql.tools.GraphQLQueryResolver
import edu.ycp.cs482.iorcapi.repositories.CharacterRepository
import org.springframework.stereotype.Component

@Component
class CharacterQueryResolver(
        private val characterRepo: CharacterRepository
) : GraphQLQueryResolver {
    fun getCharacterByName(name: String) = characterRepo.findByName(name)
    fun getAllCharacters() = characterRepo.findAll()
}