package edu.ycp.cs482.iorcapi.factories

import com.google.common.base.Predicates.equalTo
import com.mmnaseri.utils.spring.data.dsl.factory.RepositoryFactoryBuilder
import edu.ycp.cs482.iorcapi.model.authentication.AuthorityLevel
import edu.ycp.cs482.iorcapi.model.authentication.PasswordUtils
import edu.ycp.cs482.iorcapi.model.authentication.User
import edu.ycp.cs482.iorcapi.repositories.UserRepository
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import org.hamcrest.CoreMatchers.*

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
        addTestUsers()
        userFactory = UserFactory(userRepository, passwordUtils)
    }

    @Test
    fun createUserAccount() {
        //val user =  userFactory.createUserAccount("bro@test.com", "theboi37", "JoyToTheWorld17", AuthorityLevel.ROLE_USER)
       // assertThat(user.uname, `is`(equalTo("bro@test.com")))

    }

    @Test
    fun createAdminAccount() {
    }

    @Test
    fun loginUser() {
    }

    @Test
    fun hydrateUser() {
    }
}