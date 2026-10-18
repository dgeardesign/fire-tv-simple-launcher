package com.dgeardesign.firetvlauncher.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.dgeardesign.firetvlauncher.databinding.ActivityMainBinding

/** The home screen: a focusable grid of installed apps. */
class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding
    private val adapter = AppAdapter { viewModel.launch(it) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.appGrid.layoutManager = GridLayoutManager(this, COLUMNS)
        binding.appGrid.adapter = adapter
        viewModel.apps.observe(this) { adapter.submit(it) }
    }

    override fun onResume() {
        super.onResume()
        // Pick up newly installed/uninstalled apps whenever we return home.
        viewModel.refresh()
    }

    // As a launcher, Back should not close the home screen.
    @Deprecated("Back is intentionally ignored on the home screen")
    override fun onBackPressed() {}

    private companion object {
        const val COLUMNS = 6
    }
}
