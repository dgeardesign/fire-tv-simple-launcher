package com.dgeardesign.firetvlauncher.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.dgeardesign.firetvlauncher.data.AppRepository
import com.dgeardesign.firetvlauncher.data.WebsiteStore
import com.dgeardesign.firetvlauncher.models.LauncherItem
import com.dgeardesign.firetvlauncher.models.Website
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

/** Holds apps and website shortcuts so they survive configuration changes. */
class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AppRepository(application)
    private val store = WebsiteStore(application)
    private var websites: List<Website> = emptyList()

    private val _items = MutableLiveData<List<LauncherItem>>(emptyList())
    val items: LiveData<List<LauncherItem>> = _items

    /** Reload apps and shortcuts off the main thread. */
    fun refresh() {
        viewModelScope.launch {
            val (apps, sites) = withContext(Dispatchers.IO) { repository.loadApps() to store.load() }
            websites = sites
            _items.value = apps.map { LauncherItem.App(it) } + sites.map { LauncherItem.Link(it) }
        }
    }

    fun launch(item: LauncherItem) = when (item) {
        is LauncherItem.App -> repository.launch(item.info)
        is LauncherItem.Link -> repository.openWebsite(item.site)
    }

    /** Adds a shortcut, or updates the one with [id]. */
    fun saveWebsite(id: String?, title: String, url: String) {
        websites = if (id == null) {
            websites + Website(UUID.randomUUID().toString(), title, url)
        } else {
            websites.map { if (it.id == id) it.copy(title = title, url = url) else it }
        }
        persistAndRefresh()
    }

    fun removeWebsite(site: Website) {
        websites = websites.filter { it.id != site.id }
        persistAndRefresh()
    }

    private fun persistAndRefresh() {
        val snapshot = websites
        viewModelScope.launch {
            withContext(Dispatchers.IO) { store.save(snapshot) }
            refresh()
        }
    }
}
