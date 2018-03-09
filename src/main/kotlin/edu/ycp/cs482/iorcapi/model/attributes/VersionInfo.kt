package edu.ycp.cs482.iorcapi.model.attributes

import org.springframework.data.annotation.Id

data class VersionInfo(
        @Id val id: String,
        val version: String,
        val name: String,
        val type: String,
        val value: String
)