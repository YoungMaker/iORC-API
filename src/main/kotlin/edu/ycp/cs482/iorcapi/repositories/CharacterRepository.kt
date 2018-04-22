package edu.ycp.cs482.iorcapi.repositories

import edu.ycp.cs482.iorcapi.model.Character
import edu.ycp.cs482.iorcapi.model.Race
import edu.ycp.cs482.iorcapi.model.attributes.Ability
import org.springframework.data.mongodb.repository.MongoRepository

interface CharacterRepository : MongoRepository<Character, String> {
    fun findByName(name: String): List<Character>
    fun findById(id: String): Character?
    fun findByVersion(version: String): List<Character>
    fun findByAccess_Owner(owner: String): List<Character>
    fun findByNameAndAccess_Owner(name: String, owner: String): List<Character>
}