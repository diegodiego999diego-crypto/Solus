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

    fun isAudio(file: File): Boolean {
        return file.isFile && file.extension.lowercase() in AUDIO_EXTENSIONS
    }

    /**
     * Devuelve la lista de carpetas (en cualquier nivel a partir de [root])
     * que contienen al menos un archivo de audio, directa o indirectamente.
     * El orden es alfabético natural, carpetas primero.
     */
    fun scanFolders(root: File): List<FolderItem> {
        val result = mutableListOf<FolderItem>()
        walkFolders(root, result)
        return result.sortedWith(
            compareBy(String.CASE_INSENSITIVE_ORDER) { it.name }
        )
    }

    private fun walkFolders(dir: File, out: MutableList<FolderItem>) {
        val children = dir.listFiles() ?: return

        // Filtrar directorios ignorados
        val subdirs = children.filter {
            it.isDirectory && it.name !in IGNORED_DIRS && !it.name.startsWith(".")
        }

        // ¿Hay audios directos en esta carpeta?
        val directAudios = children.count { isAudio(it) }

        // Recorrer subcarpetas primero
        var hasAudioBelow = false
        for (sub in subdirs) {
            val before = out.size
            walkFolders(sub, out)
            if (out.size > before) hasAudioBelow = true
        }

        if (directAudios > 0 || hasAudioBelow) {
            out.add(
                FolderItem(
                    folder = dir,
                    name = dir.name,
                    path = dir.absolutePath,
                    audioCount = directAudios
                )
            )
        }
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
