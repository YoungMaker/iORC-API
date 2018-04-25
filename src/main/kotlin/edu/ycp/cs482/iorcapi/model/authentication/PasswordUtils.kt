package edu.ycp.cs482.iorcapi.model.authentication

import org.springframework.stereotype.Component
import java.security.spec.InvalidKeySpecException
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.util.*
import java.util.regex.Pattern
import javax.crypto.spec.PBEKeySpec
import javax.crypto.SecretKeyFactory
import org.bouncycastle.cms.RecipientId.password




private val ITERATIONS = 500
private val KEY_LENGTH = 256

@Component
class PasswordUtils {


    fun hashPassword(password: CharArray, salt: ByteArray): ByteArray {
        val spec = PBEKeySpec(password, salt, ITERATIONS, KEY_LENGTH)
        Arrays.fill(password, Character.MIN_VALUE)
        try {
            val skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512")

            val key = skf.generateSecret(spec)
            return key.encoded

        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException(e) //this should never happen. Should be a fatal exception
        } catch (e: InvalidKeySpecException) {
            throw RuntimeException(e)
        } finally {
            spec.clearPassword()
        }
    }

    fun isExpectedPassword(password: CharArray, salt: ByteArray, expectedHash: ByteArray): Boolean {
        val pwdHash = hashPassword(password, salt)
        Arrays.fill(password, Character.MIN_VALUE)
        if (pwdHash.size != expectedHash.size) return false
        return pwdHash.indices.none { pwdHash[it] != expectedHash[it] }
    }

    fun generateSalt(len: Int): ByteArray {
        val rand = SecureRandom.getInstance("SHA1PRNG", "SUN")
        val arr = ByteArray(len)
        rand.nextBytes(arr)
        return arr
    }

    fun fitsPasswordRules(password: String, uname: String): STR_RULES {

        if ( password.length < 8) {
           return STR_RULES.TOO_SHORT
        }
        if (password.contains(uname)) {
           return STR_RULES.CONTAINS_UNAME
        }
        val upperCaseChars = "(.*[A-Z].*)"
        if (!password.matches(Regex(upperCaseChars))) {
            return STR_RULES.NO_UPPERCASE
        }
        val lowerCaseChars = "(.*[a-z].*)"
        if (!password.matches(Regex(lowerCaseChars))) {
            return STR_RULES.NO_LOWERCASE
        }
        val numbers = "(.*[0-9].*)"
        if (!password.matches(Regex(numbers))) {
            return STR_RULES.NO_DIGITS
        }
        if(password.contains(" ")){
            return STR_RULES.ILLEGAL_CHAR
        } //FIXME: will not detect ` character as special. Dunno why
        val specialChars = "(.*[,~,!,@,#,$,%,^,&,*,(,),-,_,=,+,[,{,],},|,;,:,<,>,/,?].*$)"
        if (!password.matches(Regex(specialChars))) {
            return STR_RULES.NO_SPECIAL_CHARS
        }
        return STR_RULES.OK
    }

    fun fitsUnameRules(uname: String) : STR_RULES {
        if (uname.length < 4) {
            return STR_RULES.TOO_SHORT
        }
        if(uname.length > 20) {
            return STR_RULES.TOO_LONG
        }
        if(uname.contains(" ")) {
            return STR_RULES.ILLEGAL_CHAR
        }
        val specialChars = "(.*[,~,!,@,#,$,%,^,&,*,(,),=,+,[,{,],},|,;,:,<,>,/,?].*$)"
        if (uname.matches(Regex(specialChars))) {
            return STR_RULES.ILLEGAL_CHAR
        }
        return STR_RULES.OK
    }

}

enum class STR_RULES {
    OK,
    TOO_SHORT,
    NO_SPECIAL_CHARS,
    ILLEGAL_CHAR,
    NO_DIGITS,
    NO_UPPERCASE,
    NO_LOWERCASE,
    TOO_LONG,
    CONTAINS_UNAME
}