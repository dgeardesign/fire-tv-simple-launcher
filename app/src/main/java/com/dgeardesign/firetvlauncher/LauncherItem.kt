package com.dgeardesign.firetvlauncher

import org.json.JSONArray
import org.json.JSONObject

/** An entry in the launcher grid: either an installed app or a website shortcut. */
sealed class LauncherItem {
    abstract val label: String

    data class App(val packageName: String, override val label: String) : LauncherItem()

    data class Website(val id: String, override val label: String, val url: String) : LauncherItem()
}

/** Normalises user input into an http(s) URL, or returns null if it is not valid. */
fun normalizeUrl(input: String): String? {
    val trimmed = input.trim()
    if (trimmed.isEmpty() || trimmed.any { it.isWhitespace() }) return null
    val withScheme = if (Regex("^[a-zA-Z][a-zA-Z0-9+.-]*://").containsMatchIn(trimmed)) trimmed else "https://$trimmed"
    val match = Regex("^(https?)://([^/?#]+)", RegexOption.IGNORE_CASE).find(withScheme) ?: return null
    val host = match.groupValues[2].substringAfterLast('@')
    return if (host.isEmpty() || host.startsWith(":")) null else withScheme
}

/** JSON (de)serialisation of the stored website shortcuts. */
object WebsiteJson {
    fun toJson(sites: List<LauncherItem.Website>): String {
        val arr = JSONArray()
        sites.forEach {
            arr.put(JSONObject().put("id", it.id).put("title", it.label).put("url", it.url))
        }
        return arr.toString()
    }

    fun fromJson(json: String): List<LauncherItem.Website> {
        val arr = JSONArray(json)
        return (0 until arr.length()).mapNotNull { i ->
            val o = arr.optJSONObject(i) ?: return@mapNotNull null
            val url = normalizeUrl(o.optString("url")) ?: return@mapNotNull null
            val title = o.optString("title").ifBlank { url }
            LauncherItem.Website(o.optString("id").ifBlank { java.util.UUID.randomUUID().toString() }, title, url)
        }
    }
}
