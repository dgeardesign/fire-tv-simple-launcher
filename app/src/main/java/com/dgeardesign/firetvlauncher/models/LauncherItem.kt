package com.dgeardesign.firetvlauncher.models

/** A website shortcut shown in the grid next to apps. */
data class Website(val id: String, val title: String, val url: String)

/** One tile in the grid: either an installed app or a website shortcut. */
sealed class LauncherItem {
    abstract val label: String

    data class App(val info: AppInfo) : LauncherItem() {
        override val label: String get() = info.label
    }

    data class Link(val site: Website) : LauncherItem() {
        override val label: String get() = site.title
    }
}
