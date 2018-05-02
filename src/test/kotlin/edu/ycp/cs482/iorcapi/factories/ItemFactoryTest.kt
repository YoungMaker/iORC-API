package edu.ycp.cs482.iorcapi.factories

import com.mmnaseri.utils.spring.data.dsl.factory.RepositoryFactoryBuilder
import edu.ycp.cs482.iorcapi.model.Item
import edu.ycp.cs482.iorcapi.model.attributes.Modifier
import edu.ycp.cs482.iorcapi.model.attributes.ObjType
import edu.ycp.cs482.iorcapi.model.attributes.Stat
import edu.ycp.cs482.iorcapi.model.attributes.VersionInfo
import edu.ycp.cs482.iorcapi.model.authentication.AuthorityLevel
import edu.ycp.cs482.iorcapi.model.authentication.Authorizer
import edu.ycp.cs482.iorcapi.model.authentication.PasswordUtils
import edu.ycp.cs482.iorcapi.model.authentication.User
import edu.ycp.cs482.iorcapi.repositories.*
import org.junit.Test
import org.hamcrest.CoreMatchers.*
import org.junit.Assert.*
import org.junit.Before

class ItemFactoryTest {

    lateinit var itemRepository: ItemRepository
    lateinit var itemFactory: ItemFactory
    lateinit var userRepository: UserRepository
    //lateinit var userFactory: UserFactory
    lateinit var statRepository: StatRepository
    lateinit var versionInfoRepository: VersionInfoRepository
    lateinit var versionRepository: VersionRepository
    lateinit var versionFactory: VersionFactory
    lateinit var salt: ByteArray
    lateinit var context: User
    lateinit var passwordUtils: PasswordUtils


    @Before
    fun setup(){
        //create item repo and factory
        itemRepository = RepositoryFactoryBuilder.builder().mock(ItemRepository::class.java)
        statRepository = RepositoryFactoryBuilder.builder().mock(StatRepository::class.java)
        versionInfoRepository = RepositoryFactoryBuilder.builder().mock(VersionInfoRepository::class.java)
        userRepository = RepositoryFactoryBuilder.builder().mock(UserRepository::class.java)
        versionRepository = RepositoryFactoryBuilder.builder().mock(VersionRepository::class.java)
        versionFactory = VersionFactory(statRepository, versionInfoRepository, versionRepository, Authorizer())
        itemFactory = ItemFactory(itemRepository, Authorizer() )
        passwordUtils = PasswordUtils()
        salt = passwordUtils.generateSalt(32)
        addTestUsers()
        addTestVersion()
        addTestItems()
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

    }


    private fun addTestItems() {
        itemRepository.save(listOf(
                Item(
                    id = "Battle Axe of the Not so BoldTEST",
                    name = "Battle Axe of the Not so Bold",
                    description = "A battle axe that is wielded people who want a useless item worth way too much money.",
                    price = 999999f,
                    itemClasses = listOf("axe", "military_weapon", "melee_weapon"),
                    version = "TEST",
                    type = ObjType.ITEM_WEAPON
        ), Item(
                id = "Ranged Bow of the Unhinged MarksmanTEST",
                name = "Ranged Bow of the Unhinged Marksman",
                description = "A Bow so wildly inaccurate only insane marksmen would buy",
                price = 350f,
                itemClasses = listOf("bow", "military_weapon", "ranged_weapon", "mil_weapon", ""),
                version = "TEST",
                type = ObjType.ITEM_WEAPON
        )))

    }

    @Test
    fun addItem() {
        //TODO improve and expand
        //create item, add to variable, add to repo
        val item = itemFactory.addItem(
                    name = "Battle Axe of the Bold",
                    description = "A battle axe that is wielded by scholars to ward off lunch money thieves.",
                    price = 520f,
                    itemClasses = listOf("axe", "military_weapon", "melee_weapon"),
                    version = versionFactory.hydrateVersion("TEST"),
                    type = ObjType.ITEM_WEAPON,
                    context = context
                )

        //get items from repo
        val repoItem = itemRepository.findById(item.id)

        //check if item was added to repo
        assertThat(repoItem?.id, `is` (equalTo(item.id)))
        assertThat(repoItem?.name, `is` (equalTo(item.name)))
        assertThat(repoItem?.price, `is` (equalTo(item.price)))
        assertThat(repoItem?.description, `is`(equalTo(item.description)))
    }

