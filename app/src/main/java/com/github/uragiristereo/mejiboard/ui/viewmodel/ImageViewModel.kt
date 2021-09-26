package com.github.uragiristereo.mejiboard.ui.viewmodel

import android.content.Context
import android.media.MediaScannerConnection
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.uragiristereo.mejiboard.model.network.DownloadInfo
import com.github.uragiristereo.mejiboard.model.network.NetworkInstance
import com.github.uragiristereo.mejiboard.model.network.Tag
import com.github.uragiristereo.mejiboard.model.network.download.DownloadInstance
import com.github.uragiristereo.mejiboard.model.network.download.DownloadRepository
import com.github.uragiristereo.mejiboard.util.FileHelper.convertSize
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*
import javax.inject.Inject

@HiltViewModel
class ImageViewModel @Inject constructor(
    private val networkInstance: NetworkInstance,
    private val downloadRepository: DownloadRepository
) : ViewModel() {
    var imageSize by mutableStateOf("")
    var originalImageSize by mutableStateOf("")
    var shareModalVisible by mutableStateOf(false)

    var showOriginalImage by mutableStateOf(false)
    var originalImageShown by mutableStateOf(false)
    var originalImageUpdated by mutableStateOf(false)

    var infoData = mutableStateOf<List<Tag>>(listOf())
    var infoProgressVisible by mutableStateOf(false)
    var showTagsIsCollapsed by mutableStateOf(true)

    fun checkImage(url: String, original: Boolean = false) {
        if (original) originalImageSize = "Loading..." else imageSize = "Loading..."

        networkInstance.api.checkImage(url).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                val size = response.headers()["content-length"] ?: "0"

                if (original)
                    originalImageSize = convertSize(size.toInt())
                else
                    imageSize = convertSize(size.toInt())
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                if (original)
                    originalImageSize = ""
                else
                    imageSize = ""
            }
        })
    }

    fun getTagsInfo(names: String) {
        infoProgressVisible = true
        infoData.value = listOf()

        networkInstance.api.getTagsInfo(names).enqueue(object : Callback<List<Tag>> {
            override fun onResponse(call: Call<List<Tag>>, response: Response<List<Tag>>) {
                infoData.value = response.body()!!
                infoProgressVisible = false
            }

            override fun onFailure(call: Call<List<Tag>>, t: Throwable) {
                infoProgressVisible = false
            }
        })
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
}