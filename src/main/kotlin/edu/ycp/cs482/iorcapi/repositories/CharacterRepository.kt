package edu.ycp.cs482.iorcapi.repositories

import edu.ycp.cs482.iorcapi.model.Character
import org.springframework.data.mongodb.repository.MongoRepository

interface CharacterRepository : MongoRepository<Character, Int> {
    fun findByName(name: String): Character
}