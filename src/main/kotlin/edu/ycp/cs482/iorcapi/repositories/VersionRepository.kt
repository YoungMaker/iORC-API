package edu.ycp.cs482.iorcapi.repositories

import edu.ycp.cs482.iorcapi.model.Version
import edu.ycp.cs482.iorcapi.model.authentication.AccessData
import org.springframework.data.mongodb.repository.MongoRepository

interface VersionRepository: MongoRepository<Version, String> {
    fun findByVersion(version: String): Version?
    fun findByAccess(access: AccessData): List<Version>
}