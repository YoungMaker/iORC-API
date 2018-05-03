package edu.ycp.cs482.iorcapi.factories


import com.mmnaseri.utils.spring.data.dsl.factory.RepositoryFactoryBuilder
import edu.ycp.cs482.iorcapi.model.*
import edu.ycp.cs482.iorcapi.model.attributes.*
import edu.ycp.cs482.iorcapi.model.authentication.*
import edu.ycp.cs482.iorcapi.repositories.*
import graphql.GraphQLException
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
    lateinit var userRepository: UserRepository
    lateinit var versionRepository: VersionRepository
    lateinit var passwordUtils: PasswordUtils
    lateinit var salt: ByteArray
    lateinit var context: User

    @Before
    fun setUp() {
        classRepository = RepositoryFactoryBuilder.builder().mock(ClassRepository::class.java)
        raceRepository = RepositoryFactoryBuilder.builder().mock(RaceRepository::class.java)
        characterRepository = RepositoryFactoryBuilder.builder().mock(CharacterRepository::class.java)
        statRepository = RepositoryFactoryBuilder.builder().mock(StatRepository::class.java)
        versionInfoRepository = RepositoryFactoryBuilder.builder().mock(VersionInfoRepository::class.java)
        userRepository = RepositoryFactoryBuilder.builder().mock(UserRepository::class.java)
        itemRepository = RepositoryFactoryBuilder.builder().mock(ItemRepository::class.java)
        versionRepository = RepositoryFactoryBuilder.builder().mock(VersionRepository::class.java)
        versionFactory = VersionFactory(statRepository, versionInfoRepository, versionRepository, Authorizer())
        passwordUtils = PasswordUtils()
        salt = passwordUtils.generateSalt(32)
        addTestUsers()
        addTestItems()
        addTestVersion()
        addTestClasses()
        addTestRaces()
        addTestCharacters()
        detailFactory = DetailFactory(raceRepository, classRepository, versionFactory, Authorizer())
        itemFactory = ItemFactory(itemRepository, Authorizer())
        characterFactory = CharacterFactory(characterRepository, detailFactory, versionFactory, itemFactory, Authorizer())
    }

    @After
    fun tearDown() {
        characterRepository.deleteAll()
        classRepository.deleteAll()
        raceRepository.deleteAll()
        itemRepository.deleteAll()
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
                        uname = "test_boii",
                        authorityLevels = listOf(AuthorityLevel.ROLE_ADMIN),
                        passwordHash = passwordUtils.hashPassword("TEST".toCharArray(), salt),
                        passwordSalt = salt
                ),
                User(id= "TESTUSER3",
                        email = "test_dude@test.com",
                        uname = "test_boii2",
                        authorityLevels = listOf(AuthorityLevel.ROLE_USER),
                        passwordHash = passwordUtils.hashPassword("TEST".toCharArray(), salt),
                        passwordSalt = salt
                )
        ))
        context = userRepository.findOne("TESTUSER3")
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
                price = 5f,
                itemClasses = listOf("head"),
                modifiers = mapOf(Pair("ac", 1f)),
                version = "TEST",
                type = ObjType.ITEM_ARMOR
                )))

    }



    fun addTestVersion() {
        versionFactory.createVersion("TEST", context)
        statRepository.save(listOf(
                Stat(
                        id = "hpTEST",
                        name = "hp",
                        fname = "Health Points",
                        description = "health points",
                        version = "TEST",
                        skill = false
                ),
                Stat(
                        id = "willTEST",
                        name = "will",
                        fname = "Willpower",
                        description = "Willpower",
                        version = "TEST",
                        skill = false
                ),
                Stat(
                        id = "fortTEST",
                        name = "fort",
                        fname = "Fortitude",
                        description = "Fortitude",
                        version = "TEST",
                        skill = false
                ),
                Stat(
                        id = "historyTEST",
                        name = "history",
                        fname = "History",
                        description = "History",
                        version = "TEST",
                        skill = true
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

    fun addTestCharacters() {
        characterRepository.save(listOf(
                Character(
                        id = "1.2",
                        name = "Cregan the Destroyer of Worlds",
                        abilityPoints = Ability(12, 14,15,11,12,14),
                        raceid = "0.0",
                        classid = "1.1",
                        version = "TEST",
                        money = 1f,
                        slots = listOf<Slot>(),
                        inventory = listOf<String>(),
                        access = AccessData(owner = "TESTUSER",  controlList = mapOf(
                                Pair(AuthorityLevel.ROLE_ADMIN, AuthorityMode.MODE_EDIT)))
                        ),
                Character(
                        id = "13.0",
                        name = "Del",
                        abilityPoints = Ability(14, 13,15,17,16,11),
                        raceid = "1.0",
                        classid = "0.1",
                        version = "TEST",
                        money = 50f,
                        slots = listOf<Slot>(),
                        inventory = listOf<String>(),
                        access = AccessData(owner = "TESTUSER",  controlList = mapOf(
                                Pair(AuthorityLevel.ROLE_ADMIN, AuthorityMode.MODE_EDIT)))
                )
        ))
    }

    @Test
    fun createNewCharacter() {
        val owner = userRepository.findById("TESTUSER") ?: throw RuntimeException()
        val character = characterFactory.createNewCharacter(
                abilityPoints = AbilityInput(13,12,11,15,14,16),
                name = "Harold",
                classid = "1.1",
                raceid = "0.0",
                version = "TEST",
                owner = owner
        )
        val classRpg = detailFactory.getClassById("1.1", versionFactory.hydrateVersion("TEST"), owner)
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
    fun updateCharacter() {
        val owner = userRepository.findById("TESTUSER") ?: throw RuntimeException()
        val user = userRepository.findById("TESTUSER3") ?: throw RuntimeException()

        val character = characterRepository.findById("1.2")
        try {
            characterFactory.updateCharacter(id = "1.2",
                    name = "blah",
                    abilityPoints = AbilityInput(12, 14, 15, 11, 12, 14),
                    classid = character!!.classid,
                    raceid = character.raceid,
                    context = user)
            fail()
        }catch (e: GraphQLException){
            assertThat(e.message, `is`(equalTo("Forbidden")))
        }

        val editCharacter = characterFactory.updateCharacter(id = "1.2",
                name = character!!.name,
                abilityPoints = AbilityInput(12, 14, 15, 11, 1, 14),
                classid = character.classid,
                raceid = character.raceid,
                context = owner)

        assertThat(editCharacter.abilityPoints.wis, `is`(equalTo(1)))
        assertThat(editCharacter.abilityPoints.str, `is`(equalTo(12)))
        assertThat(editCharacter.abilityPoints.con, `is`(equalTo(14)))
        assertThat(editCharacter.name == character.name, `is`(true))
    }

    @Test
    fun getCharacterById() {
        val context = userRepository.findById("TESTUSER") ?: throw RuntimeException()
        val character = characterFactory.getCharacterById("1.2", context)

        assertThat(character.name,  `is`(equalTo("Cregan the Destroyer of Worlds")))
        assertThat(character.abilityPoints,  `is`(equalTo(Ability(12, 14, 15, 11, 12, 14))))
        assertThat(character.race.name,  `is`(equalTo("Orc")))
        assertThat(character.money, `is`(equalTo(1f)))

    }

    @Test
    fun getCharactersByName() {
        val context = userRepository.findById("TESTUSER") ?: throw RuntimeException()
        val characterList = characterFactory.getCharactersByName("Cregan the Destroyer of Worlds", context)

        assertThat(characterList.count(), `is`(equalTo(1)))

        val character = characterList[0]

        assertThat(character.name,  `is`(equalTo("Cregan the Destroyer of Worlds")))
        assertThat(character.abilityPoints,  `is`(equalTo(Ability(12, 14, 15, 11, 12, 14))))
        assertThat(character.race.name,  `is`(equalTo("Orc")))
        assertThat(character.money, `is`(equalTo(1f)))

    }

    @Test
    fun getCharactersByVersion(){
        val context = userRepository.findById("TESTUSER2") ?: throw RuntimeException()
        val characterList = characterFactory.getCharactersByVersion("TEST", context)

        assertThat(characterList.count(), `is`(equalTo(2)))

        val character = characterList[1]

        assertThat(character.name,  `is`(equalTo("Cregan the Destroyer of Worlds")))
        assertThat(character.abilityPoints,  `is`(equalTo(Ability(12, 14, 15, 11, 12, 14))))
        assertThat(character.race.name,  `is`(equalTo("Orc")))
        assertThat(character.money, `is`(equalTo(1f)))
    }

    @Test
    fun testVersionSlots() {
        val owner = userRepository.findById("TESTUSER") ?: throw RuntimeException()
        val user = userRepository.findById("TESTUSER2") ?: throw RuntimeException()
        val character = characterFactory.createNewCharacter(
                abilityPoints = AbilityInput(12,14,11,15,14,16),
                name = "Jerome Stefan",
                classid = "0.1",
                raceid = "1.0",
                version = "TEST",
                owner = owner
        )
        assertThat(character.slots.count(), `is`(not(0)))
        assertThat(character.slots[0].name, `is`(equalTo("hand_left")))
        assertThat(character.slots[0].empty, `is`(true))

        try {
            characterFactory.addItemToCharacter(character.id, "BucketTEST", false, user)
            fail()
        } catch (e: GraphQLException) {
            assertThat(e.message, `is`(equalTo("Forbidden")))
        }

        val newCharacter = characterFactory.addItemToCharacter(character.id, "BucketTEST", false, owner)
        assertThat(newCharacter.inventory.count(), `is`(not(0)))
        assertThat(newCharacter.money, `is`(equalTo(0f)))

        val equipChar = characterFactory.equipItem(character.id, "BucketTEST", "head", owner)

        assertThat(equipChar.slots[2].empty, `is`(false))
        assertThat(equipChar.slots[2].name, `is`(equalTo("head")))
        assertThat(equipChar.slots[2].item, `is`(equalTo(ItemQL(id = "BucketTEST",
                name = "Bucket of head",
                description = "A bucket you can wear on your head",
                price = 5f,
                itemClasses = listOf("head"),
                modifiers = listOf(Modifier("ac", 1f)),
                version = "TEST",
                type = ObjType.ITEM_ARMOR))))

    }

    @Test
    fun testMoneySystem(){
        val context = userRepository.findById("TESTUSER") ?: throw RuntimeException()
        val character = characterFactory.createNewCharacter(
                abilityPoints = AbilityInput(12,14,11,15,14,16),
                name = "Big Boii",
                classid = "0.1",
                raceid = "1.0",
                version = "TEST",
                owner = context
        )
        assertThat(character.money, `is`(equalTo(0f)))

        assertThat(character.slots.count(), `is`(not(0)))

        val characterMoney = characterFactory.setCharacterMoney(character.id, 5f, context)
        assertThat(characterMoney.money, `is`(equalTo(5f)))

        val purchaseCharacter = characterFactory.purchaseItem(character.id, "BucketTEST", context)
        assertThat(purchaseCharacter.money, `is`(equalTo(0f)))
        assertThat(purchaseCharacter.inventory.count(), `is`(not(0)))

        assertThat(purchaseCharacter.slots.count(), `is`(not(0)))

        assertThat(purchaseCharacter.inventory.contains(ItemQL(id = "BucketTEST",
                name = "Bucket of head",
                description = "A bucket you can wear on your head",
                price = 5f,
                itemClasses = listOf("head"),
                modifiers = listOf(Modifier("ac", 1f)),
                version = "TEST",
                type = ObjType.ITEM_ARMOR)), `is`(equalTo(true)))


    }


}
