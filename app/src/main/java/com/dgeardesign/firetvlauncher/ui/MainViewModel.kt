package com.dgeardesign.firetvlauncher.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.dgeardesign.firetvlauncher.data.AppRepository
import com.dgeardesign.firetvlauncher.models.AppInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** Holds the list of apps so it survives configuration changes. */
class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AppRepository(application)
    private val _apps = MutableLiveData<List<AppInfo>>(emptyList())
    val apps: LiveData<List<AppInfo>> = _apps

    /** Reload the installed-app list off the main thread. */
    fun refresh() {
        viewModelScope.launch {
            _apps.value = withContext(Dispatchers.IO) { repository.loadApps() }
        }
    }

    fun launch(app: AppInfo) = repository.launch(app)
}
