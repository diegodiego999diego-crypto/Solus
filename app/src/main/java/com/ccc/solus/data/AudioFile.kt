package com.ccc.solus.data

import java.io.File

data class AudioFile(
    val file: File,
    val name: String,
    val path: String,
    val size: Long,
    val lastModified: Long
) {
    val extension: String
        get() = file.extension.lowercase()

    val displayName: String
        get() = file.nameWithoutExtension
}
