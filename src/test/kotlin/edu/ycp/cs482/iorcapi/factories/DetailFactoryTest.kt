package edu.ycp.cs482.iorcapi.factories

import edu.ycp.cs482.iorcapi.model.ClassRpg
import org.hamcrest.CoreMatchers.*
import org.hamcrest.CoreMatchers.notNullValue
import org.junit.After
import org.junit.Before
import org.junit.Test

import org.junit.Assert.*

import org.springframework.boot.test.context.SpringBootTest

import com.mmnaseri.utils.spring.data.dsl.factory.RepositoryFactoryBuilder
import edu.ycp.cs482.iorcapi.model.Item
import edu.ycp.cs482.iorcapi.model.ItemQL
import edu.ycp.cs482.iorcapi.model.Race
import edu.ycp.cs482.iorcapi.model.attributes.Modifier
import edu.ycp.cs482.iorcapi.model.attributes.ObjType
import edu.ycp.cs482.iorcapi.model.attributes.Stat
import edu.ycp.cs482.iorcapi.repositories.*
import graphql.GraphQLException


@SpringBootTest
class DetailFactoryTest {

    lateinit var classRepository: ClassRepository
    lateinit var raceRepository: RaceRepository
    lateinit var detailFactory: DetailFactory
    lateinit var statRepository: StatRepository
    lateinit var versionInfoRepository: VersionInfoRepository
    lateinit var versionFactory: VersionFactory
    lateinit var itemRepository: ItemRepository
    lateinit var itemFactory: ItemFactory

