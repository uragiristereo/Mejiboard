package com.uragiristereo.mejiboard.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uragiristereo.mejiboard.model.database.AppDatabase
import com.uragiristereo.mejiboard.model.database.Bookmark
import com.uragiristereo.mejiboard.model.network.NetworkInstance
import com.uragiristereo.mejiboard.model.network.Post
import com.uragiristereo.mejiboard.model.preferences.PreferencesManager
import com.uragiristereo.mejiboard.util.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager,
    private val networkInstance: NetworkInstance,
    private val appDatabase: AppDatabase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    var okHttpClient = networkInstance.okHttpClient
    var imageLoader = networkInstance.imageLoader

    // preferences
    var theme by mutableStateOf("system")
    var isDesiredThemeDark by mutableStateOf(true)
    var blackTheme by mutableStateOf(false)
    var dohEnabled by mutableStateOf(true)
    var safeListingOnly by mutableStateOf(true)
    var previewSize by mutableStateOf("sample")
    var dohProvider by mutableStateOf("cloudflare")

    // posts, search & settings
    var refreshNeeded by mutableStateOf(false)

    // posts & search
    var searchTags by mutableStateOf("")

    // posts & image
    var selectedPost: Post? = null

    // bookmarks
    var bookmarks by mutableStateOf<List<Bookmark>>(listOf())

    init {
        // load saved state from system
        savedStateHandle.get<Post>(STATE_KEY_SELECTED_POST)?.let {
            selectedPost = it
        }

        viewModelScope.launch {
            // load preferences
            with(preferencesManager.dataStore.data) {
                theme = map { it[THEME] ?: "system" }.first()
                blackTheme = map { it[BLACK_DARK_THEME] ?: false }.first()
                previewSize = map { it[PREVIEW_SIZE] ?: "sample" }.first()
                safeListingOnly = map { it[SAFE_LISTING_ONLY] ?: true }.first()
                dohEnabled = map { it[USE_DNS_OVER_HTTPS] ?: true }.first()
                dohProvider = map { it[DNS_OVER_HTTPS_PROVIDER] ?: "cloudflare" }.first()
            }

//            insertBookmark(10)

            renewNetworkInstance(dohEnabled, dohProvider)

            refreshNeeded = true
        }
    }

    fun renewNetworkInstance(
        useDnsOverHttps: Boolean,
        dohProvider: String
    ) {
        networkInstance.renewInstance(useDnsOverHttps, dohProvider)
        okHttpClient = networkInstance.okHttpClient
        imageLoader = networkInstance.imageLoader
    }

    fun setTheme(theme: String, blackTheme: Boolean) {
        this.theme = theme
        this.blackTheme = blackTheme

        viewModelScope.launch {
            save(THEME, theme)
            save(BLACK_DARK_THEME, blackTheme)
        }
    }

    fun save(key: Preferences.Key<String>, value: String) {
        viewModelScope.launch {
            preferencesManager.dataStore.edit {
                it[key] = value
            }
        }
    }

    fun save(key: Preferences.Key<Boolean>, value: Boolean) {
        viewModelScope.launch {
            preferencesManager.dataStore.edit {
                it[key] = value
            }
        }
    }

    fun insertBookmark(postId: Int) {
        val now = Date
            .from(LocalDateTime.now()
                .toInstant(ZoneOffset.UTC)
            )

        val bookmark = Bookmark(
            id = postId,
            dateAdded = now
        )

        viewModelScope.launch {
            appDatabase.bookmarkDao().insert(bookmark)
        }
    }

    fun getBookmarks() {
        bookmarks = appDatabase.bookmarkDao().get()
        Timber.i(bookmarks.toString())
    }

    fun saveSelectedPost(post: Post) {
        selectedPost = post
        savedStateHandle.set(STATE_KEY_SELECTED_POST, post)
    }
}