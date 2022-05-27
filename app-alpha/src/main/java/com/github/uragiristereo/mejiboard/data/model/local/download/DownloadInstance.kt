package com.github.uragiristereo.mejiboard.data.model.local.download

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