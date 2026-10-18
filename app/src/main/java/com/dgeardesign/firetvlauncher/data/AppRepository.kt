package com.dgeardesign.firetvlauncher.data

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import com.dgeardesign.firetvlauncher.models.AppInfo
import com.dgeardesign.firetvlauncher.models.Website
import com.dgeardesign.firetvlauncher.utils.AppFilter

/** Handles discovering installed apps and launching them. */
class AppRepository(private val context: Context) {

    private val pm: PackageManager = context.packageManager

    /**
     * Returns all apps that can be started from a home screen, sorted by name.
     * Both LEANBACK_LAUNCHER (TV apps) and LAUNCHER (regular apps) are included,
     * de-duplicated by package; TV-specific intents win.
     */
    fun loadApps(): List<AppInfo> {
        val byPackage = LinkedHashMap<String, AppInfo>()
        for (category in listOf(Intent.CATEGORY_LEANBACK_LAUNCHER, Intent.CATEGORY_LAUNCHER)) {
            val query = Intent(Intent.ACTION_MAIN).addCategory(category)
            for (info in pm.queryIntentActivities(query, 0)) {
                val pkg = info.activityInfo.packageName
                if (byPackage.containsKey(pkg)) continue
                val intent = Intent(Intent.ACTION_MAIN).apply {
                    addCategory(category)
                    setClassName(pkg, info.activityInfo.name)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
                }
                byPackage[pkg] = AppInfo(
                    label = info.loadLabel(pm).toString(),
                    packageName = pkg,
                    icon = info.loadIcon(pm),
                    launchIntent = intent
                )
            }
        }
        val visible = AppFilter.excludePackage(byPackage.values.toList(), context.packageName) { it.packageName }
        return AppFilter.sortByLabel(visible) { it.label }
    }

    /** Starts the given app; shows a toast if it can't be launched. */
    fun launch(app: AppInfo) {
        try {
            context.startActivity(app.launchIntent)
        } catch (e: Exception) {
            Toast.makeText(context, "Unable to launch ${app.label}", Toast.LENGTH_SHORT).show()
        }
    }

    /** Opens the URL in the default browser; shows a toast if none can handle it. */
    fun openWebsite(site: Website) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(site.url)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Unable to open ${site.title}", Toast.LENGTH_SHORT).show()
        }
    }
}
