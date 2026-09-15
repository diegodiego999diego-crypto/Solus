package com.ccc.solus.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
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
            val totalCount = subfolders.size + files.size

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        var index = 0

                        subfolders.forEach { folder ->
                            FolderCard(
                                folder = folder,
                                onClick = { onFolderClick(folder) }
                            )
                            if (index < totalCount - 1) SettingsDivider()
                            index++
                        }

                        files.forEach { audio ->
                            AudioCard(
                                audio = audio,
                                onClick = { /* v2 */ }
                            )
                            if (index < totalCount - 1) SettingsDivider()
                            index++
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0f),
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.25f),
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.25f),
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0f)
                    )
                )
            )
    )
}
