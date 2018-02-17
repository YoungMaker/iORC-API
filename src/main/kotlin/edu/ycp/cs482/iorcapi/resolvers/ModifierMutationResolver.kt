package edu.ycp.cs482.iorcapi.resolvers

import com.coxautodev.graphql.tools.GraphQLMutationResolver
import edu.ycp.cs482.iorcapi.factories.DetailFactory
import edu.ycp.cs482.iorcapi.model.attributes.ObjType
import org.springframework.stereotype.Component

@Component
class ModifierMutationResolver(
    private val detailFactory: DetailFactory

) : GraphQLMutationResolver{

    fun addModifier(id: String, type: ObjType, key: String, value: Int): Any? {

        return when(type) {
            ObjType.RACE -> detailFactory.addRaceModifiers(id, hashMapOf(Pair(key.toLowerCase(), value)))
            ObjType.CLASS -> detailFactory.addClassModifiers(id, hashMapOf(Pair(key.toLowerCase(), value)))
            //TODO: Add the rest when implemented
            else -> {null}
        }
    }
}