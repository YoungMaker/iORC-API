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
        modifiers: Map<String, Int> = mapOf(),
        val version: String,
        val type: ObjType = ObjType.CLASS

) :  Modifiable(modifiers)


data class ClassQL(
        @Id val id: String,
        val name: String,
        val role: String,
        val description: String,
        val version: String,
        val modifiers: List<Modifier> = listOf(),
        val type: ObjType = ObjType.CLASS
){
    constructor(rpgClass: ClassRpg) :
            this(id = rpgClass.id,
                    name = rpgClass.name,
                    role = rpgClass.role,
                    description = rpgClass.description,
                    version = rpgClass.version,
                    modifiers = rpgClass.convertToModifiers())
}