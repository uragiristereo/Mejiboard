package com.uragiristereo.mejiboard.ui.viewmodel

import android.graphics.drawable.Drawable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.uragiristereo.mejiboard.model.Tag
import com.uragiristereo.mejiboard.model.network.NetworkInstance
import com.uragiristereo.mejiboard.util.convertSize
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class ImageViewModel @Inject constructor(
    _networkInstance: NetworkInstance
) : ViewModel() {
    val retrofitInstance = _networkInstance

    var imageSize by mutableStateOf("")
    var originalImageSize by mutableStateOf("")
    var shareModalVisible by mutableStateOf(false)

    var showOriginalImage by mutableStateOf(false)
    var originalImageShown by mutableStateOf(false)
    var originalImageUpdated by mutableStateOf(false)
    var originalImageDrawable: Drawable? = null

    var infoData = mutableStateOf<List<Tag>>(listOf())
    var infoProgressVisible by mutableStateOf(false)
    var showTagsIsCollapsed by mutableStateOf(true)

    fun checkImage(url: String, original: Boolean = false) {
        if (original) originalImageSize = "Loading..." else imageSize = "Loading..."

        retrofitInstance.api.checkImage(url).enqueue(object : Callback<Void> {
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

        retrofitInstance.api.getTagsInfo(names).enqueue(object : Callback<List<Tag>> {
            override fun onResponse(call: Call<List<Tag>>, response: Response<List<Tag>>) {
                infoData.value = response.body()!!
                infoProgressVisible = false
            }

            override fun onFailure(call: Call<List<Tag>>, t: Throwable) {
                infoProgressVisible = false
            }
        })
    }
}