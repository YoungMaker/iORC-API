package edu.ycp.cs482.iorcapi.factories

import com.mmnaseri.utils.spring.data.dsl.factory.RepositoryFactoryBuilder
import edu.ycp.cs482.iorcapi.model.attributes.ObjType
import edu.ycp.cs482.iorcapi.repositories.ItemRepository
import org.junit.Test
import org.hamcrest.CoreMatchers.*
import org.junit.Assert.*
import org.junit.Before

//TODO: add tests pls @hwilling
class ItemFactoryTest {

    lateinit var itemRepository: ItemRepository
    lateinit var itemFactory: ItemFactory


    @Before
    fun setup(){
        //create item repo and factory
        itemRepository = RepositoryFactoryBuilder.builder().mock(ItemRepository::class.java)
        itemFactory = ItemFactory(itemRepository)
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

        //sanity checks
        //print("FACTORY ITEMS" + item.id)
        //print("REPO ITEMS" + repoItem?.id)

        //check if item was added to repo
        assertThat(repoItem?.id, `is` (equalTo(item.id)))
        assertThat(repoItem?.name, `is` (equalTo(item.name)))
    }

    @Test
    fun getVersionItems() {
        //TODO improve and expand
        //itemRepository
        itemFactory.addItem(
                name = "Battle Axe of the Not so Bold",
                description = "A battle axe that is wielded people who want a useless item worth way too much money.",
                price = 999999f,
                itemClasses = listOf("axe", "military_weapon", "melee_weapon"),
                version = "TEST_VERSION",
                type = ObjType.ITEM_WEAPON
        )

        //get by version the list of items
        val itemFactList = itemFactory.getVersionItems("TEST_VERSION")
        //print("FACTORY ITEMS" + itemFactList)
        val repoItems = itemRepository.findByVersion("TEST_VERSION")
        //print("REPO ITEMS" + repoItems)

        //test the list of items
        assertThat(repoItems.get(0).id, `is` (equalTo(itemFactList.get(0).id)))
        assertThat(repoItems.get(0).name, `is` (equalTo(itemFactList.get(0).name)))
    }

    @Test
    fun getVersionItemType() {
        //TODO improve and expand
        //itemRepository
        itemFactory.addItem(
                name = "Battle Axe of the Not so Bold",
                description = "A battle axe that is wielded people who want a useless item worth way too much money.",
                price = 999999f,
                itemClasses = listOf("axe", "military_weapon", "melee_weapon"),
                version = "TEST_VERSION",
                type = ObjType.ITEM_WEAPON
        )

        //get by version and item type the list of items
        val itemFactList = itemFactory.getVersionItemType("TEST_VERSION", type = ObjType.ITEM_WEAPON)
        //print("FACTORY ITEMS" + itemFactList)
        val repoItems = itemRepository.findByVersion("TEST_VERSION")
        //print("REPO ITEMS" + repoItems)

        //test the list of items
        assertThat(repoItems.get(0).id, `is` (equalTo(itemFactList.get(0).id)))
        assertThat(repoItems.get(0).name, `is` (equalTo(itemFactList.get(0).name)))

    }

    @Test
    fun getItemsByClasses() {
        //TODO improve and expand, add more items with different classes and add them to list
        //itemRepository
        /*val test = itemFactory.addItem(
                name = "Battle Axe of the Not so Bold",
                description = "A battle axe that is wielded people who want a useless item worth way too much money.",
                price = 999999f,
                itemClasses = listOf("axe", "military_weapon", "melee_weapon"),
                version = "TEST_VERSION",
                type = ObjType.ITEM_WEAPON
        )
        val repoItem = itemRepository.findById(test.id)
        print("TEST ITEM" + repoItem?.name)
        //get by version and item type the list of items
        val itemFactList = itemFactory.getItemsByClasses("TEST_VERSION", classes = listOf("military_weapon"))
        print("FACTORY ITEMS" + itemFactList)
        val repoItems = itemRepository.findByVersionAndItemClasses("TEST_VERSION", classes = listOf("military_weapon"))
        print("REPO ITEMS" + repoItems)

        //test the list of items
        assertThat(repoItems.get(0).id, `is` (equalTo(itemFactList.get(0).id)))
        assertThat(repoItems.get(0).name, `is` (equalTo(itemFactList.get(0).name)))*/
    }

    @Test
    fun addItemMutations() {
        //TODO implement
    }

    @Test
    fun removeItemMutation() {
        //TODO implement
    }
}