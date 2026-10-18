package com.dgeardesign.firetvlauncher.models

import android.content.Intent
import android.graphics.drawable.Drawable

/** A launchable app shown in the grid. */
data class AppInfo(
    val label: String,
    val packageName: String,
    val icon: Drawable,
    val launchIntent: Intent
)
