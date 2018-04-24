package edu.ycp.cs482.iorcapi.repositories

import edu.ycp.cs482.iorcapi.model.authentication.User
import org.springframework.data.mongodb.repository.MongoRepository

interface UserRepository: MongoRepository<User, String> {
    fun findByEmail(email: String): User?
    fun findById(id: String): User?
    fun findByUname(uname: String): User?
}