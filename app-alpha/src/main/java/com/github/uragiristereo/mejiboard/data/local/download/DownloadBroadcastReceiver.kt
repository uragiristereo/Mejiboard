package com.github.uragiristereo.mejiboard.data.local.download

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import com.github.uragiristereo.mejiboard.data.repository.local.DownloadRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DownloadBroadcastReceiver : BroadcastReceiver() {
    @Inject
    lateinit var downloadRepository: DownloadRepository

    override fun onReceive(context: Context?, intent: Intent?) {
        val postId = intent?.action?.toInt()
        val notificationId = intent?.extras?.getInt("notificationId", -1)
        val instance = postId?.let { downloadRepository.getInstance(it) }
        val notificationManager = context?.let { NotificationManagerCompat.from(it) }
        instance?.cancel()
        postId?.let { downloadRepository.removeInstance(it) }
        notificationId?.let { notificationManager?.cancel(it) }
    }
}