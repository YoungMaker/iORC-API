package edu.ycp.cs482.iorcapi.repositories

import edu.ycp.cs482.iorcapi.model.attributes.Stat
import org.springframework.data.mongodb.repository.MongoRepository

interface StatRepository: MongoRepository<Stat, String> {
    fun findById(id: String): Stat?
    fun findByVersion(version: String): List<Stat>
}