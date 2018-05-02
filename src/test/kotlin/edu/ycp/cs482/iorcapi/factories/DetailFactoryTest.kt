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
import edu.ycp.cs482.iorcapi.model.ModTools
import edu.ycp.cs482.iorcapi.model.Race
import edu.ycp.cs482.iorcapi.model.RaceQL
import edu.ycp.cs482.iorcapi.model.attributes.Modifier
import edu.ycp.cs482.iorcapi.model.attributes.Stat
import edu.ycp.cs482.iorcapi.model.authentication.AuthorityLevel
import edu.ycp.cs482.iorcapi.model.authentication.Authorizer
import edu.ycp.cs482.iorcapi.model.authentication.PasswordUtils
import edu.ycp.cs482.iorcapi.model.authentication.User
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
    lateinit var userRepository: UserRepository
    lateinit var versionRepository: VersionRepository
    lateinit var passwordUtils: PasswordUtils
    lateinit var salt: ByteArray
    lateinit var context: User

    @Before
    fun setUp() {
        classRepository = RepositoryFactoryBuilder.builder().mock(ClassRepository::class.java)
        raceRepository = RepositoryFactoryBuilder.builder().mock(RaceRepository::class.java)
        statRepository = RepositoryFactoryBuilder.builder().mock(StatRepository::class.java)
        versionRepository = RepositoryFactoryBuilder.builder().mock(VersionRepository::class.java)
        versionInfoRepository = RepositoryFactoryBuilder.builder().mock(VersionInfoRepository::class.java)
        userRepository = RepositoryFactoryBuilder.builder().mock(UserRepository::class.java)
        versionFactory = VersionFactory(statRepository, versionInfoRepository, versionRepository, Authorizer())
        passwordUtils = PasswordUtils()
        salt = passwordUtils.generateSalt(32)
        addTestUsers()
        addTestVersion()
        addTestClasses()
        addTestRaces()
        detailFactory = DetailFactory(raceRepository, classRepository, versionFactory, Authorizer())
    }

    private fun addTestUsers(){
        userRepository.save(listOf(
                User(id= "TESTUSER",
                        email = "test@test.com",
                        uname = "test_daddy",
                        authorityLevels = listOf(AuthorityLevel.ROLE_USER),
                        passwordHash = passwordUtils.hashPassword("TEST".toCharArray(), salt),
                        passwordSalt = salt
                ),
                User(id= "TESTUSER2",
                        email = "test_admin@test.com",
                        uname = "test_boii2",
                        authorityLevels = listOf(AuthorityLevel.ROLE_ADMIN),
                        passwordHash = passwordUtils.hashPassword("TEST".toCharArray(), salt),
                        passwordSalt = salt
                ),
                User(id= "TESTUSER3",
                        email = "test_dude@test.com",
                        uname = "test_boii3",
                        authorityLevels = listOf(AuthorityLevel.ROLE_USER),
                        passwordHash = passwordUtils.hashPassword("TEST".toCharArray(), salt),
                        passwordSalt = salt
                )
        ))
        context = userRepository.findOne("TESTUSER3")

    }

    fun addTestVersion(){
        versionFactory.createVersion("TEST", context)
        statRepository.save(listOf(
                Stat(
                        id= "hpTEST",
                        name= "hp",
                        fname = "Health Points",
                        description = "health points",
                        version = "TEST",
                        skill = false
                ),
                Stat(
                        id= "willTEST",
                        name= "will",
                        fname = "Willpower",
                        description = "Willpower",
                        version = "TEST",
                        skill = false
                ),
                Stat(
                        id= "fortTEST",
                        name= "fort",
                        fname = "Fortitude",
                        description = "Fortitude",
                        version = "TEST",
                        skill = false
                ),
                Stat(
                        id ="historyTEST",
                        name = "history",
                        fname = "History",
                        description = "History",
                        version = "TEST",
                        skill = true
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
                        version = versionFactory.hydrateVersion("TEST"),
                        description = "TESTHALFELF",
                        context = context
                        )
        assertThat(race.name, `is`(equalTo("Half-Elf")))
        assertThat(race.version,  `is`(equalTo("TEST")))
        assertThat(race.description,  `is`(equalTo("TESTHALFELF")))

        //this assumes that the first return will be our own, do not insert anything with this name before here
        val repoRace = raceRepository.findByNameAndVersion("Half-Elf", "TEST")[0]

        assertThat(repoRace, notNullValue())
        assertThat(repoRace.name, `is`(equalTo(race.name)))
        assertThat(repoRace.version,  `is`(equalTo(race.version)))
        assertThat(repoRace.description,  `is`(equalTo(race.description)))

    }
    @Test
    fun updateRace(){
        val race = detailFactory.updateRace(
                id = "1.0",
                version = versionFactory.hydrateVersion("TEST"),
                name = "Human",
                description = "TESTHUMAN1",
                context = context
        )

        assertThat(race.name, `is`(equalTo("Human")))
        assertThat(race.version,  `is`(equalTo("TEST")))
        assertThat(race.description,  `is`(equalTo("TESTHUMAN1")))

        //this assumes that the first return will be our own, do not insert anything with this name before here
        val repoRace = raceRepository.findByNameAndVersion("Human", "TEST")[0]

        assertThat(repoRace, notNullValue())
        assertThat(repoRace.name, `is`(equalTo(race.name)))
        assertThat(repoRace.version,  `is`(equalTo(race.version)))
        assertThat(repoRace.description,  `is`(equalTo(race.description)))


        val raceRevert = detailFactory.updateRace(
                id = "1.0",
                version = versionFactory.hydrateVersion("TEST"),
                name = "Human",
                description = "TESTHUMAN",
                context = context
        )

        assertThat(raceRevert.name, `is`(equalTo("Human")))
        assertThat(raceRevert.version,  `is`(equalTo("TEST")))
        assertThat(raceRevert.description,  `is`(equalTo("TESTHUMAN")))

    }

    @Test
    fun getRaceById() {
        val race = detailFactory.getRaceById("0.0", versionFactory.hydrateVersion("TEST"), context)
        val race2 = detailFactory.getRaceById("1.0", versionFactory.hydrateVersion("TEST"), context)

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
        val raceList = detailFactory.getRacesByName("Orc", versionFactory.hydrateVersion("TEST"), context)
        val raceList2 = detailFactory.getRacesByName("Human", versionFactory.hydrateVersion("TEST"), context)
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
        val raceList = detailFactory.getRacesByVersion(versionFactory.hydrateVersion("TEST"), context)
        assertThat(raceList.count(), `is`(not(equalTo(0))))
        assert(raceList.containsAll(detailFactory.hydrateRaces(raceRepository.findAll())))
    }

    @Test
    fun createNewClass(){
        val classRpg = detailFactory.createNewClass(
                name = "Fighter",
                version = versionFactory.hydrateVersion("TEST"),
                role = "DEFENDER",
                description = "TESTFIGHTER",
                context = context
        )
        assertThat(classRpg.name, `is`(equalTo("Fighter")))
        assertThat(classRpg.version,  `is`(equalTo("TEST")))
        assertThat(classRpg.role,  `is`(equalTo("DEFENDER")))
        assertThat(classRpg.description,  `is`(equalTo("TESTFIGHTER")))

        //this assumes that the first return will be our own, do not insert anything with this name before here
        val repoClass = classRepository.findByNameAndVersion("Fighter", "TEST")[0] //?: throw RuntimeException()

        assertThat(repoClass, notNullValue())
        assertThat(repoClass.name, `is`(equalTo(classRpg.name)))
        assertThat(repoClass.version,  `is`(equalTo(classRpg.version)))
        assertThat(repoClass.role,  `is`(equalTo(classRpg.role)))
        assertThat(repoClass.description,  `is`(equalTo(classRpg.description)))
    }

    @Test
    fun updateClass(){
        val classRpg = detailFactory.updateClass(
                id = "0.1",
                name = "Cleric",
                role= "Healer",
                version = versionFactory.hydrateVersion("TEST"),
                description = "TESTCLERIC2",
                context = context
        )

        assertThat(classRpg.name, `is`(equalTo("Cleric")))
        assertThat(classRpg.version,  `is`(equalTo("TEST")))
        assertThat(classRpg.description,  `is`(equalTo("TESTCLERIC2")))

        //this assumes that the first return will be our own, do not insert anything with this name before here
        val repoClassRpg = classRepository.findByNameAndVersion("Cleric", "TEST")[0]

        assertThat(repoClassRpg, notNullValue())
        assertThat(repoClassRpg.name, `is`(equalTo(classRpg.name)))
        assertThat(repoClassRpg.version,  `is`(equalTo(classRpg.version)))
        assertThat(repoClassRpg.description,  `is`(equalTo(classRpg.description)))


        val classRevert = detailFactory.updateClass(
                id = "0.1",
                name = "Cleric",
                role= "Healer",
                version = versionFactory.hydrateVersion("TEST"),
                description = "TESTCLERIC",
                context = context
        )
        assertThat(classRevert.name, `is`(equalTo("Cleric")))
        assertThat(classRevert.version,  `is`(equalTo("TEST")))
        assertThat(classRevert.description,  `is`(equalTo("TESTCLERIC")))

    }
    @Test
    fun getClassById(){
        val classRpg = detailFactory.getClassById("1.1", versionFactory.hydrateVersion("TEST"), context)
        val classRpg2 = detailFactory.getClassById("0.1", versionFactory.hydrateVersion("TEST"), context)

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
        val classRpgList1 = detailFactory.getClassesByName("Ranger", versionFactory.hydrateVersion("TEST"), context)
        val classRpgList2 = detailFactory.getClassesByName("Cleric", versionFactory.hydrateVersion("TEST"), context)

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
        val classList = detailFactory.getClassesByVersion(versionFactory.hydrateVersion("TEST"), context)
        assertThat(classList.count(), `is`(not(equalTo(0))))
        assert(classList.containsAll(detailFactory.hydrateClasses(classRepository.findAll())))
    }

    @Test
    fun addRemoveModifiers(){
        val race = detailFactory.addRaceModifiers("0.0", hashMapOf(Pair("wis", 2f)), versionFactory.hydrateVersion("TEST"), context)

        assertThat(race.modifiers.count(), `is`(equalTo(3)) )

        assertThat(race.name,  `is`(equalTo("Orc")))
        assertThat(race.version,  `is`(equalTo("TEST")))
        assertThat(race.description,  `is`(equalTo("TESTORC")))
        assertThat(race.modifiers.contains(Modifier("wis", 2f)), `is`(true))
        assertThat(race.modifiers.contains(Modifier("dex", 2f)), `is`(true))
        assertThat(race.modifiers.contains(Modifier("int", 2f)), `is`(true))

        val race2 = detailFactory.removeRaceModifier("0.0", "wis", versionFactory.hydrateVersion("TEST"), context)

        try {
            detailFactory.addRaceModifiers("0.0", hashMapOf(Pair("kit", 2f)), versionFactory.hydrateVersion("TEST"), context)
        } catch (e: GraphQLException) {
            assertThat(e.message, `is`(equalTo("This Modifier is not in the version sheet!")))
        }

        assertThat(race2.modifiers.count(), `is`(equalTo(2)) )

        assertThat(race2.modifiers.contains(Modifier("dex", 2f)), `is`(true))
        assertThat(race2.modifiers.contains(Modifier("int", 2f)), `is`(true))
        assertThat(race2.modifiers.contains(Modifier("wis", 2f)), `is`(false))

        val classRpg = detailFactory.addClassModifiers("1.1", hashMapOf(Pair("wis", 2f)), versionFactory.hydrateVersion("TEST"), context)

        assertThat(classRpg.modifiers.count(), `is`(equalTo(3)) )

        assertThat(classRpg.name,  `is`(equalTo("Ranger")))
        assertThat(classRpg.version,  `is`(equalTo("TEST")))
        assertThat(classRpg.description,  `is`(equalTo("TESTRANGER")))
        assertThat(classRpg.role, `is`(equalTo("Combatant")))
        assertThat(classRpg.modifiers.contains(Modifier("hp", 12f)), `is`(true))
        assertThat(classRpg.modifiers.contains(Modifier("will", 2f)), `is`(true))
        assertThat(classRpg.modifiers.contains(Modifier("wis", 2f)), `is`(true))

        val classRpg2 = detailFactory.removeClassModifier("1.1", "wis", versionFactory.hydrateVersion("TEST"), context)

        assertThat(classRpg2.modifiers.count(), `is`(equalTo(2)) )
        assertThat(classRpg2.modifiers.contains(Modifier("hp", 12f)), `is`(true))
        assertThat(classRpg2.modifiers.contains(Modifier("will", 2f)), `is`(true))
        assertThat(classRpg2.modifiers.contains(Modifier("wis", 2f)), `is`(false))


    }

}
