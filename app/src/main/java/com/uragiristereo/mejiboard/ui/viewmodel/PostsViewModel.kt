package com.uragiristereo.mejiboard.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.uragiristereo.mejiboard.model.network.NetworkInstance
import com.uragiristereo.mejiboard.model.network.Post
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import javax.inject.Inject

@HiltViewModel
class PostsViewModel @Inject constructor(
    private val networkInstance: NetworkInstance
) : ViewModel() {
    val postsData = mutableStateOf<ArrayList<Post>>(arrayListOf())
    var postsProgressVisible by mutableStateOf(false)
    var page by mutableStateOf(0)
    var newSearch by mutableStateOf(true)
    var fabVisible by mutableStateOf(false)
    var postsError by mutableStateOf("")

    fun getPosts(searchTags: String, refresh: Boolean, safeListingOnly: Boolean) {
        postsProgressVisible = true

        if (refresh) {
            page = 0
            postsData.value = arrayListOf()
            newSearch = true
        } else newSearch = false

        val tags = if (safeListingOnly) "$searchTags rating:safe" else searchTags

        networkInstance.api.getPosts(page, tags).enqueue(object : Callback<List<Post>> {
            override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                postsProgressVisible = false

                if (refresh) {
                    postsData.value = arrayListOf()
                    page = 0
                } else page += 1

                if (response.code() == 200) {
                    postsError = ""
                    postsData.value.addAll(response.body()!!)
                } else {
                    postsError = response.code().toString()
                }
            }

            override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                postsError = t.message!!
                postsProgressVisible = false
            }
        })
    }
}