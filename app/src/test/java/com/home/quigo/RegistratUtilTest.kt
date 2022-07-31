package com.home.quigo

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class RegistrationUtilTest{

    @Test
    fun `empty username returns false`(){
        val result = RegistrationUtil.validateRegistrationInput(
            "",
            "123",
            "123"
        )
        assertThat(result).isFalse()
    }

    @Test
    fun `username already exists returns false`(){
        val result = RegistrationUtil.validateRegistrationInput(
            "cedric",
            "123",
            "123"
        )
        assertThat(result).isFalse()
    }

    @Test
    fun `empty password returns false`(){
        val result = RegistrationUtil.validateRegistrationInput(
            "cedric",
            "",
            ""
        )
        assertThat(result).isFalse()
    }

    @Test
    fun `incorrect repeated password returns false`(){
        val result = RegistrationUtil.validateRegistrationInput(
            "cedric",
            "124",
            "123"
        )
        assertThat(result).isFalse()
    }

    @Test
    fun `password length is less than 6 returns false`(){
        val result = RegistrationUtil.validateRegistrationInput(
            "cedric",
            "123",
            "123"
        )
        assertThat(result).isFalse()
    }

    @Test
    fun `password has no digit returns false`(){
        val result = RegistrationUtil.validateRegistrationInput(
            "cedric",
            "abcd",
            "abcd"
        )
        assertThat(result).isFalse()
    }
}