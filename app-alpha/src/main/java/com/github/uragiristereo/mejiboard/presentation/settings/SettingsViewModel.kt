package com.github.uragiristereo.mejiboard.presentation.settings

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.uragiristereo.mejiboard.domain.entity.preferences.AppPreferences
import com.github.uragiristereo.mejiboard.data.repository.local.PreferencesRepository
import com.github.uragiristereo.mejiboard.domain.usecase.common.ConvertFileSizeUseCase
import com.github.uragiristereo.mejiboard.presentation.common.mapper.update
import com.github.uragiristereo.mejiboard.presentation.settings.core.SettingsState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val convertFileSizeUseCase: ConvertFileSizeUseCase,
    val preferencesRepository: PreferencesRepository,
) : ViewModel() {
    val state = mutableStateOf(value = SettingsState())
    private val _state by state

    var preferences by mutableStateOf(AppPreferences())
        private set

    init {
        preferencesRepository.data
            .onEach { preferences = it }
            .launchIn(viewModelScope)
    }

    inline fun updatePreferences(crossinline body: (AppPreferences) -> AppPreferences) {
        viewModelScope.launch {
            val data = body(preferences)

            preferencesRepository.update(data)
        }
    }

    fun getFormattedFolderSize(file: File) {
        val folderSize = getFolderSize(file)
        val formattedFileSize = convertFileSizeUseCase(sizeBytes = folderSize)

        state.update { it.copy(cacheDirectorySize = formattedFileSize) }
    }

    private fun getFolderSize(file: File): Long {
        var size = 0L

        if (file.isDirectory) {
            file.walk().forEach {
                if (it.isFile)
                    size += it.length()
            }
        }

        return size
    }

    fun shouldUseBigHeader(
        columnState: LazyListState,
        onPerformScroll: (index: Int) -> Unit,
    ): Boolean {
        val half = (0.6f * _state.settingsHeaderSize).toInt()

        if (columnState.firstVisibleItemIndex == 0 && columnState.firstVisibleItemScrollOffset != 0 && !columnState.isScrollInProgress) {
            if (columnState.firstVisibleItemScrollOffset < half)
                onPerformScroll(0)
            else
                onPerformScroll(1)
        }

        return when (columnState.firstVisibleItemIndex) {
            0 -> columnState.firstVisibleItemScrollOffset < half
            else -> false
        }
    }

    suspend fun updateIsCacheCleanedState(file: File) {
        state.update { it.copy(isCacheCleaned = true) }

        this.getFormattedFolderSize(file)

        delay(timeMillis = 4000L)

        state.update { it.copy(isCacheCleaned = false) }
    }
}