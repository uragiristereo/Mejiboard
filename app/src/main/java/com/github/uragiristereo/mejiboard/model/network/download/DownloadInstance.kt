package com.github.uragiristereo.mejiboard.model.network.download

import com.github.uragiristereo.mejiboard.model.network.DownloadInfo
import okhttp3.ResponseBody
import retrofit2.Call

class DownloadInstance(
    val call: Call<ResponseBody>
) {
    var info = DownloadInfo()

    fun cancel() {
        info.status = "canceled"
        call.cancel()
    }
}