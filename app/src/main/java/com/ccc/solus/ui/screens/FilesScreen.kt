package com.ccc.solus.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ccc.solus.R
import com.ccc.solus.data.AudioFile
import com.ccc.solus.data.FolderItem
import com.ccc.solus.ui.components.AudioCard
import com.ccc.solus.ui.components.FolderCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilesScreen(
    currentFolder: FolderItem,
    files: List<AudioFile>,
    subfolders: List<FolderItem>,
    onFolderClick: (FolderItem) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(currentFolder.displayName) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { innerPadding ->
        if (files.isEmpty() && subfolders.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.no_files),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(32.dp)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                // Primero las subcarpetas
                items(
                    items = subfolders,
                    key = { "folder_${it.path}" }
                ) { folder ->
                    FolderCard(
                        folder = folder,
                        onClick = { onFolderClick(folder) }
                    )
                }
                // Luego los archivos
                items(
                    items = files,
                    key = { "file_${it.path}" }
                ) { audio ->
                    AudioCard(
                        audio = audio,
                        onClick = { /* v2: reproducir */ }
                    )
                }
            }
        }
    }
}
