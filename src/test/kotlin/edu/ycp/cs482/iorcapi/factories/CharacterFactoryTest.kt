package edu.ycp.cs482.iorcapi.factories


import edu.ycp.cs482.iorcapi.model.Character
import edu.ycp.cs482.iorcapi.model.ClassRpg
import edu.ycp.cs482.iorcapi.model.ModTools
import edu.ycp.cs482.iorcapi.model.Race
import edu.ycp.cs482.iorcapi.model.attributes.Ability
import edu.ycp.cs482.iorcapi.repositories.CharacterRepository
import edu.ycp.cs482.iorcapi.repositories.ClassRepository
import edu.ycp.cs482.iorcapi.repositories.RaceRepository
import com.mmnaseri.utils.spring.data.dsl.factory.RepositoryFactoryBuilder
import edu.ycp.cs482.iorcapi.model.attributes.Stat
import edu.ycp.cs482.iorcapi.repositories.StatRepository
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.springframework.boot.test.context.SpringBootTest
import org.junit.runner.RunWith

import org.junit.Assert.*


class CharacterFactoryTest {

    lateinit var classRepository: ClassRepository
    lateinit var raceRepository: RaceRepository
    lateinit var characterRepository: CharacterRepository
    lateinit var characterFactory: CharacterFactory
    lateinit var detailFactory: DetailFactory
    lateinit var statRepository: StatRepository
    lateinit var versionFactory: VersionFactory

    @Before
    fun setUp() {
        classRepository = RepositoryFactoryBuilder.builder().mock(ClassRepository::class.java)
        raceRepository = RepositoryFactoryBuilder.builder().mock(RaceRepository::class.java)
        characterRepository = RepositoryFactoryBuilder.builder().mock(CharacterRepository::class.java)
        statRepository = RepositoryFactoryBuilder.builder().mock(StatRepository::class.java)
        versionFactory = VersionFactory(statRepository)
        addTestVersion()
        addTestClasses()
        addTestRaces()
        addTestCharacters()
        detailFactory = DetailFactory(raceRepository, classRepository, versionFactory)
        characterFactory = CharacterFactory(characterRepository, detailFactory)
    }

    @After
    fun tearDown() {
        characterRepository.deleteAll()
        classRepository.deleteAll()
        raceRepository.deleteAll()
    }


    fun addTestVersion(){
        versionFactory.initializeVersion("TEST")
        statRepository.save(listOf(
                Stat(
                        id= "hpTEST",
                        name= "hp",
                        description = "health points",
                        version = "TEST",
                        skill = false
                ),
                Stat(
                        id= "willTEST",
                        name= "will",
                        description = "Willpower",
                        version = "TEST",
                        skill = false
                ),
                Stat(
                        id= "fortTEST",
                        name= "fort",
                        description = "Fortitude",
                        version = "TEST",
                        skill = false
                )
        ))
    }

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
        characterRepository.save(listOf(
                Character(
                        id = "1.2",
                        name = "Cregan the Destroyer of Worlds",
                        abilityPoints = Ability(12, 14,15,11,12,14),
                        raceid = "0.0",
                        classid = "1.1",
                        version = "TEST"
                ),
                Character(
                        id = "13.0",
                        name = "Del",
                        abilityPoints = Ability(14, 13,15,17,16,11),
                        raceid = "1.0",
                        classid = "0.1",
                        version = "TEST"
                )
        ))
    }

    @Test
    fun createNewCharacter() {
        val character = characterFactory.createNewCharacter(
                abilityPoints = Ability(13,12,11,15,14,16),
                name = "Harold",
                classid = "1.1",
                raceid = "0.0",
                version = "TEST"
        )
        val classRpg = detailFactory.getClassById("1.1")
        assertThat(character.name, CoreMatchers.`is`(equalTo("Harold")))
        assertThat(character.abilityPoints.str, `is`(equalTo(13)))
        assertThat(character.abilityPoints.con, `is`(equalTo(12)))
        assertThat(character.abilityPoints.dex, `is`(equalTo(11)))
        assertThat(character.abilityPoints.int, `is`(equalTo(15)))
        assertThat(character.abilityPoints.wis, `is`(equalTo(14)))
        assertThat(character.abilityPoints.cha, `is`(equalTo(16)))
        assertThat(character.classql, `is`(equalTo(classRpg)))
    }

    @Test
    fun updateName() {
        val nameUpdate = characterFactory.updateName("1.2","Gerald")
        assertThat(nameUpdate.name, `is`(equalTo("Gerald")))
    }

    @Test
    fun getCharacterById() {
        val character = characterFactory.getCharacterById("1.2")

        assertThat(character.name,  `is`(equalTo("Cregan the Destroyer of Worlds")))
        assertThat(character.abilityPoints,  `is`(equalTo(Ability(12, 14, 15, 11, 12, 14))))
        assertThat(character.race.name,  `is`(equalTo("Orc")))

    }

    @Test
    fun getCharactersByName() {
        val characterList = characterFactory.getCharactersByName("Cregan the Destroyer of Worlds")

        assertThat(characterList.count(), `is`(equalTo(1)))

        val character = characterList[0]

        assertThat(character.name,  `is`(equalTo("Cregan the Destroyer of Worlds")))
        assertThat(character.abilityPoints,  `is`(equalTo(Ability(12, 14, 15, 11, 12, 14))))
        assertThat(character.race.name,  `is`(equalTo("Orc")))

    }


}
