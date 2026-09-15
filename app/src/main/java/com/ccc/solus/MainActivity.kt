            package com.ccc.solus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ccc.solus.ui.screens.FilesScreen
import com.ccc.solus.ui.screens.FoldersScreen
import com.ccc.solus.ui.screens.LoadingScreen
import com.ccc.solus.ui.theme.SolusTheme
import com.ccc.solus.util.Permissions
import com.ccc.solus.viewmodel.MainViewModel
import com.ccc.solus.viewmodel.UiState

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            SolusTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SolusApp(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
private fun SolusApp(viewModel: MainViewModel) {
    val context = LocalContext.current
    var hasPermission by remember { mutableStateOf(Permissions.hasAllFilesAccess()) }
    var showPermissionDialog by remember { mutableStateOf(!hasPermission) }

    LaunchedEffect(Unit) {
        hasPermission = Permissions.hasAllFilesAccess()
        if (hasPermission) {
            showPermissionDialog = false
            viewModel.rescan()
        }
    }

    if (showPermissionDialog) {
        AlertDialog(
            onDismissRequest = { /* no dismissable */ },
            title = { Text(stringResource(R.string.permission_title)) },
            text = { Text(stringResource(R.string.permission_message)) },
            confirmButton = {
                TextButton(
                    onClick = { Permissions.requestAllFilesAccess(context) }
                ) {
                    Text(stringResource(R.string.permission_button))
                }
            }
        )
        return
    }

    val state by viewModel.state.collectAsStateWithLifecycle()

    // Intercepta el gesto/botón "atrás" SOLO cuando estamos dentro de una carpeta
    BackHandler(enabled = state is UiState.FileList) {
        viewModel.goBack()
    }

    when (val s = state) {
        is UiState.Loading -> LoadingScreen()

        is UiState.Empty -> FoldersScreen(
            folders = emptyList(),
            onFolderClick = {}
        )

        is UiState.FolderList -> FoldersScreen(
            folders = s.folders,
            onFolderClick = { folder -> viewModel.openFolder(folder) }
        )

        is UiState.FileList -> FilesScreen(
            currentFolder = s.currentFolder,
            files = s.files,
            subfolders = s.subfolders,
            onFolderClick = { folder -> viewModel.openFolder(folder) },
            onBack = { viewModel.goBack() }
        )
    }
}
