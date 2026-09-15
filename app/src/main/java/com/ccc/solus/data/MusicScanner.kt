        package com.ccc.solus.data

import java.io.File

object MusicScanner {

    val AUDIO_EXTENSIONS = setOf(
        "mp3", "flac", "m4a", "aac", "ogg", "opus", "wav", "wma", "alac", "aiff", "mp4", "3gp"
    )

    // Carpetas que se ignoran por completo
    private val IGNORED_DIRS = setOf(
        "Android", "data", "obb"
    )

    // Carpetas raíz permitidas (solo se escanean estas)
    private val ROOT_WHITELIST = listOf(
        "Download",
        "Music",
        "Movies",
        "Recordings"
    )

    fun isAudio(file: File): Boolean {
        return file.isFile && file.extension.lowercase() in AUDIO_EXTENSIONS
    }

    /**
     * Escanea SOLO las carpetas de la whitelist (Download, Music, Movies,
     * Recordings) y devuelve las que contienen música en cualquier nivel.
     */
    fun scanWhitelistedFolders(sdcard: File): List<FolderItem> {
        val result = mutableListOf<FolderItem>()
        for (name in ROOT_WHITELIST) {
            val folder = File(sdcard, name)
            if (folder.isDirectory && containsAudio(folder)) {
                val directCount = folder.listFiles()?.count { isAudio(it) } ?: 0
                result.add(
                    FolderItem(
                        folder = folder,
                        name = folder.name,
                        path = folder.absolutePath,
                        audioCount = directCount
                    )
                )
            }
        }
        return result.sortedWith(
            compareBy(String.CASE_INSENSITIVE_ORDER) { it.name }
        )
    }

    /**
     * Devuelve los archivos de audio que están directamente dentro de [dir],
     * ordenados alfabéticamente. Las subcarpetas con música se devuelven aparte.
     */
    fun scanFilesIn(dir: File): Pair<List<AudioFile>, List<FolderItem>> {
        val children = dir.listFiles() ?: return emptyList<AudioFile>() to emptyList()

        val audios = children
            .filter { isAudio(it) }
            .map {
                AudioFile(
                    file = it,
                    name = it.name,
                    path = it.absolutePath,
                    size = it.length(),
                    lastModified = it.lastModified()
                )
            }
            .sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.name })

        val subfolders = children
            .filter {
                it.isDirectory && it.name !in IGNORED_DIRS && !it.name.startsWith(".")
            }
            .filter { containsAudio(it) }
            .map {
                FolderItem(
                    folder = it,
                    name = it.name,
                    path = it.absolutePath,
                    audioCount = it.listFiles()?.count { f -> isAudio(f) } ?: 0
                )
            }
            .sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.name })

        return audios to subfolders
    }

    /** ¿Hay audio en [dir] o en cualquiera de sus subcarpetas? */
    fun containsAudio(dir: File): Boolean {
        val children = dir.listFiles() ?: return false
        if (children.any { isAudio(it) }) return true
        return children
            .filter {
                it.isDirectory && it.name !in IGNORED_DIRS && !it.name.startsWith(".")
            }
            .any { containsAudio(it) }
    }
}
