package com.dgeardesign.firetvlauncher

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class WebsiteTest {
    @Test fun normalizeAddsScheme() = assertEquals("https://youtube.com", normalizeUrl(" youtube.com "))

    @Test fun normalizeKeepsHttp() = assertEquals("http://a.b/c", normalizeUrl("http://a.b/c"))

    @Test fun normalizeRejectsBad() {
        assertNull(normalizeUrl(""))
        assertNull(normalizeUrl("javascript://x"))
        assertNull(normalizeUrl("a b"))
    }

    @Test fun jsonRoundTrip() {
        val sites = listOf(LauncherItem.Website("1", "YT", "https://youtube.com"))
        assertEquals(sites, WebsiteJson.fromJson(WebsiteJson.toJson(sites)))
    }
}
