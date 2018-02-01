package edu.ycp.cs482.iorcapi

import edu.ycp.cs482.iorcapi.model.Character
import edu.ycp.cs482.iorcapi.model.attributes.Ability
import edu.ycp.cs482.iorcapi.repositories.CharacterRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class SystemInit {
    @Autowired
    lateinit var characterRepository: CharacterRepository

    fun addTestCharacters() {
        characterRepository.deleteAll()
        characterRepository.insert(listOf(
                Character(
                        id = 0,
                        name = "Test Orc",
                        abilityPoints = Ability(
                                str = 5,
                                con = 7,
                                dex = 3,
                                int = 3,
                                wis = 9,
                                cha = 3
                        )
                ),
                Character(
                        id = 1,
                        name = "Test Man",
                        abilityPoints = Ability(
                                str = 4,
                                con = 7,
                                dex = 2,
                                int = 8,
                                wis = 4,
                                cha = 2
                        )
                )
        ))
    }
}