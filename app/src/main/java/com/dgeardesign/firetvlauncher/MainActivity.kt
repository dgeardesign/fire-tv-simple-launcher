package com.dgeardesign.firetvlauncher

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    private lateinit var store: WebsiteStore
    private lateinit var adapter: TileAdapter
    private lateinit var websites: MutableList<LauncherItem.Website>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        store = WebsiteStore(this)
        websites = store.load()

        adapter = TileAdapter(
            iconFor = { app -> runCatching { packageManager.getApplicationIcon(app.packageName) }.getOrNull() },
            onClick = ::launch,
            onLongClick = ::showOptions
        )
        findViewById<RecyclerView>(R.id.grid).apply {
            layoutManager = GridLayoutManager(this@MainActivity, 5)
            adapter = this@MainActivity.adapter
        }
        findViewById<Button>(R.id.addLink).setOnClickListener { editWebsite(null) }
    }

    override fun onResume() {
        super.onResume()
        refresh()
    }

    private fun installedApps(): List<LauncherItem.App> {
        val pm = packageManager
        val leanback = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LEANBACK_LAUNCHER)
        val regular = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        return (pm.queryIntentActivities(leanback, 0) + pm.queryIntentActivities(regular, 0))
            .map { it.activityInfo.packageName to it.loadLabel(pm).toString() }
            .filter { it.first != packageName }
            .distinctBy { it.first }
            .sortedBy { it.second.lowercase() }
            .map { LauncherItem.App(it.first, it.second) }
    }

    private fun refresh() {
        adapter.items = installedApps() + websites
    }

    private fun launch(item: LauncherItem) {
        val intent = when (item) {
            is LauncherItem.App ->
                packageManager.getLeanbackLaunchIntentForPackage(item.packageName)
                    ?: packageManager.getLaunchIntentForPackage(item.packageName)
            is LauncherItem.Website -> Intent(Intent.ACTION_VIEW, Uri.parse(item.url))
        }
        try {
            startActivity(intent ?: throw ActivityNotFoundException())
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, R.string.cannot_open, Toast.LENGTH_SHORT).show()
        }
    }

    private fun showOptions(item: LauncherItem) {
        if (item !is LauncherItem.Website) return
        AlertDialog.Builder(this)
            .setTitle(item.label)
            .setItems(arrayOf(getString(R.string.edit), getString(R.string.remove))) { _, which ->
                if (which == 0) editWebsite(item) else {
                    websites.remove(item)
                    store.save(websites)
                    refresh()
                }
            }
            .show()
    }

    private fun editWebsite(existing: LauncherItem.Website?) {
        val title = EditText(this).apply { hint = getString(R.string.link_title); setText(existing?.label) }
        val url = EditText(this).apply { hint = getString(R.string.link_url); setText(existing?.url) }
        val form = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 16, 48, 0)
            addView(title)
            addView(url)
        }
        val dialog = AlertDialog.Builder(this)
            .setView(form)
            .setPositiveButton(R.string.save, null)
            .setNegativeButton(R.string.cancel, null)
            .create()
        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val normalized = normalizeUrl(url.text.toString())
                if (normalized == null) {
                    url.error = getString(R.string.invalid_url)
                    return@setOnClickListener
                }
                val label = title.text.toString().trim().ifEmpty { normalized }
                if (existing == null) {
                    websites.add(LauncherItem.Website(java.util.UUID.randomUUID().toString(), label, normalized))
                } else {
                    websites[websites.indexOf(existing)] = existing.copy(label = label, url = normalized)
                }
                store.save(websites)
                refresh()
                dialog.dismiss()
            }
        }
        dialog.show()
    }
}
