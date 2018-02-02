package edu.ycp.cs482.iorcapi.model

import org.springframework.data.annotation.Id

//TODO: add mutators once mutators are implemented.
data class Race (
    @Id val id: Int,
    val name: String,
    val description: String
)