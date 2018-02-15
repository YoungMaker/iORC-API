package edu.ycp.cs482.iorcapi.model

import edu.ycp.cs482.iorcapi.model.attributes.Ability
import org.springframework.data.annotation.Id

data class Character(
        @Id val id: String,
        val name: String,
        val abilityPoints: Ability,
        val race: Race,
        val hp: Int = 0
)
