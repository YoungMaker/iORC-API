package edu.ycp.cs482.iorcapi

import edu.ycp.cs482.iorcapi.model.Character
import edu.ycp.cs482.iorcapi.model.ClassRpg
import edu.ycp.cs482.iorcapi.model.attributes.Ability
import edu.ycp.cs482.iorcapi.model.Race
import edu.ycp.cs482.iorcapi.repositories.CharacterRepository
import edu.ycp.cs482.iorcapi.repositories.ClassRepository
import edu.ycp.cs482.iorcapi.repositories.RaceRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class SystemInit {
    @Autowired
    lateinit var characterRepository: CharacterRepository

    @Autowired
    lateinit var raceRepository: RaceRepository

    @Autowired
    lateinit var classRepository: ClassRepository



    fun addTestClasses() {
        classRepository.save(listOf(
                ClassRpg(
                        id = "1.1",
                        version = "TEST",
                        role = "Combatant",
                        name = "Ranger",
                        description = "TESTRANGER",
                        modifiers = mapOf( Pair("hp", 12f), Pair("will", 2f))
                ),
                ClassRpg(
                        id = "0.1",
                        name = "Cleric",
                        role= "Healer",
                        version = "TEST",
                        description = "TESTCLERIC",
                        modifiers = mapOf( Pair("hp", 12f), Pair("fort", 2f))

                )
        ))

    }


    fun addTestRaces() {
        raceRepository.save(listOf(
                Race(
                        id = "1.0",
                        version = "TEST",
                        name = "Human",
                        description = "TESTHUMAN",
                        modifiers = mapOf( Pair("int", 2f), Pair("wis", 2f))
                ),
                Race(
                        id = "0.0",
                        name = "Orc",
                        version = "TEST",
                        description = "TESTORC",
                        modifiers = mapOf( Pair("dex", 2f), Pair("int", 2f))
                )
        ))

    }

    fun addTestCharacters() {
        //characterRepository.deleteAll()
        characterRepository.save(listOf(
                Character(
                        id = "0",
                        name = "Test Orc",
                        abilityPoints = Ability(
                                str = 5,
                                con = 7,
                                dex = 3,
                                int = 3,
                                wis = 9,
                                cha = 3
                        ),
                        raceid = "0.0",
                        classid = "0.1",
                        version = "TEST"

                ),

                Character(
                        id = "1",
                        name = "Test Man",
                        abilityPoints = Ability(
                                str = 4,
                                con = 7,
                                dex = 2,
                                int = 8,
                                wis = 4,
                                cha = 2
                        ),
                        raceid = "1.0",
                        classid = "1.1",
                        version = "TEST"
                )
        ))
    }
}