package edu.ycp.cs482.iorcapi.factories

//import com.sun.corba.se.impl.orbutil.graph.Graph
import edu.ycp.cs482.iorcapi.model.authentication.*
import edu.ycp.cs482.iorcapi.repositories.UserRepository
import graphql.GraphQLException
import java.util.*
import org.apache.commons.validator.routines.EmailValidator
//import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
//import java.security.Key
//import java.util.regex.Pattern

@Component
class UserFactory(
        private val userRepository: UserRepository,
        private val passwordUtils: PasswordUtils,
        private var jwtUtils: JwtUtils
) {

    @Value("\${privatekey}") //pulled from secret config file.
    private val privatekey: String = "THISISADEFAULTKEY_USEDFORTESTINGONLY1122334dfadfaefadfeadfefadf" //default key used in test environment
    val MAX_TOKENS: Int = 5 //maximum active token count

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
                        passwordHash = passwordUtils.hashPassword(password.toCharArray(), salt),
                        tokenList = listOf()
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

    fun updateUserPassword(email: String, password: String, newPassword: String): UserQL {
        val  user = userRepository.findByEmail(email) ?: throw GraphQLException("incorrect user/email combo")
        if(!passwordUtils.isExpectedPassword(password.toCharArray(), user.passwordSalt, user.passwordHash)){
            throw GraphQLException("incorrect user/email combo")
        } //basically tokenless login

        //check if the new password is the old!
        if(passwordUtils.isExpectedPassword(newPassword.toCharArray(), user.passwordSalt, user.passwordHash)){
            throw GraphQLException("Cannot re-use your last password!")
        }
        validateInfo(user.email, user.uname, newPassword) //validate the new password against our rules
        val salt = passwordUtils.generateSalt(32) //create secure random salt to append to hash
        val newUser = User(id = user.id,
                email = user.email,
                authorityLevels = user.authorityLevels,
                passwordSalt = salt,
                uname = user.uname,
                passwordHash = passwordUtils.hashPassword(newPassword.toCharArray(), salt),
                tokenList = user.tokenList
        )
        userRepository.save(newUser)
        logout(email, newPassword) //should log you out of all tokens!
        return UserQL(newUser) //hydrates to QL compliant authentication
    }

    fun createAdminAccount(email: String, uname: String, password: String, context: User) : UserQL{
        if(context.authorityLevels.contains(AuthorityLevel.ROLE_ADMIN)) { //only admins can create admins
            return createUserAccount(email, uname, password, AuthorityLevel.ROLE_ADMIN)
        } else {
            throw GraphQLException("Forbidden")
        }
    }

    //Stores token if its not above the max
    fun loginUser(email:String, password: String): Context {
        val  user = userRepository.findByEmail(email) ?: throw RuntimeException("incorrect user/email combo")

        if(passwordUtils.isExpectedPassword(password.toCharArray(), user.passwordSalt, user.passwordHash)){
            if(!user.authorityLevels.contains(AuthorityLevel.ROLE_ADMIN) && !user.authorityLevels.contains(AuthorityLevel.ROLE_USER)) {
                throw GraphQLException("User account is banned.")
            }
            if(user.tokenList.size >= MAX_TOKENS) {
                throw GraphQLException("Max logins reached")
            }
            val token = Context(jwtUtils.createJwt(user.id, privatekey.toByteArray())) //create token
            val newTokenList = mutableListOf<Context>()
            newTokenList.addAll(user.tokenList) //add old tokens
            newTokenList.add(token)
            val newUser = User(id = user.id,
                    email = user.email,
                    authorityLevels = user.authorityLevels,
                    passwordSalt = user.passwordSalt,
                    uname = user.uname,
                    passwordHash = user.passwordHash,
                    tokenList = newTokenList
            )
            userRepository.save(newUser) //write over the old user with new token values
            return token
        } else {
            throw GraphQLException("incorrect user/email combo")
        }
    }

    fun deregisterToken(context: Context): String {
        val tokenParse = jwtUtils.parseJWT(context.token, privatekey.toByteArray()) //will contain "-expired" if expired

        val user = userRepository.findById(tokenParse.removeSuffix("-expired")) ?: throw GraphQLException("Invalid Token!") //will remove "-expired" if it exists
        if(!user.tokenList.contains(context)) { throw GraphQLException("Invalid Token!") }
        val newTokenList = mutableListOf<Context>()
        newTokenList.addAll(user.tokenList) //add old tokens
        newTokenList.remove(context)
        val newUser = User(id = user.id,
                email = user.email,
                authorityLevels = user.authorityLevels,
                passwordSalt = user.passwordSalt,
                uname = user.uname,
                passwordHash = user.passwordHash,
                tokenList = newTokenList
        )
        userRepository.save(newUser) //write over the old user with new token values
        return "Token de-registered on user " + user.id
    }

    fun logout(email: String, password: String): UserQL {
        val  user = userRepository.findByEmail(email) ?: throw GraphQLException("incorrect user/email combo")
        if(passwordUtils.isExpectedPassword(password.toCharArray(), user.passwordSalt, user.passwordHash)) {
            val newUser = User(id = user.id,
                    email = user.email,
                    authorityLevels = user.authorityLevels,
                    passwordSalt = user.passwordSalt,
                    uname = user.uname,
                    passwordHash = user.passwordHash,
                    tokenList = listOf() //clears old token list, all login tokens are deregistered
            )
            userRepository.save(newUser)
            return UserQL(newUser)
        } else {
            throw GraphQLException("incorrect user/email combo")
        }
    }

    fun banUserAccount(id: String, context: User): String{
        if(context.authorityLevels.contains(AuthorityLevel.ROLE_ADMIN)){
            val user = userRepository.findById(id) ?: throw GraphQLException("User does not exist")
            val newUser = User(id = user.id,
                    email = user.email,
                    authorityLevels = listOf(),
                    passwordSalt = user.passwordSalt,
                    uname = user.uname,
                    passwordHash = user.passwordHash,
                    tokenList = user.tokenList
            )
            userRepository.save(newUser)
            return "User $id was banned"
        } else {
            throw GraphQLException("Forbidden")
        }
    }

    fun unbanUserAccount(id: String, context: User): UserQL{
        if(context.authorityLevels.contains(AuthorityLevel.ROLE_ADMIN)){
            val user = userRepository.findById(id) ?: throw GraphQLException("User does not exist")
            val newUser = User(id = user.id,
                    email = user.email,
                    authorityLevels = listOf(AuthorityLevel.ROLE_USER),
                    passwordSalt = user.passwordSalt,
                    uname = user.uname,
                    passwordHash = user.passwordHash,
                    tokenList = user.tokenList
            )
            userRepository.save(newUser)
            return UserQL(newUser)
        } else {
            throw GraphQLException("Forbidden")
        }
    }

    fun elevateUserAccount(id: String, context: User): UserQL {
        if(context.authorityLevels.contains(AuthorityLevel.ROLE_ADMIN)){
            val user = userRepository.findById(id) ?: throw GraphQLException("User does not exist")
            val newUser = User(id = user.id,
                    email = user.email,
                    authorityLevels = listOf(AuthorityLevel.ROLE_USER, AuthorityLevel.ROLE_ADMIN),
                    passwordSalt = user.passwordSalt,
                    uname = user.uname,
                    passwordHash = user.passwordHash,
                    tokenList = user.tokenList
            )
            userRepository.save(newUser)
            return UserQL(newUser)
        } else {
            throw GraphQLException("Forbidden")
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

    //TODO: remove token from store if expired
    fun hydrateUser(context: Context) : User {//translates signed JWT tokens into fully hydrated user objects
        val tokenParse = jwtUtils.parseJWT(context.token, privatekey.toByteArray())
        if(tokenParse.contains("-expired")){
            deregisterToken(context)
            throw GraphQLException("Invalid Token!")
        }
        val user =  userRepository.findById(tokenParse) ?: throw GraphQLException("Invalid token!")
        if(!user.tokenList.contains(context)) { throw GraphQLException("Invalid Token!")} //if token isn't in the list
        if(!user.authorityLevels.contains(AuthorityLevel.ROLE_ADMIN) && !user.authorityLevels.contains(AuthorityLevel.ROLE_USER)) {
            throw GraphQLException("User account is banned.")
        }
        return user
    } //throw invalid token if user doesn't exist, we don't want attackers to be able to find if a user exists this way.

    //this only removes the user account. the mutation destroys their characters but leaves any other data intact
    fun deleteUser(email: String, context: User) {
        val user = userRepository.findByEmail(email) ?: throw GraphQLException("incorrect user/email combo")
        if(context.id == user.id){ //users are the only ones that can delete their account? Should be admins?
            userRepository.delete(user.id)
        }
    }


}