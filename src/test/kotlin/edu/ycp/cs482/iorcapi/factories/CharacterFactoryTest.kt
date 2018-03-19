package edu.ycp.cs482.iorcapi.factories


import com.mmnaseri.utils.spring.data.dsl.factory.RepositoryFactoryBuilder
import edu.ycp.cs482.iorcapi.model.*
import edu.ycp.cs482.iorcapi.model.attributes.*
import edu.ycp.cs482.iorcapi.repositories.*
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
    lateinit var versionInfoRepository: VersionInfoRepository
    lateinit var versionFactory: VersionFactory
    lateinit var itemRepository: ItemRepository
    lateinit var itemFactory: ItemFactory

    @Before
    fun setUp() {
        classRepository = RepositoryFactoryBuilder.builder().mock(ClassRepository::class.java)
        raceRepository = RepositoryFactoryBuilder.builder().mock(RaceRepository::class.java)
        characterRepository = RepositoryFactoryBuilder.builder().mock(CharacterRepository::class.java)
        statRepository = RepositoryFactoryBuilder.builder().mock(StatRepository::class.java)
        versionInfoRepository = RepositoryFactoryBuilder.builder().mock(VersionInfoRepository::class.java)
        itemRepository = RepositoryFactoryBuilder.builder().mock(ItemRepository::class.java)
        versionFactory = VersionFactory(statRepository, versionInfoRepository)
        addTestItems()
        addTestVersion()
        addTestClasses()
        addTestRaces()
        addTestCharacters()
        detailFactory = DetailFactory(raceRepository, classRepository, versionFactory)
        itemFactory = ItemFactory(itemRepository)
        characterFactory = CharacterFactory(characterRepository, detailFactory, versionFactory, itemFactory)
    }

    @After
    fun tearDown() {
        characterRepository.deleteAll()
        classRepository.deleteAll()
        raceRepository.deleteAll()
        itemRepository.deleteAll()
    }

    private fun addTestItems() {
        itemRepository.save(listOf(
                Item(
                    id = "Battle Axe of the Not so BoldTEST",
                    name = "Battle Axe of the Not so Bold",
                    description = "A battle axe that is wielded people who want a useless item worth way too much money.",
                    price = 999999f,
                    itemClasses = listOf("axe", "military_weapon", "melee_weapon", "twohand_weapon", "hands_left", "hand_right"),
                    version = "TEST",
                    type = ObjType.ITEM_WEAPON
                ),
                Item(
                    id = "Ranged Bow of the Unhinged MarksmanTEST",
                    name = "Ranged Bow of the Unhinged Marksman",
                    description = "A Bow so wildly inaccurate only insane marksmen would buy",
                    price = 350f,
                    itemClasses = listOf("bow", "military_weapon", "ranged_weapon", "mil_weapon", "onehand_weapon", "hand_left", "hand_right"),
                    version = "TEST",
                    type = ObjType.ITEM_WEAPON
        ),Item(
                id = "BucketTEST",
                name = "Bucket of head",
                description = "A bucket you can wear on your head",
                price = 350f,
                itemClasses = listOf("head"),
                modifiers = mapOf(Pair("ac", 1f)),
                version = "TEST",
                type = ObjType.ITEM_ARMOR
                )))

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

        versionInfoRepository.save(listOf(
                VersionInfo(
                        id = "hand_leftTEST",
                        name = "hand_left",
                        type = "slot",
                        value = "left hand item slot",
                        version = "TEST"
                ),
                VersionInfo(
                        id = "hand_rightTEST",
                        name = "hand_right",
                        type = "slot",
                        value = "right hand item slot",
                        version = "TEST"
                ),
                VersionInfo(
                        id = "headTEST",
                        name = "head",
                        type = "slot",
                        value = "head item slot",
                        version = "TEST"
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
                abilityPoints = AbilityInput(13,12,11,15,14,16),
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

   // @Test
//    fun updateName() {
//        val nameUpdate = characterFactory.updateName("1.2","Gerald")
//        assertThat(nameUpdate.name, `is`(equalTo("Gerald")))
//    }

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

    @Test
    fun getCharactersByVersion(){
        val characterList = characterFactory.getCharactersByVersion("TEST")

        assertThat(characterList.count(), `is`(equalTo(2)))

        val character = characterList[1]

        assertThat(character.name,  `is`(equalTo("Cregan the Destroyer of Worlds")))
        assertThat(character.abilityPoints,  `is`(equalTo(Ability(12, 14, 15, 11, 12, 14))))
        assertThat(character.race.name,  `is`(equalTo("Orc")))
    }

    @Test
    fun testVersionSlots() {
        val character = characterFactory.createNewCharacter(
                abilityPoints = AbilityInput(12,14,11,15,14,16),
                name = "Jerome Stefan",
                classid = "0.1",
                raceid = "1.0",
                version = "TEST"
        )
        assertThat(character.slots.count(), `is`(not(0)))
        assertThat(character.slots[0].name, `is`(equalTo("hand_left")))
        assertThat(character.slots[0].empty, `is`(false))


        val newCharacter = characterFactory.addItemToCharacter(character.id, "BucketTEST")
        assertThat(newCharacter.inventory.count(), `is`(not(0)))

        val equipChar = characterFactory.equipItem(character.id, "BucketTEST", "head")

        assertThat(equipChar.slots[2].empty, `is`(false))
        assertThat(equipChar.slots[2].name, `is`(equalTo("head")))
        assertThat(equipChar.slots[2].item, `is`(equalTo(ItemQL(id = "BucketTEST",
                name = "Bucket of head",
                description = "A bucket you can wear on your head",
                price = 350f,
                itemClasses = listOf("head"),
                modifiers = listOf(Modifier("ac", 1f)),
                version = "TEST",
                type = ObjType.ITEM_ARMOR))))

    }


}
