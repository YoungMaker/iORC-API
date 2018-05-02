package edu.ycp.cs482.iorcapi.factories

import com.google.common.base.Predicates.equalTo
import com.mmnaseri.utils.spring.data.dsl.factory.RepositoryFactoryBuilder
import edu.ycp.cs482.iorcapi.model.authentication.*
import edu.ycp.cs482.iorcapi.repositories.UserRepository
import graphql.GraphQLException
import io.jsonwebtoken.impl.crypto.MacProvider
import org.hamcrest.Matchers
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.springframework.stereotype.Component
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

//@RunWith(SpringJUnit4ClassRunner::class)
//@ContextConfiguration("classpath:applicationContext.xml")
class UserFactoryTest {

    lateinit var userFactory: UserFactory
    lateinit var userRepository: UserRepository
    lateinit var passwordUtils: PasswordUtils
    lateinit var salt: ByteArray
    lateinit var context: User


    private fun addTestUsers() {
        userRepository.save(listOf(
                User(id = "TESTUSER",
                        email = "test@test.com",
                        uname = "test_daddy",
                        authorityLevels = listOf(AuthorityLevel.ROLE_USER),
                        passwordHash = passwordUtils.hashPassword("TEST".toCharArray(), salt),
                        passwordSalt = salt
                ),
                User(id = "TESTUSER2",
                        email = "test_admin@test.com",
                        uname = "test_boii",
                        authorityLevels = listOf(AuthorityLevel.ROLE_ADMIN),
                        passwordHash = passwordUtils.hashPassword("TEST".toCharArray(), salt),
                        passwordSalt = salt
                ),
                User(id = "TESTUSER3",
                        email = "test_dude@test.com",
                        uname = "test_boii",
                        authorityLevels = listOf(AuthorityLevel.ROLE_USER),
                        passwordHash = passwordUtils.hashPassword("TEST".toCharArray(), salt),
                        passwordSalt = salt
                )
        ))
        //admin context!
        context = userRepository.findById("TESTUSER2") ?: throw RuntimeException()
    }

    @Before
    fun setUp() {
        userRepository = RepositoryFactoryBuilder.builder().mock(UserRepository::class.java)
        passwordUtils = PasswordUtils()
        salt = passwordUtils.generateSalt(32)
        addTestUsers()
        userFactory = UserFactory(userRepository, passwordUtils, JwtUtils())
    }

    @Test
    fun createUserAccount() {
        val user =  userFactory.createUserAccount("bro@test.com", "theboi37", "JoyToTheWo&rld17", AuthorityLevel.ROLE_USER)
        assertThat(user.email, Matchers.`is`(Matchers.equalTo("bro@test.com")))
        assertThat(user.uname, Matchers.`is`(Matchers.equalTo("theboi37")))
        assertThat(user.authorityLevels.contains(AuthorityLevel.ROLE_USER), Matchers.`is`(true))
        assertThat(user.authorityLevels.contains(AuthorityLevel.ROLE_ADMIN), Matchers.`is`(false))
    }

    @Test
    fun createAdminAccount() {
        val user =  userFactory.createAdminAccount("bro@test.com", "theboi37", "JoyToTheWo&rld17", context)
        assertThat(user.email, Matchers.`is`(Matchers.equalTo("bro@test.com")))
        assertThat(user.uname, Matchers.`is`(Matchers.equalTo("theboi37")))
        assertThat(user.authorityLevels.contains(AuthorityLevel.ROLE_USER), Matchers.`is`(false))
        assertThat(user.authorityLevels.contains(AuthorityLevel.ROLE_ADMIN), Matchers.`is`(true))
    }

    @Test
    fun loginUser() {
        val token = userFactory.loginUser("test@test.com", "TEST")
        val user = userRepository.findByEmail("test@test.com")
        val loggedInUser = userFactory.hydrateUser(token)
        assertThat(loggedInUser.id, Matchers.`is`(Matchers.equalTo(user!!.id)))
    }

    @Test
    fun updatePassword(){
        val user =  userFactory.createUserAccount("bro@test.com", "theboi37", "JoyToTheWo&rld17", AuthorityLevel.ROLE_USER)
        assertThat(user.email, Matchers.`is`(Matchers.equalTo("bro@test.com")))
        assertThat(user.uname, Matchers.`is`(Matchers.equalTo("theboi37")))

        try {
             userFactory.updateUserPassword("bro@test.com", "JoyToTheWo&rld17", "JoyToTheWo&rld17")
            fail()
        }catch (e: GraphQLException){
            assertThat(e.message, Matchers.`is`(Matchers.equalTo("Cannot re-use your last password!")))
        }

        try {
            userFactory.updateUserPassword("bro@test.com", "JoyToTheWorld17", "JoyToTheWo&rld17")
            fail()
        }catch (e: GraphQLException){
            assertThat(e.message, Matchers.`is`(Matchers.equalTo("incorrect user/email combo")))
        }
        val user2 = userFactory.updateUserPassword("bro@test.com", "JoyToTheWo&rld17", "JoyToTheWorld17&")
        assertThat(user2.email, Matchers.`is`(Matchers.equalTo(user.email)))
        assertThat(user2.uname, Matchers.`is`(Matchers.equalTo(user.uname)))
        assertThat(user2.authorityLevels.contains(AuthorityLevel.ROLE_USER), Matchers.`is`(true))
        assertThat(user2.authorityLevels.contains(AuthorityLevel.ROLE_ADMIN), Matchers.`is`(false))
    }

    @Test
    fun banAccount(){
        val user = userRepository.findById("TESTUSER") ?: throw RuntimeException()
        val nonAdmin = userRepository.findById("TESTUSER3") ?: throw RuntimeException()
        assertThat(user.authorityLevels.contains(AuthorityLevel.ROLE_USER), Matchers.`is`(true))

        try{
            userFactory.banUserAccount(user.id, nonAdmin)
            fail()
        } catch (e: GraphQLException){
            assertThat(e.message, Matchers.`is`(Matchers.equalTo("Forbidden")))
        }

        val ban = userFactory.banUserAccount(user.id, context)
        assertThat(ban, Matchers.`is`(Matchers.equalTo("User TESTUSER was banned")))
        val user2 = userRepository.findById("TESTUSER") ?: throw RuntimeException()
        assertThat(user2.authorityLevels.contains(AuthorityLevel.ROLE_USER), Matchers.`is`(false))


        val unban = userFactory.unbanUserAccount(user.id, context)
        assertThat(unban.authorityLevels.contains(AuthorityLevel.ROLE_USER), Matchers.`is`(true))
    }

    @Test
    fun elevateAccount(){
        val user = userRepository.findById("TESTUSER") ?: throw RuntimeException()
        val nonAdmin = userRepository.findById("TESTUSER3") ?: throw RuntimeException()

        try {
            userFactory.elevateUserAccount(user.id, nonAdmin)
            fail()
        } catch (e: GraphQLException){
            assertThat(e.message, Matchers.`is`(Matchers.equalTo("Forbidden")))
        }

        val user2 = userFactory.elevateUserAccount(user.id, context)
        assertThat(user2.authorityLevels.contains(AuthorityLevel.ROLE_ADMIN), Matchers.`is`(true))
    }
}