package com.ccc.solus.data

import java.io.File

data class FolderItem(
    val folder: File,
    val name: String,
    val path: String,
    val audioCount: Int
) {
    val displayName: String
        get() = name.ifBlank { folder.absolutePath }
}
