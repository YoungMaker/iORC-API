package edu.ycp.cs482.iorcapi.factories

import com.mmnaseri.utils.spring.data.dsl.factory.RepositoryFactoryBuilder
import edu.ycp.cs482.iorcapi.model.attributes.Stat
import edu.ycp.cs482.iorcapi.model.attributes.StatQL
import edu.ycp.cs482.iorcapi.model.authentication.*
import edu.ycp.cs482.iorcapi.repositories.StatRepository
import edu.ycp.cs482.iorcapi.repositories.UserRepository
import edu.ycp.cs482.iorcapi.repositories.VersionInfoRepository
import edu.ycp.cs482.iorcapi.repositories.VersionRepository
import graphql.GraphQLException
import org.hamcrest.Matchers.*
import org.junit.After
import org.junit.Before
import org.junit.Test

import org.junit.Assert.*

class VersionFactoryTest {

    lateinit var statRepository: StatRepository
    lateinit var versionInfoRepository: VersionInfoRepository
    lateinit var versionFactory: VersionFactory
    lateinit var userRepository: UserRepository
    //lateinit var userFactory: UserFactory
    lateinit var passwordUtils: PasswordUtils
    lateinit var versionRepository: VersionRepository
    lateinit var salt: ByteArray
    lateinit var context: User


    @Before
    fun setUp() {
        statRepository = RepositoryFactoryBuilder.builder().mock(StatRepository::class.java)
        versionInfoRepository = RepositoryFactoryBuilder.builder().mock(VersionInfoRepository::class.java)
        userRepository = RepositoryFactoryBuilder.builder().mock(UserRepository::class.java)
        versionRepository = RepositoryFactoryBuilder.builder().mock(VersionRepository::class.java)
        passwordUtils = PasswordUtils()
        //userFactory = UserFactory(userRepository, passwordUtils, JwtUtils())
        versionFactory = VersionFactory(statRepository, versionInfoRepository, versionRepository, Authorizer())
        salt = passwordUtils.generateSalt(32)
        addTestUsers()
        addTestVersion()
    }


    private fun addTestUsers(){
        userRepository.save(listOf(
                User(id= "TESTUSER",
                        email = "test@test.com",
                        uname = "test_daddy", //user is banned
                        authorityLevels = listOf(),
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

    @After
    fun tearDown() {
        statRepository.deleteAll()
    }

    @Test
    fun getVersionSkills() {
        val user2 = userRepository.findByUname("test_daddy") ?: throw RuntimeException()

        val skillList = versionFactory.getVersionSkills(versionFactory.hydrateVersion("TEST"), context).stats

        assertThat(skillList.count(), `is`(equalTo(1)))

        assertThat(skillList[0].key, `is`(equalTo("history")))
        assertThat(skillList[0].description,`is`(equalTo("History")))
        assertThat(skillList[0].skill, `is`(true))

        try {//users that don't have ROLE_USER don't have MODE_VIEW
            versionFactory.getVersionSkills(versionFactory.hydrateVersion("TEST"), user2)
            fail()
        } catch (e: GraphQLException){
            assertThat(e.message, `is`(equalTo("Forbidden")))
        }

    }

    @Test
    fun addStatToVersion() {
        val versionSheet = versionFactory.addStatToVersion("rec", "Recognition", "Recognition", versionFactory.hydrateVersion("TEST") , true)
        assertThat(versionSheet.version, `is`(equalTo("TEST")))
        assertThat(versionSheet.stats.contains(StatQL("rec", "Recognition", "Recognition", true, mutableListOf())), `is`(true))

        val skill = versionSheet.stats[versionSheet.stats.indexOf(StatQL("rec", "Recognition", "Recognition", true, mutableListOf()))]
        val repoSkill = statRepository.findById("recTEST")

        assertThat(repoSkill, notNullValue())
        assertThat(repoSkill?.name, `is`(equalTo(skill.key)))
        assertThat(repoSkill?.description, `is`(equalTo(skill.description)))
        assertThat(repoSkill?.skill, `is`(equalTo(skill.skill)))
    }

    @Test
    fun addStatModifiers() {
       // val versionSheet = versionFactory.addStatModifiers("test_mod", versionFactory.hydrateVersion("TEST") mods = listOf())
    }

    @Test
    fun removeStatModifier(){

    }

}