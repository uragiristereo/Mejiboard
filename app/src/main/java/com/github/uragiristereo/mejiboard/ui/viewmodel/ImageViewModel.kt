package com.github.uragiristereo.mejiboard.ui.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.uragiristereo.mejiboard.model.network.NetworkInstance
import com.github.uragiristereo.mejiboard.model.network.Tag
import com.github.uragiristereo.mejiboard.util.convertSize
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
    private val networkInstance: NetworkInstance
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

    // download
    lateinit var instance: DownloadInstance

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

    fun download(
        url: String,
        location: File,
        onDownloadProgress: (DownloadInfo) -> Unit = {},
        onDownloadComplete: () -> Unit = {},
    ): DownloadInstance {
        val instance = DownloadInstance(
            call = networkInstance.api.download(url),
            info = mutableStateOf(DownloadInfo())
        )

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
                            var info by instance.info

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

                                    info = DownloadInfo(progress, downloaded, length, path.absolutePath)
                                    onDownloadProgress(info)
                                }

                                outputStream.flush()
                                info.completed = true
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
            }
        })

        return instance
    }

    data class DownloadInfo(
        val progress: Float = 0f,
        val downloaded: Long = 0L,
        val length: Long = 0L,
        var path: String = "",
        var completed: Boolean = false
    )

    data class DownloadInstance(
        val call: Call<ResponseBody>,
        var info: MutableState<DownloadInfo>
    )
}