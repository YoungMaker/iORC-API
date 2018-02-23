package edu.ycp.cs482.iorcapi.factories


import edu.ycp.cs482.iorcapi.model.Character
import edu.ycp.cs482.iorcapi.model.ClassRpg
import edu.ycp.cs482.iorcapi.model.ModTools
import edu.ycp.cs482.iorcapi.model.Race
import edu.ycp.cs482.iorcapi.model.attributes.Ability
import edu.ycp.cs482.iorcapi.repositories.CharacterRepository
import edu.ycp.cs482.iorcapi.repositories.ClassRepository
import edu.ycp.cs482.iorcapi.repositories.RaceRepository
import org.hamcrest.CoreMatchers
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*


class CharacterFactoryTest {

    //TODO: Mock the repos required and create a factory instance.
    //TODO: write tests.
    lateinit var classRepository: ClassRepository
    lateinit var raceRepository: RaceRepository
    lateinit var characterRepository: CharacterRepository
    lateinit var characterFactory: CharacterFactory
    lateinit var detailFactory: DetailFactory

    @Before
    fun setUp() {
        classRepository = RepositoryFactoryBuilder.builder().mock(ClassRepository::class.java)
        raceRepository = RepositoryFactoryBuilder.builder().mock(RaceRepository::class.java)
        characterRepository = RepositoryFactoryBuilder.builder().mock(CharacterRepository::class.java)
        addTestClasses()
        addTestRaces()
        addTestCharacters()
        detailFactory = DetailFactory(raceRepository, classRepository, ModTools())
        characterFactory = CharacterFactory(characterRepository, detailFactory)
    }

    @After
    fun tearDown() {
        characterRepository.deleteAll()
        classRepository.deleteAll()
        raceRepository.deleteAll()
    }

    fun addTestClasses() {
        classRepository.save(listOf(
                ClassRpg(
                        id = "1.1",
                        version = "TEST",
                        role = "Combatant",
                        name = "Ranger",
                        description = "TESTRANGER"
                ),
                ClassRpg(
                        id = "0.1",
                        name = "Cleric",
                        role= "Healer",
                        version = "TEST",
                        description = "TESTCLERIC"
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
                        modifiers = mapOf( Pair("int", 2), Pair("wis", 2))
                ),
                Race(
                        id = "0.0",
                        name = "Orc",
                        version = "TEST",
                        description = "TESTORC",
                        modifiers = mapOf( Pair("dex", 2), Pair("int", 2))
                )
        ))

    }

    fun addTestCharacters() {
        characterRepository.save(listOf(
                Character(
                        id = "1.2",
                        name = "Cregan the Destroyer of Worlds",
                        abilityPoints = Ability(12, 14,15,11,12,14),
                        raceid = "Orc",
                        classid = "Ranger"
                ),
                Character(
                        id = "13.0",
                        name = "Del",
                        abilityPoints = Ability(14, 13,15,17,16,11),
                        raceid = "Human",
                        classid = "Cleric"
                )
        ))
    }

    @Test
    fun createNewCharacter() {
        val character = characterFactory.createNewCharacter(
                abilityPoints = Ability(13,12,11,15,14,16),
                name = "Harold"
        )
        assertThat(character.name, CoreMatchers.`is`(CoreMatchers.equalTo("Harold")))
        assertThat(character.abilityPoints.str, CoreMatchers.`is`(CoreMatchers.equalTo(13)))
        assertThat(character.abilityPoints.con, CoreMatchers.`is`(CoreMatchers.equalTo(12)))
        assertThat(character.abilityPoints.dex, CoreMatchers.`is`(CoreMatchers.equalTo(11)))
        assertThat(character.abilityPoints.int, CoreMatchers.`is`(CoreMatchers.equalTo(15)))
        assertThat(character.abilityPoints.wis, CoreMatchers.`is`(CoreMatchers.equalTo(14)))
        assertThat(character.abilityPoints.cha, CoreMatchers.`is`(CoreMatchers.equalTo(16)))
    }

    @Test
    fun updateName() {
        var nameUpdate = characterFactory.updateName("1.2","Gerald")
        nameUpdate.name
        assertThat(nameUpdate.name, CoreMatchers.`is`(CoreMatchers.equalTo("Gerald")))
    }

    @Test
    fun getCharacterById() {
    }

    @Test
    fun getCharactersByName() {
    }


}
