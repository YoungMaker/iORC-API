package edu.ycp.cs482.iorcapi.factories

import com.mmnaseri.utils.spring.data.dsl.factory.RepositoryFactoryBuilder
import edu.ycp.cs482.iorcapi.model.Item
import edu.ycp.cs482.iorcapi.model.attributes.Modifier
import edu.ycp.cs482.iorcapi.model.attributes.ObjType
import edu.ycp.cs482.iorcapi.repositories.ItemRepository
import org.junit.Test
import org.hamcrest.CoreMatchers.*
import org.junit.Assert.*
import org.junit.Before

class ItemFactoryTest {

    lateinit var itemRepository: ItemRepository
    lateinit var itemFactory: ItemFactory


    @Before
    fun setup(){
        //create item repo and factory
        itemRepository = RepositoryFactoryBuilder.builder().mock(ItemRepository::class.java)
        itemFactory = ItemFactory(itemRepository)
        addTestItems()
    }

    private fun addTestItems() {
        itemRepository.save(listOf(
                Item(
                    id = "Battle Axe of the Not so BoldTEST_VERSION",
                    name = "Battle Axe of the Not so Bold",
                    description = "A battle axe that is wielded people who want a useless item worth way too much money.",
                    price = 999999f,
                    itemClasses = listOf("axe", "military_weapon", "melee_weapon"),
                    version = "TEST_VERSION",
                    type = ObjType.ITEM_WEAPON
        ), Item(
                id = "Ranged Bow of the Unhinged MarksmanTEST_VERSION",
                name = "Ranged Bow of the Unhinged Marksman",
                description = "A Bow so wildly inaccurate only insane marksmen would buy",
                price = 350f,
                itemClasses = listOf("bow", "military_weapon", "ranged_weapon", "mil_weapon", ""),
                version = "TEST_VERSION",
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
                    version = "TEST_VERSION",
                    type = ObjType.ITEM_WEAPON
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
        val itemFactList = itemFactory.getVersionItems("TEST_VERSION")
        //print("FACTORY ITEMS" + itemFactList)
        val repoItems = itemRepository.findByVersion("TEST_VERSION")
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
        val itemFactList = itemFactory.getVersionItemType("TEST_VERSION", type = ObjType.ITEM_WEAPON)
        //print("FACTORY ITEMS" + itemFactList)
        val repoItems = itemRepository.findByVersion("TEST_VERSION")
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
        val repoItem = itemRepository.findById("Battle Axe of the Not so BoldTEST_VERSION")
        print("TEST ITEM" + repoItem?.name)
        //get by version and item type the list of items
        val itemFactList = itemFactory.getItemsByClasses("TEST_VERSION", classes = listOf("axe", "military_weapon", "melee_weapon"))
        print("FACTORY ITEMS" + itemFactList)
        val repoItems = itemRepository.findByVersionAndItemClasses("TEST_VERSION", classes = listOf("axe", "military_weapon", "melee_weapon"))
        print("REPO ITEMS" + repoItems)

        //test the list of items
        assertThat(repoItems[0].id, `is` (equalTo(itemFactList[0].id)))
        assertThat(repoItems[0].name, `is` (equalTo(itemFactList[0].name)))
    }

    @Test
    fun addRemoveItemMutations() {
        var item = itemFactory.addItemModifier("Battle Axe of the Not so BoldTEST_VERSION", hashMapOf(Pair("dmg", 2.6f)))

        assertThat(item.modifiers.count(), `is`(not(equalTo(0))))
        assertThat(item.modifiers.contains(Modifier("dmg", 2.6f)), `is`(true))

        item = itemFactory.removeItemModifier("Battle Axe of the Not so BoldTEST_VERSION", "dmg")

        assertThat(item.modifiers.count(), `is`((equalTo(0))))
        assertThat(item.modifiers.contains(Modifier("dmg", 2.6f)), `is`(false))
    }

}