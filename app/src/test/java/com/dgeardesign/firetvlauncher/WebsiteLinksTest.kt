package com.dgeardesign.firetvlauncher

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import com.dgeardesign.firetvlauncher.models.Website
import com.dgeardesign.firetvlauncher.utils.WebsiteLinks

class WebsiteLinksTest {
    @Test fun normalizeAddsScheme() = assertEquals("https://youtube.com", WebsiteLinks.normalizeUrl(" youtube.com "))

    @Test fun normalizeKeepsHttp() = assertEquals("http://a.b/c", WebsiteLinks.normalizeUrl("http://a.b/c"))

    @Test fun normalizeRejectsBad() {
        assertNull(WebsiteLinks.normalizeUrl(""))
        assertNull(WebsiteLinks.normalizeUrl("javascript://x"))
        assertNull(WebsiteLinks.normalizeUrl("a b"))
    }

    @Test fun jsonRoundTrip() {
        val sites = listOf(Website("1", "YT", "https://youtube.com"))
        assertEquals(sites, WebsiteLinks.fromJson(WebsiteLinks.toJson(sites)))
    }
}
