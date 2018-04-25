package edu.ycp.cs482.iorcapi.factories

import com.sun.corba.se.impl.orbutil.graph.Graph
import edu.ycp.cs482.iorcapi.model.authentication.*
import edu.ycp.cs482.iorcapi.repositories.UserRepository
import graphql.GraphQLException
import java.util.*
import org.apache.commons.validator.routines.EmailValidator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.security.Key
import java.util.regex.Pattern

@Component
class UserFactory(
        private val userRepository: UserRepository,
        private val passwordUtils: PasswordUtils,
        private var jwtUtils: JwtUtils
) {

    @Value("\${privatekey}") //pulled from secret config file.
    private val privatekey: String = "THISISADEFAULTKEY_USEDFORTESTINGONLY1122334dfadfaefadfeadfefadf" //default key used in test environment

    fun createUserAccount(email: String, uname: String, password: String, level: AuthorityLevel = AuthorityLevel.ROLE_USER) : UserQL{
        validateInfo(email, uname, password)
        if(userRepository.findByEmail(email) == null) { //does email exist?
            if(userRepository.findByUname(uname) == null) { //does username already exist?
                val salt = passwordUtils.generateSalt(32) //create secure random salt to append to hash
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
                throw GraphQLException("Username taken!")
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

    fun loginUser(email:String, password: String): Context {
        val  user = userRepository.findByEmail(email) ?: throw GraphQLException("incorrect user/email combo")

        if(passwordUtils.isExpectedPassword(password.toCharArray(), user.passwordSalt, user.passwordHash)){
            return Context(jwtUtils.createJwt(user.id, privatekey.toByteArray()))
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

    fun validateInfo(email: String, uname: String, password: String): Boolean {
        if(!isValidEmail(email)) throw GraphQLException("email incorrect or already in use")
        val unameStatus = passwordUtils.fitsUnameRules(uname)
        when(unameStatus) {
            STR_RULES.TOO_LONG -> throw GraphQLException("Username too long!")
            STR_RULES.TOO_SHORT -> throw GraphQLException("Username too short!")
            STR_RULES.ILLEGAL_CHAR -> throw GraphQLException("Illegal Character in Username!")
        } //uname is OK!
        val pwrdStatus = passwordUtils.fitsPasswordRules(password, uname)
        if(pwrdStatus != STR_RULES.OK) {
            throw GraphQLException("Password must be at least 8 chars, contain a special character, " +
                    "have upper and lower case characters and contain a number")
        }
        return true
    }

    private fun isValidEmail(email: String ): Boolean {
        val validator = EmailValidator.getInstance()
        return validator.isValid(email)
    }


    fun hydrateUser(context: Context) : User {//translates signed JWT tokens into fully hydrated user objects
        return userRepository.findById(jwtUtils.parseJWT(context.token, privatekey.toByteArray())) ?: throw GraphQLException("Invalid Token!")
    } //throw invalid token if user doesn't exist, we don't want attackers to be able to find if a user exists this way.

    //this only removes the user account. the mutation destroys their characters but leaves any other data intact
    fun deleteUser(email: String, context: User) {
        val user = userRepository.findByEmail(email) ?: throw GraphQLException("incorrect user/email combo")
        if(context.id == user.id){ //users are the only ones that can delete their account? Should be admins?
            userRepository.delete(user.id)
        }
    }


}