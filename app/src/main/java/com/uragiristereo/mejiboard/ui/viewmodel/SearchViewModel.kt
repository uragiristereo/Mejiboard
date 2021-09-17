package com.uragiristereo.mejiboard.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.uragiristereo.mejiboard.model.network.Search
import com.uragiristereo.mejiboard.model.network.NetworkInstance
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val networkInstance: NetworkInstance
) : ViewModel() {
    var searchData: List<Search> by mutableStateOf(listOf())
    var searchProgressVisible by mutableStateOf(false)
    var searchError by mutableStateOf("")
    var tagsQueue: ArrayList<String> = arrayListOf()

    fun getTags(newTag: String) {
        if (newTag != "") {
            val thisUUID = UUID.randomUUID().toString()

            tagsQueue.add(thisUUID)
            searchProgressVisible = true

            networkInstance.api.getTags(newTag).enqueue(object : Callback<List<Search>> {
                override fun onResponse(
                    call: Call<List<Search>>,
                    response: Response<List<Search>>
                ) {
                    if (tagsQueue.lastOrNull() == thisUUID) {
                        tagsQueue = arrayListOf()

                        if (response.code() == 200) {
                            searchError = ""
                            searchData = response.body()!!
                        } else {
                            searchError = response.code().toString()
                        }
                    }

                    searchProgressVisible = false
                }

                override fun onFailure(call: Call<List<Search>>, t: Throwable) {
                    searchError = t.message!!
                    searchProgressVisible = false
                }
            })
        } else clearSearches()
    }

    fun clearSearches() {
        searchData = listOf()
        searchProgressVisible = false
    }
}