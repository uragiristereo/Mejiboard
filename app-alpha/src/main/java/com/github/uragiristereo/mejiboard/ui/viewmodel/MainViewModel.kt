package com.github.uragiristereo.mejiboard.ui.viewmodel

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.MediaScannerConnection
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.FileProvider
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.uragiristereo.mejiboard.BuildConfig
import com.github.uragiristereo.mejiboard.R
import com.github.uragiristereo.mejiboard.model.database.AppDatabase
import com.github.uragiristereo.mejiboard.model.database.Bookmark
import com.github.uragiristereo.mejiboard.model.network.*
import com.github.uragiristereo.mejiboard.model.network.download.DownloadBroadcastReceiver
import com.github.uragiristereo.mejiboard.model.network.download.DownloadInstance
import com.github.uragiristereo.mejiboard.model.network.download.DownloadRepository
import com.github.uragiristereo.mejiboard.model.preferences.PreferencesManager
import com.github.uragiristereo.mejiboard.util.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.io.*
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager,
    private val networkInstance: NetworkInstance,
    private val appDatabase: AppDatabase,
    private val savedStateHandle: SavedStateHandle,
    private val downloadRepository: DownloadRepository
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
    var autoCleanCache by mutableStateOf(true)
    var blockFromRecents by mutableStateOf(true)
    var videoVolume by mutableStateOf(0.5f)

    // posts, search & settings
    var refreshNeeded by mutableStateOf(false)

    // posts & search
    var searchTags by mutableStateOf("")

    // posts & image
    var selectedPost: Post? = null

    // bookmarks
    var bookmarks by mutableStateOf<List<Bookmark>>(listOf())

    // image
    private var notificationCount = 0

    // update
    var updateStatus by mutableStateOf("idle")
    var latestVersion by mutableStateOf(ReleaseInfo(BuildConfig.VERSION_CODE, BuildConfig.VERSION_NAME, false))
    var updateDialogVisible by mutableStateOf(false)
    var splashShown by mutableStateOf(false)
    var remindLaterCounter by mutableStateOf(0)


    init {
        // load saved state from system
        savedStateHandle.get<Post>(STATE_KEY_SELECTED_POST)?.let {
            selectedPost = it
        }
        savedStateHandle.get<Int>(STATE_KEY_NOTIFICATION_COUNT)?.let {
            notificationCount = it
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
                autoCleanCache = map { it[AUTO_CLEAN_CACHE] ?: true }.first()
                blockFromRecents = map { it[BLOCK_CONTENT_FROM_RECENTS] ?: false }.first()
                remindLaterCounter = map { it[REMIND_LATER_UPDATE_COUNTER] ?: -1 }.first()
                videoVolume = map { it[VIDEO_VOLUME] ?: 0.5f }.first()
            }

//            insertBookmark(10)

            refreshNeeded = true
            renewNetworkInstance(dohEnabled, dohProvider)
            incrementLaterCounter()
        }

        viewModelScope.launch {
            with(preferencesManager.dataStore.data) {
                map { it[BLOCK_CONTENT_FROM_RECENTS] ?: false }.collect { value ->
                    preferencesManager.setBlockFromRecents(value)
                }
            }
        }
    }

    fun getNewNotificationCount(): Int {
        notificationCount = notificationCount.inc()
        savedStateHandle.set(STATE_KEY_NOTIFICATION_COUNT, notificationCount)
        return notificationCount
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

    fun save(key: Preferences.Key<Int>, value: Int) {
        viewModelScope.launch {
            preferencesManager.dataStore.edit {
                it[key] = value
            }
        }
    }

    fun save(key: Preferences.Key<Float>, value: Float) {
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

    fun setPermissionState(state: String) {
        preferencesManager.setPermissionState(state)
    }

    fun getPermissionState(): String {
        return preferencesManager.getPermissionState()
    }

    private fun download(
        context: Context,
        url: String,
        location: File,
        onDownloadProgress: (DownloadInfo) -> Unit = {},
        onDownloadComplete: () -> Unit = {},
    ): DownloadInstance {
        val instance = DownloadInstance(networkInstance.api.download(url))
        instance.info.status = "downloading"

        instance.call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    response.body()?.let { body ->
                        viewModelScope.launch(Dispatchers.IO) {
                            val filename = File(url).name
                            val path =  File(location, filename)
                            val length = body.contentLength()
                            var inputStream: InputStream? = null
                            var outputStream: OutputStream? = null

                            try {
                                val buffer = ByteArray(8192)
                                var downloaded = 0L
                                var read: Int

                                inputStream = body.byteStream()
                                outputStream = FileOutputStream(path)

                                while (inputStream.read(buffer).also { read = it } != -1) {
                                    outputStream.write(buffer, 0, read)
                                    downloaded += read.toLong()
                                    val progress = downloaded.toFloat() / length.toFloat()

                                    instance.info = DownloadInfo(progress, downloaded, length, path.absolutePath, "downloading")
                                    onDownloadProgress(instance.info)
                                }

                                outputStream.flush()
                                instance.info.status = "completed"
                                MediaScannerConnection.scanFile(context, arrayOf(path.absolutePath.toString()), null, null)
                                onDownloadComplete()
                            } catch (e: IOException) {
                                e.printStackTrace()
                            } finally {
                                inputStream?.close()
                                outputStream?.close()
                            }
                        }
                    }
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
                instance.info.status = "canceled"
                val file = File(instance.info.path)
                if (file.exists())
                    file.delete()
            }
        })

        return instance
    }

    fun newDownloadInstance(context: Context, postId: Int, url: String, location: File): DownloadInstance? {
        return if (downloadRepository.isInstanceAlreadyAdded(postId))
            null
        else {
            val instance = download(context, url, location)
            downloadRepository.addInstance(postId, instance)
            instance
        }
    }

    fun getInstance(postId: Int): DownloadInstance? {
        return downloadRepository.getInstance(postId)
    }

    fun removeInstance(postId: Int) {
        downloadRepository.removeInstance(postId)
    }

    fun trackDownloadProgress(
        context: Context,
        post: Post,
        instance: DownloadInstance
    ) {
        viewModelScope.launch {
            val notificationManager = NotificationManagerCompat.from(context)
            val notificationId = getNewNotificationCount()
            val cancelDownloadIntent = Intent(context, DownloadBroadcastReceiver::class.java).apply {
                action = post.id.toString()
                putExtra("notificationId", notificationId)
            }
            val cancelDownloadPendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                cancelDownloadIntent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT
            )

            val notification = NotificationCompat.Builder(context, "downloads")
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setContentTitle("Downloading post ${post.id}")
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setAutoCancel(true)
                .addAction(R.drawable.cancel, "Cancel", cancelDownloadPendingIntent)

            var downloadSpeed = 0
            var lastDownloaded: Long

            while (instance.info.status == "downloading") {
                notificationManager.apply {
                    notification
                        .setProgress(100, instance.info.progress.times(100).toInt(), instance.info.progress == 0f)
                        .setSubText("${instance.info.progress.times(100).toInt()}% - ${FileHelper.convertSize(downloadSpeed)}/s")
                        .setContentText("${FileHelper.convertSize(instance.info.downloaded.toInt())} / ${FileHelper.convertSize(instance.info.length.toInt())}")
                    notify(notificationId, notification.build())
                }
                lastDownloaded = instance.info.downloaded
                delay(1300)
                downloadSpeed = ((instance.info.downloaded - lastDownloaded) / 1.3f).toInt()
            }

            val openDownloadedFileIntent = Intent().apply {
                action = Intent.ACTION_VIEW
                val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", File(instance.info.path))
                val contentResolver = context.contentResolver
                setDataAndType(uri, contentResolver.getType(uri))
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            val pendingOpenDownloadedFileIntent = PendingIntent.getActivity(context, 0, openDownloadedFileIntent, PendingIntent.FLAG_IMMUTABLE)

            if (instance.info.status == "completed") {
                removeInstance(post.id)
                notificationManager.apply {
                    notification
                        .setSmallIcon(android.R.drawable.stat_sys_download_done)
                        .setContentTitle("Post ${post.id}")
                        .setProgress(0, 0, false)
                        .setSubText(null)
                        .setContentText("Download complete • ${FileHelper.convertSize(instance.info.length.toInt())}")
                        .setOngoing(false)
                        .setAutoCancel(true)
                        .setOnlyAlertOnce(false)
                        .clearActions()
                        .setContentIntent(pendingOpenDownloadedFileIntent)
                    notify(notificationId, notification.build())
                }
            }
        }
    }

    fun checkForUpdate() {
        updateStatus = "checking"

        networkInstance.api.checkForUpdate().enqueue(
            object : Callback<AppUpdate> {
                override fun onResponse(call: Call<AppUpdate>, response: Response<AppUpdate>) {
                    if (response.isSuccessful) {
                        val currentVersion = BuildConfig.VERSION_CODE
//                        val currentVersion = 12004

                        response.body()?.let { appUpdate ->
                            val releases = appUpdate.releases.sortedByDescending { it.versionCode }
                            val releasesNewerThanCurrent = releases.filter { it.versionCode > currentVersion }
                            val updateRequired = releasesNewerThanCurrent.any { it.updateRequired }
//                            updateStatus = "update_available"

                            if (releasesNewerThanCurrent.isNotEmpty()) {
                                latestVersion = releasesNewerThanCurrent[0]

                                updateStatus =
                                    if (updateRequired)
                                        "update_required"
                                    else
                                        "update_available"
                            } else
                                updateStatus = "latest"

                            Timber.i(updateStatus)
                        }
                    } else
                        updateStatus = "failed"
                }

                override fun onFailure(call: Call<AppUpdate>, t: Throwable) {
                    updateStatus = "failed"
                }
            }
        )
    }

    private fun incrementLaterCounter() {
        if (remindLaterCounter >= 10)
            remindLaterCounter = -1
        else
            if (remindLaterCounter != -1)
                remindLaterCounter += 1

        save(REMIND_LATER_UPDATE_COUNTER, remindLaterCounter)
    }
}