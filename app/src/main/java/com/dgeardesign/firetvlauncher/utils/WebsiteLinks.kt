package com.dgeardesign.firetvlauncher.utils

import com.dgeardesign.firetvlauncher.models.Website
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

/** Pure helpers for website shortcuts (URL cleanup and JSON storage format). */
object WebsiteLinks {
    private val SCHEME = Regex("^[a-zA-Z][a-zA-Z0-9+.-]*://")
    private val HTTP_HOST = Regex("^https?://([^/?#]+)", RegexOption.IGNORE_CASE)

    /** Adds https:// when no scheme is given; returns null unless the result is a valid http(s) URL. */
    fun normalizeUrl(input: String): String? {
        val trimmed = input.trim()
        if (trimmed.isEmpty() || trimmed.any { it.isWhitespace() }) return null
        val url = if (SCHEME.containsMatchIn(trimmed)) trimmed else "https://$trimmed"
        val authority = HTTP_HOST.find(url)?.groupValues?.get(1) ?: return null
        val host = authority.substringAfterLast('@')
        return if (host.isEmpty() || host.startsWith(":")) null else url
    }

    fun toJson(sites: List<Website>): String {
        val arr = JSONArray()
        sites.forEach { arr.put(JSONObject().put("id", it.id).put("title", it.title).put("url", it.url)) }
        return arr.toString()
    }

    /** Parses stored shortcuts, dropping entries whose URL is invalid. */
    fun fromJson(json: String): List<Website> {
        val arr = JSONArray(json)
        return (0 until arr.length()).mapNotNull { i ->
            val o = arr.optJSONObject(i) ?: return@mapNotNull null
            val url = normalizeUrl(o.optString("url")) ?: return@mapNotNull null
            Website(
                id = o.optString("id").ifBlank { UUID.randomUUID().toString() },
                title = o.optString("title").ifBlank { url },
                url = url
            )
        }
    }
}