    @Before
    fun setUp() {
        classRepository = RepositoryFactoryBuilder.builder().mock(ClassRepository::class.java)
        raceRepository = RepositoryFactoryBuilder.builder().mock(RaceRepository::class.java)
        statRepository = RepositoryFactoryBuilder.builder().mock(StatRepository::class.java)
        versionInfoRepository = RepositoryFactoryBuilder.builder().mock(VersionInfoRepository::class.java)
        itemRepository = RepositoryFactoryBuilder.builder().mock(ItemRepository::class.java)
        versionFactory = VersionFactory(statRepository, versionInfoRepository)
        itemFactory = ItemFactory(itemRepository)
        addTestVersion()
        addTestClasses()
        addTestRaces()
        detailFactory = DetailFactory(raceRepository, classRepository, versionFactory, itemFactory)
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
        itemRepository.save(listOf(
                Item(
                        id="TESTFEAT",
                        name = "Accidental Tells",
                        description = "When making Insight checks, roll twice if target is in range of Mantle of Misfortune.",
                        price = 0.0f,
                        modifiers = mapOf(),
                        itemClasses = listOf("feat_tiefling", "passive"),
                        version = "TEST",
                        type = ObjType.ITEM_FEAT
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
                        role = "Healer",
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


    @After
    fun tearDown() {
        classRepository.deleteAll()
        raceRepository.deleteAll()
        statRepository.deleteAll()
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
        assertThat(race.feats.isEmpty(), `is`(true))

        //this assumes that the first return will be our own, do not insert anything with this name before here
        val repoRace = raceRepository.findByName("Half-Elf")[0]

        assertThat(repoRace, notNullValue())
        assertThat(repoRace.name, `is`(equalTo(race.name)))
        assertThat(repoRace.version,  `is`(equalTo(race.version)))
        assertThat(repoRace.description,  `is`(equalTo(race.description)))

    }
    @Test
    fun updateRace(){
        val race = detailFactory.updateRace(
                id = "1.0",
                version = "TEST",
                name = "Human",
                description = "TESTHUMAN1"
        )

        assertThat(race.name, `is`(equalTo("Human")))
        assertThat(race.version,  `is`(equalTo("TEST")))
        assertThat(race.description,  `is`(equalTo("TESTHUMAN1")))

        //this assumes that the first return will be our own, do not insert anything with this name before here
        val repoRace = raceRepository.findByName("Human")[0]

        assertThat(repoRace, notNullValue())
        assertThat(repoRace.name, `is`(equalTo(race.name)))
        assertThat(repoRace.version,  `is`(equalTo(race.version)))
        assertThat(repoRace.description,  `is`(equalTo(race.description)))


        val raceRevert = detailFactory.updateRace(
                id = "1.0",
                version = "TEST",
                name = "Human",
                description = "TESTHUMAN"
        )

        assertThat(raceRevert.name, `is`(equalTo("Human")))
        assertThat(raceRevert.version,  `is`(equalTo("TEST")))
        assertThat(raceRevert.description,  `is`(equalTo("TESTHUMAN")))

    }


    @Test
    fun addRaceFeats(){
        val race = raceRepository.findById("1.0")
        assertThat(race!!.name, `is`(equalTo("Human")))
        assertThat(race.version,  `is`(equalTo("TEST")))
        assertThat(race.description,  `is`(equalTo("TESTHUMAN")))
        assertThat(race.feats.isEmpty(), `is`(true))

        val race2 = detailFactory.addRaceFeats("1.0", listOf("TESTFEAT") )

        assertThat(race2.feats.contains( ItemQL(
                id="TESTFEAT",
                name = "Accidental Tells",
                description = "When making Insight checks, roll twice if target is in range of Mantle of Misfortune.",
                price = 0.0f,
                modifiers = listOf(),
                itemClasses = listOf("feat_tiefling", "passive"),
                version = "TEST",
                type = ObjType.ITEM_FEAT
        )), `is`(true))

        try{
            detailFactory.addRaceFeats("1.0", listOf("fasdfasd") ) //try to add feat that doesnt' exist
            fail()
        } catch (e: GraphQLException){
            assertThat(e.message, `is`(equalTo("Item Does not exist in that version with that name")))
        }
    }

    @Test
    fun getRaceById() {
        val race = detailFactory.getRaceById("0.0")
        val race2 = detailFactory.getRaceById("1.0")

        assertThat(race.name,  `is`(equalTo("Orc")))
        assertThat(race.version,  `is`(equalTo("TEST")))
        assertThat(race.description,  `is`(equalTo("TESTORC")))
        assertThat(race.modifiers.contains(Modifier("dex", 2f)), `is`(true))
        assertThat(race.modifiers.contains(Modifier("int", 2f)), `is`(true))

        assertThat(race2.name,  `is`(equalTo("Human")))
        assertThat(race2.version,  `is`(equalTo("TEST")))
        assertThat(race2.description,  `is`(equalTo("TESTHUMAN")))
        assertThat(race2.modifiers.contains(Modifier("wis", 2f)), `is`(true))
        assertThat(race2.modifiers.contains(Modifier("int", 2f)), `is`(true))
    }

    @Test
    fun getRacesByName() {
        val raceList = detailFactory.getRacesByName("Orc")
        val raceList2 = detailFactory.getRacesByName("Human")
        assertThat(raceList.count(), `is`(not(equalTo(0))))

        assertThat(raceList[0].name,  `is`(equalTo("Orc")))
        assertThat(raceList[0].version,  `is`(equalTo("TEST")))
        assertThat(raceList[0].description,  `is`(equalTo("TESTORC")))
        assertThat(raceList[0].modifiers.contains(Modifier("dex", 2f)), `is`(true))
        assertThat(raceList[0].modifiers.contains(Modifier("int", 2f)), `is`(true))

        assertThat(raceList2[0].name, `is`(equalTo("Human")))
        assertThat(raceList2[0].version, `is`(equalTo("TEST")))
        assertThat(raceList2[0].description, `is`(equalTo("TESTHUMAN")))
        assertThat(raceList2[0].modifiers.contains(Modifier("wis", 2f)), `is`(true))
        assertThat(raceList2[0].modifiers.contains(Modifier("int", 2f)), `is`(true))
    }

    @Test
    fun getRacesByVersion() {
        val raceList = detailFactory.getRacesByVersion("TEST")
        assertThat(raceList.count(), `is`(not(equalTo(0))))
        assert(raceList.containsAll(detailFactory.hydrateRaces(raceRepository.findAll())))
    }

    @Test
    fun createNewClass(){
        val classRpg = detailFactory.createNewClass(
                name = "Fighter",
                version = "TEST",
                role = "DEFENDER",
                description = "TESTFIGHTER"
        )
        assertThat(classRpg.name, `is`(equalTo("Fighter")))
        assertThat(classRpg.version,  `is`(equalTo("TEST")))
        assertThat(classRpg.role,  `is`(equalTo("DEFENDER")))
        assertThat(classRpg.description,  `is`(equalTo("TESTFIGHTER")))
        assertThat(classRpg.feats.isEmpty(), `is`(true))

        //this assumes that the first return will be our own, do not insert anything with this name before here
        val repoClass = classRepository.findByName("Fighter")[0]

        assertThat(repoClass, notNullValue())
        assertThat(repoClass.name, `is`(equalTo(classRpg.name)))
        assertThat(repoClass.version,  `is`(equalTo(classRpg.version)))
        assertThat(repoClass.role,  `is`(equalTo(classRpg.role)))
        assertThat(repoClass.description,  `is`(equalTo(classRpg.description)))
    }

    @Test
    fun addClassFeats(){
        val classRpg = classRepository.findById("0.1")
        assertThat(classRpg!!.name, `is`(equalTo("Cleric")))
        assertThat(classRpg.version,  `is`(equalTo("TEST")))
        assertThat(classRpg.role,  `is`(equalTo("Healer")))
        assertThat(classRpg.description,  `is`(equalTo("TESTCLERIC")))
        assertThat(classRpg.feats.isEmpty(), `is`(true))

        val class2Rpg = detailFactory.addClassFeats("0.1", listOf("TESTFEAT") )

        assertThat(class2Rpg.feats.contains( ItemQL(
                id="TESTFEAT",
                name = "Accidental Tells",
                description = "When making Insight checks, roll twice if target is in range of Mantle of Misfortune.",
                price = 0.0f,
                modifiers = listOf(),
                itemClasses = listOf("feat_tiefling", "passive"),
                version = "TEST",
                type = ObjType.ITEM_FEAT
        )), `is`(true))

        try{
            detailFactory.addClassFeats("0.1", listOf("fasdfasd") ) //try to add feat that doesnt' exist
            fail()
        } catch (e: GraphQLException){
            assertThat(e.message, `is`(equalTo("Item Does not exist in that version with that name")))
        }
    }

    @Test
    fun updateClass(){
        val classRpg = detailFactory.updateClass(
                id = "0.1",
                name = "Cleric",
                role= "Healer",
                version = "TEST",
                description = "TESTCLERIC2"
        )

        assertThat(classRpg.name, `is`(equalTo("Cleric")))
        assertThat(classRpg.version,  `is`(equalTo("TEST")))
        assertThat(classRpg.description,  `is`(equalTo("TESTCLERIC2")))

        //this assumes that the first return will be our own, do not insert anything with this name before here
        val repoClassRpg = classRepository.findByName("Cleric")[0]

        assertThat(repoClassRpg, notNullValue())
        assertThat(repoClassRpg.name, `is`(equalTo(classRpg.name)))
        assertThat(repoClassRpg.version,  `is`(equalTo(classRpg.version)))
        assertThat(repoClassRpg.description,  `is`(equalTo(classRpg.description)))


        val classRevert = detailFactory.updateClass(
                id = "0.1",
                name = "Cleric",
                role= "Healer",
                version = "TEST",
                description = "TESTCLERIC"
        )
        assertThat(classRevert.name, `is`(equalTo("Cleric")))
        assertThat(classRevert.version,  `is`(equalTo("TEST")))
        assertThat(classRevert.description,  `is`(equalTo("TESTCLERIC")))

    }
    @Test
    fun getClassById(){
        val classRpg = detailFactory.getClassById("1.1")
        val classRpg2 = detailFactory.getClassById("0.1")

        assertThat(classRpg.name,  `is`(equalTo("Ranger")))
        assertThat(classRpg.version,  `is`(equalTo("TEST")))
        assertThat(classRpg.description,  `is`(equalTo("TESTRANGER")))
        assertThat(classRpg.role, `is`(equalTo("Combatant")))
        assertThat(classRpg.modifiers.contains(Modifier("hp", 12f)), `is`(true))
        assertThat(classRpg.modifiers.contains(Modifier("will", 2f)), `is`(true))

        assertThat(classRpg2.name,  `is`(equalTo("Cleric")))
        assertThat(classRpg2.version,  `is`(equalTo("TEST")))
        assertThat(classRpg2.description,  `is`(equalTo("TESTCLERIC")))
        assertThat(classRpg2.role, `is`(equalTo("Healer")))
        assertThat(classRpg2.modifiers.contains(Modifier("hp", 12f)), `is`(true))
        assertThat(classRpg2.modifiers.contains(Modifier("fort", 2f)), `is`(true))
    }


    @Test
    fun getClassesByName(){
        val classRpgList1 = detailFactory.getClassesByName("Ranger")
        val classRpgList2 = detailFactory.getClassesByName("Cleric")

        assertThat(classRpgList1.count(), `is`(not(equalTo(0))))
        assertThat(classRpgList1.count(), `is`(not(equalTo(0))))

        val classRpg = classRpgList1[0]
        val classRpg2 = classRpgList2[0]

        assertThat(classRpg.name,  `is`(equalTo("Ranger")))
        assertThat(classRpg.version,  `is`(equalTo("TEST")))
        assertThat(classRpg.description,  `is`(equalTo("TESTRANGER")))
        assertThat(classRpg.role, `is`(equalTo("Combatant")))
        assertThat(classRpg.modifiers.contains(Modifier("hp", 12f)), `is`(true))
        assertThat(classRpg.modifiers.contains(Modifier("will", 2f)), `is`(true))

        assertThat(classRpg2.name,  `is`(equalTo("Cleric")))
        assertThat(classRpg2.version,  `is`(equalTo("TEST")))
        assertThat(classRpg2.description,  `is`(equalTo("TESTCLERIC")))
        assertThat(classRpg2.role, `is`(equalTo("Healer")))
        assertThat(classRpg2.modifiers.contains(Modifier("hp", 12f)), `is`(true))
        assertThat(classRpg2.modifiers.contains(Modifier("fort", 2f)), `is`(true))
    }

    @Test
    fun getClassesByVersion() {
        val classList = detailFactory.getClassesByVersion("TEST")
        assertThat(classList.count(), `is`(not(equalTo(0))))
        assert(classList.containsAll(detailFactory.hydrateClasses(classRepository.findAll())))
    }

    @Test
    fun addRemoveModifiers(){
        val race = detailFactory.addRaceModifiers("0.0", hashMapOf(Pair("wis", 2f)))

        assertThat(race.modifiers.count(), `is`(equalTo(3)) )

        assertThat(race.name,  `is`(equalTo("Orc")))
        assertThat(race.version,  `is`(equalTo("TEST")))
        assertThat(race.description,  `is`(equalTo("TESTORC")))
        assertThat(race.modifiers.contains(Modifier("wis", 2f)), `is`(true))
        assertThat(race.modifiers.contains(Modifier("dex", 2f)), `is`(true))
        assertThat(race.modifiers.contains(Modifier("int", 2f)), `is`(true))

        val race2 = detailFactory.removeRaceModifier("0.0", "wis")

        try {
            detailFactory.addRaceModifiers("0.0", hashMapOf(Pair("kit", 2f)))
        } catch (e: GraphQLException) {
            assertThat(e.message, `is`(equalTo("This Modifier is not in the version sheet!")))
        }

        assertThat(race2.modifiers.count(), `is`(equalTo(2)) )

        assertThat(race2.modifiers.contains(Modifier("dex", 2f)), `is`(true))
        assertThat(race2.modifiers.contains(Modifier("int", 2f)), `is`(true))
        assertThat(race2.modifiers.contains(Modifier("wis", 2f)), `is`(false))

        val classRpg = detailFactory.addClassModifiers("1.1", hashMapOf(Pair("wis", 2f)))

        assertThat(classRpg.modifiers.count(), `is`(equalTo(3)) )

        assertThat(classRpg.name,  `is`(equalTo("Ranger")))
        assertThat(classRpg.version,  `is`(equalTo("TEST")))
        assertThat(classRpg.description,  `is`(equalTo("TESTRANGER")))
        assertThat(classRpg.role, `is`(equalTo("Combatant")))
        assertThat(classRpg.modifiers.contains(Modifier("hp", 12f)), `is`(true))
        assertThat(classRpg.modifiers.contains(Modifier("will", 2f)), `is`(true))
        assertThat(classRpg.modifiers.contains(Modifier("wis", 2f)), `is`(true))

        val classRpg2 = detailFactory.removeClassModifier("1.1", "wis")

        assertThat(classRpg2.modifiers.count(), `is`(equalTo(2)) )
        assertThat(classRpg2.modifiers.contains(Modifier("hp", 12f)), `is`(true))
        assertThat(classRpg2.modifiers.contains(Modifier("will", 2f)), `is`(true))
        assertThat(classRpg2.modifiers.contains(Modifier("wis", 2f)), `is`(false))


    }

}
