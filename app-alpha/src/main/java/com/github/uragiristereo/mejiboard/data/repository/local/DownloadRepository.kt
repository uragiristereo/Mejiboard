package com.github.uragiristereo.mejiboard.data.repository.local

import com.github.uragiristereo.mejiboard.data.local.download.DownloadInstance


class DownloadRepository {
    private val downloadList: MutableMap<Int, DownloadInstance> = mutableMapOf()

    fun addInstance(postId: Int, instance: DownloadInstance) {
        downloadList[postId] = instance
    }

    fun isInstanceAlreadyAdded(postId: Int): Boolean {
        return downloadList.containsKey(postId)
    }

    fun getInstance(postId: Int): DownloadInstance? {
        return downloadList[postId]
    }

    fun removeInstance(postId: Int) {
        downloadList.remove(postId)
    }
}