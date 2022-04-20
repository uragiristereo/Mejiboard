package com.github.uragiristereo.mejiboard.presentation.settings

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.github.uragiristereo.mejiboard.domain.usecase.ConvertFileSizeUseCase
import com.github.uragiristereo.mejiboard.presentation.settings.core.SettingsState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import java.io.File
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val convertFileSizeUseCase: ConvertFileSizeUseCase,
) : ViewModel() {
    val state = mutableStateOf(value = SettingsState())
    private var _state by state

    fun updateUiState(updatedState: SettingsState) {
        _state = updatedState
    }

    fun getFormattedFolderSize(file: File) {
        val folderSize = getFolderSize(file)
        val formattedFileSize = convertFileSizeUseCase(sizeBytes = folderSize)

        _state = _state.copy(cacheDirectorySize = formattedFileSize)
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
    ) {
        val half = (0.6f * _state.settingsHeaderSize).toInt()

        _state = _state.copy(
            useBigHeader = if (columnState.firstVisibleItemIndex == 0)
                columnState.firstVisibleItemScrollOffset < half
            else
                false
        )

        if (columnState.firstVisibleItemIndex == 0 && columnState.firstVisibleItemScrollOffset != 0 && !columnState.isScrollInProgress) {
            if (columnState.firstVisibleItemScrollOffset < half)
                onPerformScroll(0)
            else
                onPerformScroll(1)
        }
    }

    suspend fun updateIsCacheCleanedState(file: File) {
        _state = _state.copy(isCacheCleaned = true)
        this.getFormattedFolderSize(file)

        delay(timeMillis = 4000L)

        _state = _state.copy(isCacheCleaned = false)
    }
}