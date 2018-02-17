package edu.ycp.cs482.iorcapi.repositories

import edu.ycp.cs482.iorcapi.model.ClassRpg
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Component

@Component
interface ClassRepository : MongoRepository<ClassRpg, Int> {
    fun findByName(name: String): List<ClassRpg>
    fun findByDescription(Description: String): ClassRpg
    fun findById(id: String): ClassRpg?
    fun findByVersion(version: String): List<ClassRpg>
}