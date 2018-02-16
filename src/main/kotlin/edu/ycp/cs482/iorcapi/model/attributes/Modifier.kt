package edu.ycp.cs482.iorcapi.model.attributes

import org.springframework.data.annotation.Id

//stores modifiers in key,value format. such as "AC",+5
data class Modifier(
       // @Id val id: String,
        val key: String,
        val value: Int
)