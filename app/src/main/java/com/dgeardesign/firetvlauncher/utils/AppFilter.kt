package com.dgeardesign.firetvlauncher.utils

/** Small helpers for filtering/sorting the app list. Kept pure so it is unit-testable. */
object AppFilter {
    /** Sort case-insensitively by label. */
    fun <T> sortByLabel(items: List<T>, label: (T) -> String): List<T> =
        items.sortedBy { label(it).lowercase() }

    /** Hide this launcher itself from the grid. */
    fun <T> excludePackage(items: List<T>, ownPackage: String, pkg: (T) -> String): List<T> =
        items.filter { pkg(it) != ownPackage }
}
