package edu.ycp.cs482.iorcapi.factories

import com.mmnaseri.utils.spring.data.dsl.factory.RepositoryFactoryBuilder
import edu.ycp.cs482.iorcapi.model.attributes.Stat
import edu.ycp.cs482.iorcapi.model.attributes.StatQL
import edu.ycp.cs482.iorcapi.repositories.StatRepository
import org.hamcrest.Matchers.*
import org.junit.After
import org.junit.Before
import org.junit.Test

import org.junit.Assert.*

class VersionFactoryTest {

    lateinit var statRepository: StatRepository
    lateinit var versionFactory: VersionFactory


    @Before
    fun setUp() {
        statRepository = RepositoryFactoryBuilder.builder().mock(StatRepository::class.java)
        versionFactory = VersionFactory(statRepository)
        addTestVersion()
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
                ),
                Stat(
                        id ="historyTEST",
                        name = "history",
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
        val skillList = versionFactory.getVersionSkills("TEST").stats

        assertThat(skillList.count(), `is`(equalTo(1)))

        assertThat(skillList[0].name, `is`(equalTo("history")))
        assertThat(skillList[0].description,`is`(equalTo("History")))
        assertThat(skillList[0].skill, `is`(true))


    }

    @Test
    fun addStatToVersion() {
        val versionSheet = versionFactory.addStatToVersion("rec", "Recognition", "TEST", true)
        assertThat(versionSheet.version, `is`(equalTo("TEST")))
        assertThat(versionSheet.stats.contains(StatQL("rec", "Recognition", true, mutableListOf())), `is`(true))

        val skill = versionSheet.stats[versionSheet.stats.indexOf(StatQL("rec", "Recognition", true, mutableListOf()))]
        val repoSkill = statRepository.findById("recTEST")

        assertThat(repoSkill, notNullValue())
        assertThat(repoSkill?.name, `is`(equalTo(skill.name)))
        assertThat(repoSkill?.description, `is`(equalTo(skill.description)))
        assertThat(repoSkill?.skill, `is`(equalTo(skill.skill)))
    }

    @Test
    fun addStatModifiers() {

    }

    @Test
    fun removeStatModifier(){

    }

}