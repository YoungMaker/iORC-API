package edu.ycp.cs482.iorcapi.repositories

import edu.ycp.cs482.iorcapi.model.Item
import edu.ycp.cs482.iorcapi.model.attributes.ObjType
import org.springframework.data.mongodb.repository.MongoRepository

interface ItemRepository: MongoRepository<Item, String> {
    fun findById(id: String): Item?
    fun findByVersion(version: String) : List<Item>
    fun findByVersionAndItemClasses(version: String, classes: List<String>): List<Item>
    fun findByVersionAndType(version: String, type: ObjType): List<Item>
}