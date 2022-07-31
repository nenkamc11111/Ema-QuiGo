package com.home.quigo

import com.google.common.truth.Truth
import org.junit.Assert.*
import org.junit.Test

class PostUtilTest{

    @Test
    fun `empty title returns false`(){
        val result = PostUtil.validatePostInput(
            "",
            "Demo Description",
            "500"
        )
        Truth.assertThat(result).isFalse()
    }

    @Test
    fun `empty description returns false`(){
        val result = PostUtil.validatePostInput(
            "Demo Title",
            "",
            "500"
        )
        Truth.assertThat(result).isFalse()
    }

    @Test
    fun `empty price returns false`(){
        val result = PostUtil.validatePostInput(
            "Demo Title",
            "demo title",
            ""
        )
        Truth.assertThat(result).isFalse()
    }
}