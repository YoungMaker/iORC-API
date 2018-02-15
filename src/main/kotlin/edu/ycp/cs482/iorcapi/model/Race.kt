package edu.ycp.cs482.iorcapi.model

import edu.ycp.cs482.iorcapi.model.attributes.Modifier
import org.springframework.data.annotation.Id


//TODO: add mutators once mutators are implemented.
data class Race (
    @Id val id: Int,
    val version: String,
    val name: String,
    val description: String,
    val modifiers: List<Modifier> = listOf<Modifier>()
)