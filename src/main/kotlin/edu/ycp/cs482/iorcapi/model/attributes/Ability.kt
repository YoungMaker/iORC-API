package edu.ycp.cs482.iorcapi.model.attributes

/**
 * @property str Strength
 * @property con Constitution
 */
data class Ability(
        val str: Int,
        val con: Int,
        val dex: Int,
        val int: Int,
        val wis: Int,
        val cha: Int
)

data class AbilityInput(
        val str: Int,
        val con: Int,
        val dex: Int,
        val _int: Int,
        val wis: Int,
        val cha: Int
)