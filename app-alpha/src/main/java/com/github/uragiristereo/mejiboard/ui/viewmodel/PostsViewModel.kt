package com.github.uragiristereo.mejiboard.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.github.uragiristereo.mejiboard.model.network.NetworkInstance
import com.github.uragiristereo.mejiboard.model.network.Post
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class PostsViewModel @Inject constructor(
    private val networkInstance: NetworkInstance
) : ViewModel() {
    val postsData = mutableStateListOf<Post>()
    var postsProgressVisible by mutableStateOf(false)
    var page by mutableStateOf(0)
    var newSearch by mutableStateOf(true)
    var postsError by mutableStateOf("")

    fun getPosts(searchTags: String, refresh: Boolean, safeListingOnly: Boolean) {
        postsProgressVisible = true
        postsError = ""

        if (refresh) {
            page = 0
            postsData.clear()
            newSearch = true
        } else {
            newSearch = false
            page += 1
        }

        val tags = if (safeListingOnly) "$searchTags rating:safe" else searchTags

        networkInstance.api.getPosts(page, tags).enqueue(object : Callback<List<Post>> {
            override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                postsProgressVisible = false

                if (response.code() == 200) {
                    postsError = ""
                    postsData.addAll(response.body()!!)
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