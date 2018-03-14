package edu.ycp.cs482.iorcapi.model.attributes
import edu.ycp.cs482.iorcapi.model.Item

data class Slot(
        val name: String,
        val itemId: String,
        val empty: Boolean = true
)

data class SlotQL(
        val name: String,
        val item: Item,
        val empty: Boolean = true
)