package edu.ycp.cs482.iorcapi.model.authentication

import org.springframework.stereotype.Component
import java.security.spec.InvalidKeySpecException
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.util.*
import javax.crypto.spec.PBEKeySpec
import javax.crypto.SecretKeyFactory


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
            throw RuntimeException(e) //TODO: Convert to correct exception? or handle downwind?
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

}