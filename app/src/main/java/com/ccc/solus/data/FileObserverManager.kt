package com.ccc.solus.data

import android.os.FileObserver
import java.io.File

/**
 * Observa una carpeta específica y avisa cuando cambia (archivos creados,
 * borrados, movidos o modificados). Se usa para observar SOLO la carpeta
 * visible actualmente, no todo /sdcard, para no reventar la batería.
 */
class FileObserverManager(
    private val onChanged: () -> Unit
) {
    private var observer: FileObserver? = null

    fun observe(folder: File) {
        stop()
        if (!folder.isDirectory) return

        observer = object : FileObserver(
            folder.absolutePath,
            CREATE or DELETE or MOVED_FROM or MOVED_TO or CLOSE_WRITE
        ) {
            override fun onEvent(event: Int, path: String?) {
                onChanged()
            }
        }.also { it.startWatching() }
    }

    fun stop() {
        observer?.stopWatching()
        observer = null
    }
}
