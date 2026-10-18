package com.dgeardesign.firetvlauncher

import com.dgeardesign.firetvlauncher.utils.AppFilter
import org.junit.Assert.assertEquals
import org.junit.Test

class AppFilterTest {
    @Test
    fun sortsCaseInsensitively() {
        val sorted = AppFilter.sortByLabel(listOf("banana", "Apple", "cherry")) { it }
        assertEquals(listOf("Apple", "banana", "cherry"), sorted)
    }

    @Test
    fun excludesOwnPackage() {
        val result = AppFilter.excludePackage(listOf("a.b", "c.d"), "a.b") { it }
        assertEquals(listOf("c.d"), result)
    }
}
