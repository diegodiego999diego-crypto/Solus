package com.ccc.solus.viewmodel

import android.app.Application
import android.os.Environment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ccc.solus.data.AudioFile
import com.ccc.solus.data.FileObserverManager
import com.ccc.solus.data.FolderItem
import com.ccc.solus.data.MusicScanner
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

sealed class UiState {
    data object Loading : UiState()
    data class FolderList(val folders: List<FolderItem>) : UiState()
    data class FileList(
        val currentFolder: FolderItem,
        val files: List<AudioFile>,
        val subfolders: List<FolderItem>
    ) : UiState()
    data object Empty : UiState()
}

class MainViewModel(app: Application) : AndroidViewModel(app) {

    private val _state = MutableStateFlow<UiState>(UiState.Loading)
    val state: StateFlow<UiState> = _state.asStateFlow()

    private val observer = FileObserverManager { refresh() }
    private var currentFolder: File? = null

    init {
        rescan()
    }

    /** Escaneo completo desde la raíz /sdcard */
    fun rescan() {
        _state.value = UiState.Loading
        viewModelScope.launch {
            val root = Environment.getExternalStorageDirectory()
            val folders = withContext(Dispatchers.IO) {
                MusicScanner.scanFolders(root)
            }
            _state.value = if (folders.isEmpty()) UiState.Empty
            else UiState.FolderList(folders)
        }
    }

    /** Entrar a una carpeta específica */
    fun openFolder(folder: FolderItem) {
        currentFolder = folder.folder
        observer.observe(folder.folder)
        refresh()
    }

    /** Volver a la lista de carpetas raíz */
    fun goBackToFolders() {
        currentFolder = null
        observer.stop()
        rescan()
    }

    /** Re-escanea la carpeta actual (llamado por el observer) */
    private fun refresh() {
        val folder = currentFolder ?: return
        viewModelScope.launch {
            val (files, subs) = withContext(Dispatchers.IO) {
                MusicScanner.scanFilesIn(folder)
            }
            _state.value = UiState.FileList(
                currentFolder = FolderItem(
                    folder = folder,
                    name = folder.name,
                    path = folder.absolutePath,
                    audioCount = files.size
                ),
                files = files,
                subfolders = subs
            )
        }
    }

    override fun onCleared() {
        observer.stop()
        super.onCleared()
    }
}
