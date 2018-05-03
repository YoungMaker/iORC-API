package edu.ycp.cs482.iorcapi.model

import edu.ycp.cs482.iorcapi.model.attributes.Modifiable
import edu.ycp.cs482.iorcapi.model.attributes.Modifier
import edu.ycp.cs482.iorcapi.model.attributes.ObjType
import org.springframework.data.annotation.Id

class ClassRpg (
        @Id val id: String,
        val name: String,
        val role: String,
        val description: String,
        val feats: List<String> = listOf(),
        modifiers: Map<String, Float> = mapOf(),
        val version: String,
        val type: ObjType = ObjType.CLASS

) :  Modifiable(modifiers)


data class ClassQL(
        @Id val id: String,
        val name: String,
        val role: String,
        val description: String,
        val version: String,
        val feats: List<ItemQL> = listOf(),
        val modifiers: List<Modifier> = listOf(),
        val type: ObjType = ObjType.CLASS
){
    constructor(rpgClass: ClassRpg, feats: List<ItemQL>) :
            this(id = rpgClass.id,
                    name = rpgClass.name,
                    role = rpgClass.role,
                    description = rpgClass.description,
                    version = rpgClass.version,
                    feats = feats,
                    modifiers = rpgClass.convertToModifiers())
}