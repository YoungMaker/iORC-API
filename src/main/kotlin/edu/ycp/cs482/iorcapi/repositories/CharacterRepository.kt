package edu.ycp.cs482.iorcapi.repositories

import edu.ycp.cs482.iorcapi.model.Character
import edu.ycp.cs482.iorcapi.model.Race
import edu.ycp.cs482.iorcapi.model.attributes.Ability
import org.springframework.data.mongodb.repository.MongoRepository

interface CharacterRepository : MongoRepository<Character, Int> { //TODO: Should the second parpameter be string?
    fun findByName(name: String): List<Character>
    fun findById(id: String): Character?
}