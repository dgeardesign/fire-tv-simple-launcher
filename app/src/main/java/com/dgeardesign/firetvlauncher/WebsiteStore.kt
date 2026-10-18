package com.dgeardesign.firetvlauncher

import android.content.Context
import java.io.File
import java.util.UUID

/** Persists website shortcuts in a JSON file; seeds defaults on first run. */
class WebsiteStore(context: Context) {
    private val file = File(context.filesDir, "websites.json")

    fun load(): MutableList<LauncherItem.Website> {
        if (!file.exists()) {
            val defaults = defaultSites().toMutableList()
            save(defaults)
            return defaults
        }
        return try {
            WebsiteJson.fromJson(file.readText()).toMutableList()
        } catch (e: Exception) {
            mutableListOf()
        }
    }

    fun save(sites: List<LauncherItem.Website>) {
        file.writeText(WebsiteJson.toJson(sites))
    }

    private fun defaultSites() = listOf(
        LauncherItem.Website(UUID.randomUUID().toString(), "YouTube", "https://www.youtube.com"),
        LauncherItem.Website(UUID.randomUUID().toString(), "Netflix", "https://www.netflix.com"),
        LauncherItem.Website(UUID.randomUUID().toString(), "Google", "https://www.google.com")
    )
}
