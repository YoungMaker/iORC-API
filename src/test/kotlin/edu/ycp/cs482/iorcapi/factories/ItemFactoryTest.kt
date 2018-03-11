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
                    itemClasses = listOf("Axe", "Military_Melee"),
                    version = "TEST_VERSION",
                    type = ObjType.ITEM_WEAPON
                )

        //get items from repo
        val repoItems = itemRepository.findByVersion("TEST_VERSION")

        //check if item was added to repo
        assertThat(repoItems.get(0).name, `is` (equalTo(item.name)))
    }

    @Test
    fun getVersionItems() {
        //TODO implement
    }

    @Test
    fun getVersionItemType() {
        //TODO implement
    }

    @Test
    fun getItemsByClasses() {
        //TODO implement
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