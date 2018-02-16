package edu.ycp.cs482.iorcapi.repositories

import edu.ycp.cs482.iorcapi.model.Race
import org.springframework.data.mongodb.repository.MongoRepository

interface RaceRepository : MongoRepository<Race, Int> {
    fun findByName(name: String): List<Race>
    fun findByDescription(Description: String): Race
    override fun findAll(): List<Race>
    fun findById(id: String): Race?
}