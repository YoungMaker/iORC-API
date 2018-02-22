package edu.ycp.cs482.iorcapi.factories

import edu.ycp.cs482.iorcapi.model.ClassRpg
import edu.ycp.cs482.iorcapi.repositories.ClassRepository
import org.hamcrest.CoreMatchers.*
import org.hamcrest.CoreMatchers.notNullValue
import org.junit.After
import org.junit.Before
import org.junit.Test

import org.junit.Assert.*

import org.springframework.boot.test.context.SpringBootTest

import com.mmnaseri.utils.spring.data.dsl.factory.RepositoryFactoryBuilder
import edu.ycp.cs482.iorcapi.model.ModTools
import edu.ycp.cs482.iorcapi.model.Race
import edu.ycp.cs482.iorcapi.model.RaceQL
import edu.ycp.cs482.iorcapi.model.attributes.Modifier
import edu.ycp.cs482.iorcapi.repositories.RaceRepository


@SpringBootTest
class DetailFactoryTest {

    lateinit var classRepository: ClassRepository
    lateinit var raceRepository: RaceRepository
    lateinit var detailFactory: DetailFactory

    @Before
    fun setUp() {
        classRepository = RepositoryFactoryBuilder.builder().mock(ClassRepository::class.java)
        raceRepository = RepositoryFactoryBuilder.builder().mock(RaceRepository::class.java)
        addTestClasses()
        addTestRaces()
        detailFactory = DetailFactory(raceRepository, classRepository, ModTools())
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

    @After
    fun tearDown() {
        classRepository.deleteAll()
    }

    @Test
    fun createNewRace() {
        val race = detailFactory.createNewRace(
                        name = "Half-Elf",
                        version = "TEST",
                        description = "TESTHALFELF"
                        )
        assertThat(race.name, `is`(equalTo("Half-Elf")))
        assertThat(race.version,  `is`(equalTo("TEST")))
        assertThat(race.description,  `is`(equalTo("TESTHALFELF")))

        val repoRace = raceRepository.findByName("Half-Elf")[0]

        assertThat(repoRace, notNullValue())
        assertThat(race.name, `is`(equalTo(race.name)))
        assertThat(repoRace.version,  `is`(equalTo(race.version)))
        assertThat(repoRace.description,  `is`(equalTo(race.description)))

    }

    @Test
    fun getRaceById() {
        val race = detailFactory.getRaceById("0.0")

        assertThat(race.name,  `is`(equalTo("Orc")))
        assertThat(race.version,  `is`(equalTo("TEST")))
        assertThat(race.description,  `is`(equalTo("TESTORC")))
        assertThat(race.modifiers[0], `is`(equalTo(Modifier("dex", 2))) )
        assertThat(race.modifiers[1], `is`(equalTo(Modifier("int", 2))) )
    }

    @Test
    fun getRacesByName() {
        val raceList = detailFactory.getRacesByName("Orc")
        assertThat(raceList.count(), `is`(not(equalTo(0))))

        assertThat(raceList[0].name,  `is`(equalTo("Orc")))
        assertThat(raceList[0].version,  `is`(equalTo("TEST")))
        assertThat(raceList[0].description,  `is`(equalTo("TESTORC")))
        assertThat(raceList[0].modifiers[0], `is`(equalTo(Modifier("dex", 2))) )
        assertThat(raceList[0].modifiers[1], `is`(equalTo(Modifier("int", 2))) )

    }

    @Test
    fun getRacesByVersion() {
        val raceList = detailFactory.getRacesByVersion("TEST")
        assertThat(raceList.count(), `is`(not(equalTo(0))))
        assert(raceList.containsAll(detailFactory.hydrateRaces(raceRepository.findAll())))
    }


}
