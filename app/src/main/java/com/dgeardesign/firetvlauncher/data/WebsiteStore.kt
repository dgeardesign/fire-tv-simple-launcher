package com.dgeardesign.firetvlauncher.data

import android.content.Context
import com.dgeardesign.firetvlauncher.models.Website
import com.dgeardesign.firetvlauncher.utils.WebsiteLinks
import java.io.File
import java.util.UUID

/** Persists website shortcuts in filesDir/websites.json; seeds editable defaults on first run. */
class WebsiteStore(context: Context) {
    private val file = File(context.filesDir, "websites.json")

    fun load(): List<Website> {
        if (!file.exists()) {
            val defaults = defaults()
            save(defaults)
            return defaults
        }
        return try {
            WebsiteLinks.fromJson(file.readText())
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun save(sites: List<Website>) {
        file.writeText(WebsiteLinks.toJson(sites))
    }

    private fun defaults() = listOf(
        Website(UUID.randomUUID().toString(), "YouTube", "https://www.youtube.com"),
        Website(UUID.randomUUID().toString(), "Netflix", "https://www.netflix.com"),
        Website(UUID.randomUUID().toString(), "Google", "https://www.google.com")
    )
}
