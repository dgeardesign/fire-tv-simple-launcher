package com.dgeardesign.firetvlauncher.ui

import android.app.AlertDialog
import android.os.Bundle
import android.widget.EditText
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.dgeardesign.firetvlauncher.R
import com.dgeardesign.firetvlauncher.databinding.ActivityMainBinding
import com.dgeardesign.firetvlauncher.models.LauncherItem
import com.dgeardesign.firetvlauncher.models.Website
import com.dgeardesign.firetvlauncher.utils.WebsiteLinks

/** The home screen: a focusable grid of installed apps and website shortcuts. */
class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding
    private val adapter = AppAdapter(onClick = { viewModel.launch(it) }, onLongClick = ::showOptions)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.appGrid.layoutManager = GridLayoutManager(this, COLUMNS)
        binding.appGrid.adapter = adapter
        viewModel.items.observe(this) { adapter.submit(it) }
        binding.addLink.setOnClickListener { editWebsite(null) }
    }

    override fun onResume() {
        super.onResume()
        // Pick up newly installed/uninstalled apps whenever we return home.
        viewModel.refresh()
    }

    // As a launcher, Back should not close the home screen.
    @Deprecated("Back is intentionally ignored on the home screen")
    override fun onBackPressed() {}

    private fun showOptions(item: LauncherItem) {
        if (item !is LauncherItem.Link) return
        AlertDialog.Builder(this)
            .setTitle(item.label)
            .setItems(arrayOf(getString(R.string.edit), getString(R.string.remove))) { _, which ->
                if (which == 0) editWebsite(item.site) else viewModel.removeWebsite(item.site)
            }
            .show()
    }

    private fun editWebsite(existing: Website?) {
        val title = EditText(this).apply { hint = getString(R.string.link_title); setText(existing?.title) }
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
            // Override the click so the dialog stays open while the URL is invalid.
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val normalized = WebsiteLinks.normalizeUrl(url.text.toString())
                if (normalized == null) {
                    url.error = getString(R.string.invalid_url)
                    return@setOnClickListener
                }
                val label = title.text.toString().trim().ifEmpty { normalized }
                viewModel.saveWebsite(existing?.id, label, normalized)
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    private companion object {
        const val COLUMNS = 6
    }
}
