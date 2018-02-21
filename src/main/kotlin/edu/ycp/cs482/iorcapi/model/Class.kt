package edu.ycp.cs482.iorcapi.model

import edu.ycp.cs482.iorcapi.model.attributes.Modifier
import edu.ycp.cs482.iorcapi.model.attributes.ObjType
import org.springframework.data.annotation.Id

data class ClassRpg (
        @Id val id: String,
        val name: String,
        val role: String,
        val description: String,
        val version: String,
        val modifiers: Map<String, Int> = mapOf(),
        val type: ObjType = ObjType.CLASS
)


data class ClassQL(
        @Id val id: String,
        val name: String,
        val role: String,
        val description: String,
        val version: String,
        val modifiers: List<Modifier> = listOf(),
        val type: ObjType = ObjType.CLASS
)