    @Test
    fun getVersionItems() {
        //TODO improve and expand
        //itemRepository

        //get by version the list of items
        val itemFactList = itemFactory.getVersionItems(versionFactory.hydrateVersion("TEST"), context)
        //print("FACTORY ITEMS" + itemFactList)
        val repoItems = itemRepository.findByVersion("TEST")
        //print("REPO ITEMS" + repoItems)

        //test the list of items
        assertThat(repoItems[0].id, `is` (equalTo(itemFactList[0].id)))
        assertThat(repoItems[0].name, `is` (equalTo(itemFactList[0].name)))
        assertThat(repoItems[0].price, `is` (equalTo(itemFactList[0].price)))
        assertThat(repoItems[0].description, `is` (equalTo(itemFactList[0].description)))

        assertThat(repoItems[1].id, `is` (equalTo(itemFactList[1].id)))
        assertThat(repoItems[1].name, `is` (equalTo(itemFactList[1].name)))
        assertThat(repoItems[1].price, `is` (equalTo(itemFactList[1].price)))
        assertThat(repoItems[1].description, `is` (equalTo(itemFactList[1].description)))
    }

    @Test
    fun getVersionItemType() {
        //TODO improve and expand

        //get by version and item type the list of items
        val itemFactList = itemFactory.getVersionItemType(ObjType.ITEM_WEAPON, versionFactory.hydrateVersion("TEST"), context)
        //print("FACTORY ITEMS" + itemFactList)
        val repoItems = itemRepository.findByVersion("TEST")
        //print("REPO ITEMS" + repoItems)

        //test the list of items
        assertThat(repoItems[0].id, `is` (equalTo(itemFactList[0].id)))
        assertThat(repoItems[0].name, `is` (equalTo(itemFactList[0].name)))
        assertThat(repoItems[0].description, `is`(equalTo(itemFactList[0].description)))
        assertThat(repoItems[0].price, `is`(equalTo(itemFactList[0].price)))
        assertThat(repoItems[0].type, `is`(equalTo(itemFactList[0].type)))
        assertThat(repoItems[0].itemClasses.contains("axe"), `is`(true))
    }

    @Test
    fun getItemsByClasses() {
        //itemRepository
        val repoItem = itemRepository.findById("Battle Axe of the Not so BoldTEST")
        print("TEST ITEM" + repoItem?.name)
        //get by version and item type the list of items
        val itemFactList = itemFactory.getItemsByClasses(listOf("axe", "military_weapon", "melee_weapon"), versionFactory.hydrateVersion("TEST"), context)
        print("FACTORY ITEMS" + itemFactList)
        val repoItems = itemRepository.findByVersionAndItemClasses("TEST", classes = listOf("axe", "military_weapon", "melee_weapon"))
        print("REPO ITEMS" + repoItems)

        //test the list of items
        assertThat(repoItems[0].id, `is` (equalTo(itemFactList[0].id)))
        assertThat(repoItems[0].name, `is` (equalTo(itemFactList[0].name)))
    }

    @Test
    fun addRemoveItemMutations() {
        var item = itemFactory.addItemModifier("Battle Axe of the Not so BoldTEST", hashMapOf(Pair("dmg", 2.6f)), versionFactory.hydrateVersion("TEST"), context)

        assertThat(item.modifiers.count(), `is`(not(equalTo(0))))
        assertThat(item.modifiers.contains(Modifier("dmg", 2.6f)), `is`(true))

        item = itemFactory.removeItemModifier("Battle Axe of the Not so BoldTEST", "dmg", versionFactory.hydrateVersion("TEST"), context)

        assertThat(item.modifiers.count(), `is`((equalTo(0))))
        assertThat(item.modifiers.contains(Modifier("dmg", 2.6f)), `is`(false))
    }

}