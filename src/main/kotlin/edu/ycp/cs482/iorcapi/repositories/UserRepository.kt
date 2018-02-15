package edu.ycp.cs482.iorcapi.repositories

import edu.ycp.cs482.iorcapi.model.MongoUserDetails
import edu.ycp.cs482.iorcapi.model.User
import org.springframework.data.mongodb.repository.MongoRepository


interface UserRepository: MongoRepository<User, Int> {
    fun findByUsername(username: String?): User?
}