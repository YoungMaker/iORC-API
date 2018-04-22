package edu.ycp.cs482.iorcapi.factories

import edu.ycp.cs482.iorcapi.model.authentication.*
import edu.ycp.cs482.iorcapi.repositories.UserRepository
import graphql.GraphQLException
import java.util.*
import org.apache.commons.validator.routines.EmailValidator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class UserFactory(
        private val userRepository: UserRepository,
        private val passwordUtils: PasswordUtils
) {

    @Autowired
    lateinit var jwtUtils: JwtUtils

    //TODO: be sure that unames fit format and are unique
    fun createUserAccount(email: String, uname: String, password: String) : UserQL{
        if(userRepository.findByEmail(email) == null && isValidEmail(email)) {
            val salt = passwordUtils.generateSalt(32)
             val user = User( id = UUID.randomUUID().toString(),
                    email = email,
                    authorityLevels = listOf(AuthorityLevel.ROLE_USER),
                    passwordSalt = salt,
                    uname = uname,
                    passwordHash = passwordUtils.hashPassword(password.toCharArray(), salt)
            )
            userRepository.save(user)
            return UserQL(user) //hydrates to QL compliant authentication
        } else {
            throw GraphQLException("email incorrect or already in use")
        }
    }

    fun loginUser(email:String, password: String): String {
        val  user = userRepository.findByEmail(email) ?: throw GraphQLException("No user with that email")

        if(passwordUtils.isExpectedPassword(password.toCharArray(), user.passwordSalt, user.passwordHash)){
            return jwtUtils.createJwt(user.id)
        } else {
            throw GraphQLException("incorrect user/email combo")
        }
    }

    private fun isValidEmail(email: String ): Boolean {
        val validator = EmailValidator.getInstance()
        return validator.isValid(email)
    }

    fun hydrateUser(context: Context) : User {
        return userRepository.findById(jwtUtils.parseJWT(context.token)) ?: throw GraphQLException("no user with that id")
    }

}