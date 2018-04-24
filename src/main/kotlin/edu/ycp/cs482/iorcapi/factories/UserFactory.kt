package edu.ycp.cs482.iorcapi.factories

import edu.ycp.cs482.iorcapi.model.authentication.*
import edu.ycp.cs482.iorcapi.repositories.UserRepository
import graphql.GraphQLException
import java.util.*
import org.apache.commons.validator.routines.EmailValidator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.regex.Pattern

@Component
class UserFactory(
        private val userRepository: UserRepository,
        private val passwordUtils: PasswordUtils
) {

    private final val UNAME_PATTERN: String = "^[a-z0-9_-]{3,15}$"
    private lateinit var pattern: Pattern

    @Autowired
    lateinit var jwtUtils: JwtUtils

    //TODO: be sure that passwords are min length & have some complexity
    fun createUserAccount(email: String, uname: String, password: String, level: AuthorityLevel = AuthorityLevel.ROLE_USER) : UserQL{
        if(userRepository.findByEmail(email) == null && isValidEmail(email)) {
            if(userRepository.findByUname(uname) == null) {
                val salt = passwordUtils.generateSalt(32)
                val user = User(id = UUID.randomUUID().toString(),
                        email = email,
                        authorityLevels = listOf(level),
                        passwordSalt = salt,
                        uname = uname,
                        passwordHash = passwordUtils.hashPassword(password.toCharArray(), salt)
                )
                userRepository.save(user)
                return UserQL(user) //hydrates to QL compliant authentication
            }  else {
                throw GraphQLException("Username taken or incorrect")
            }
        } else {
            throw GraphQLException("email incorrect or already in use")
        }
    }

    fun createAdminAccount(email: String, uname: String, password: String, context: User) : UserQL{
        if(context.authorityLevels.contains(AuthorityLevel.ROLE_ADMIN)) { //only admins can create admins
            return createUserAccount(email, uname, password, AuthorityLevel.ROLE_ADMIN)
        } else {
            throw GraphQLException("Forbidden")
        }
    }

    fun loginUser(email:String, password: String): String {
        val  user = userRepository.findByEmail(email) ?: throw GraphQLException("incorrect user/email combo")

        if(passwordUtils.isExpectedPassword(password.toCharArray(), user.passwordSalt, user.passwordHash)){
            return jwtUtils.createJwt(user.id)
        } else {
            throw GraphQLException("incorrect user/email combo")
        }
    }

    fun getUserInfo(email: String, context: User): UserQL{
        val user = userRepository.findByEmail(email) ?: throw GraphQLException("")
        if(user.id == context.id){
            return UserQL(user)
        } else {throw GraphQLException("Forbidden") }
    }

    private fun isValidEmail(email: String ): Boolean {
        val validator = EmailValidator.getInstance()
        return validator.isValid(email)
    }

    private fun isValidUsername(uname: String): Boolean{
        return pattern.matcher(uname).matches()
    }

    fun hydrateUser(context: Context) : User {//translates signed JWT tokens into fully hydrated user objects
        return userRepository.findById(jwtUtils.parseJWT(context.token)) ?: throw GraphQLException("Invalid Token!")
    }

    init {
        pattern = Pattern.compile(UNAME_PATTERN)
    }

}