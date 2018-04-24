package edu.ycp.cs482.iorcapi.model.authentication

import org.hamcrest.CoreMatchers.*
import org.junit.Test

import org.junit.Assert.*

class PasswordUtilsTest {
    var passwordUtils = PasswordUtils()

    @Test
    fun hashPassword() {
        val salt = passwordUtils.generateSalt(32)
        val salt2 = passwordUtils.generateSalt(32)
        val password = "password123"
        val passwordHash = passwordUtils.hashPassword(password.toCharArray(), salt)
        val passwordHash2 = passwordUtils.hashPassword(password.toCharArray(), salt)
        val passwordHash3 = passwordUtils.hashPassword(password.toCharArray(), salt2)
        assertThat(passwordHash.contentEquals(passwordHash2), `is`(true))
        assertThat(passwordHash.contentEquals(passwordHash3), `is`(false))
    }

    @Test
    fun isExpectedPassword() {
        val salt = passwordUtils.generateSalt(32)
        val password = "password123"
        val passwordHash = passwordUtils.hashPassword(password.toCharArray(), salt)
        val correctPwrd = passwordUtils.isExpectedPassword(password.toCharArray(), salt, passwordHash)
        val incorrectPwrd = passwordUtils.isExpectedPassword("Password123".toCharArray(), salt, passwordHash)
        assertThat(correctPwrd, `is`(true))
        assertThat(incorrectPwrd, `is`(false))
    }

    @Test
    fun generateSalt() {
        var salt = passwordUtils.generateSalt(32)
        assertThat(salt.size, `is`(equalTo(32)))
        salt = passwordUtils.generateSalt(64)
        assertThat(salt.size, `is`(equalTo(64)))

        val salt1 = passwordUtils.generateSalt(32)
        val salt2 = passwordUtils.generateSalt(32)
        assertThat(salt1.contentEquals(salt2), `is`(false))
    }

    @Test
    fun fitsPasswordRules() {
        assertThat(passwordUtils.fitsPasswordRules("SuperBoi111_my_dude", "SuperBoi111"), `is`(equalTo(STR_RULES.CONTAINS_UNAME)))
        assertThat(passwordUtils.fitsPasswordRules("Pass ord123", "SuperBoi111"), `is`(equalTo(STR_RULES.ILLEGAL_CHAR)))
        assertThat(passwordUtils.fitsPasswordRules("password123", "SuperBoi111"), `is`(equalTo(STR_RULES.NO_UPPERCASE)))
        assertThat(passwordUtils.fitsPasswordRules("ANGRY123#", "SuperBoi111"), `is`(equalTo(STR_RULES.NO_LOWERCASE)))
        assertThat(passwordUtils.fitsPasswordRules("Password123", "SuperBoi111"), `is`(equalTo(STR_RULES.NO_SPECIAL_CHARS)))
        assertThat(passwordUtils.fitsPasswordRules("Password", "SuperBoi111"), `is`(equalTo(STR_RULES.NO_DIGITS)))

        assertThat(passwordUtils.fitsPasswordRules("Password123_1234!@My_dude", "SuperBoi111"), `is`(equalTo(STR_RULES.OK)))
        assertThat(passwordUtils.fitsPasswordRules("Core@2Quad", "Elon_Musk"), `is`(equalTo(STR_RULES.OK)))
    }

    @Test
    fun fitsUnameRules() {
        assertThat(passwordUtils.fitsUnameRules("Young@Maker"), `is`(equalTo(STR_RULES.ILLEGAL_CHAR)))
        assertThat(passwordUtils.fitsUnameRules("a"), `is`(equalTo(STR_RULES.TOO_SHORT)))
        assertThat(passwordUtils.fitsUnameRules("Young@Maker_thisisthestoryofagirl_wowantedthewholeworld"),
                `is`(equalTo(STR_RULES.TOO_LONG)))
        assertThat(passwordUtils.fitsUnameRules("Young_Maker"), `is`(equalTo(STR_RULES.OK)))
        assertThat(passwordUtils.fitsUnameRules("xXXxTheSuperSlay00r"), `is`(equalTo(STR_RULES.OK)))
    }
}

