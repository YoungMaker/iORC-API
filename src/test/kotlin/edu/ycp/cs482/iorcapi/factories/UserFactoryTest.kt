package edu.ycp.cs482.iorcapi.factories

import com.google.common.base.Predicates.equalTo
import com.mmnaseri.utils.spring.data.dsl.factory.RepositoryFactoryBuilder
import edu.ycp.cs482.iorcapi.model.authentication.*
import edu.ycp.cs482.iorcapi.repositories.UserRepository
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
        val user =  userFactory.createUserAccount("bro@test.com", "theboi37", "JoyToTheWorld17", AuthorityLevel.ROLE_USER)
        assertThat(user.email, Matchers.`is`(Matchers.equalTo("bro@test.com")))
        assertThat(user.uname, Matchers.`is`(Matchers.equalTo("theboi37")))
        assertThat(user.authorityLevels.contains(AuthorityLevel.ROLE_USER), Matchers.`is`(true))
        assertThat(user.authorityLevels.contains(AuthorityLevel.ROLE_ADMIN), Matchers.`is`(false))
    }

    @Test
    fun createAdminAccount() {
    }

    @Test
    fun loginUser() {
        val token = userFactory.loginUser("test@test.com", "TEST")
        val user = userRepository.findByEmail("test@test.com")
        val loggedInUser = userFactory.hydrateUser(Context(token))
        assertThat(loggedInUser.id, Matchers.`is`(Matchers.equalTo(user!!.id)))
    }
}