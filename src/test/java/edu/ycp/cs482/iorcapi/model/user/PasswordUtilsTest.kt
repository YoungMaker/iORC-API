package edu.ycp.cs482.iorcapi.model.user

import org.junit.Test

import org.junit.Assert.*
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.equalTo

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
}