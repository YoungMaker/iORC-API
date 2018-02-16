package edu.ycp.cs482.iorcapi.model

import edu.ycp.cs482.iorcapi.model.attributes.Modifier
import org.springframework.data.annotation.Id



data class Race (
    @Id val id: String,
    val name: String,
    val description: String,
    val version: String,
    val modifiers: List<Modifier> = listOf<Modifier>()
)