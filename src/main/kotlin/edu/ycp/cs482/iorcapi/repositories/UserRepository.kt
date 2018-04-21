package edu.ycp.cs482.iorcapi.repositories

import edu.ycp.cs482.iorcapi.model.Character
import edu.ycp.cs482.iorcapi.model.user.User
import org.springframework.data.mongodb.repository.MongoRepository

interface UserRepository: MongoRepository<User, String> {
    fun findByEmail(name: String): List<Character>
    fun findById(id: String): User?
}