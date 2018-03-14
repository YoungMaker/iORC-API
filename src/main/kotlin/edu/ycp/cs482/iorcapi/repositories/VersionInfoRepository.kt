package edu.ycp.cs482.iorcapi.repositories

import edu.ycp.cs482.iorcapi.model.attributes.VersionInfo
import org.springframework.data.mongodb.repository.MongoRepository

interface VersionInfoRepository: MongoRepository<VersionInfo, String> {
    fun findByVersion(version: String): List<VersionInfo>
    fun findById(id: String): VersionInfo?
    fun findByVersionAndType(version: String, type: String): List<VersionInfo>
}