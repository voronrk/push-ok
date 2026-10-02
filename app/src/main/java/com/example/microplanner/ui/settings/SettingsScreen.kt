package com.example.microplanner.ui.settings

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.microplanner.data.AppDatabase
import com.example.microplanner.data.TaskRepository
import kotlinx.coroutines.launch // <-- ВОТ ЭТОГО ИМПОРТА НЕ ХВАТАЛО

class SettingsViewModel(application: Application) : ViewModel() {
    private val taskRepository = TaskRepository(AppDatabase.getDatabase(application).taskDao())

    fun restorePredefinedTasks() {
        viewModelScope.launch {
            taskRepository.restorePredefinedTasks()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = viewModel(factory = SettingsViewModelFactory(LocalContext.current.applicationContext as Application))
) {
    val context = LocalContext.current
    var showRestoreDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Настройки") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SettingsItem(
                title = "Поддержать разработчика",
                onClick = { openUrl(context, "https://example.com/donate-settings") }
            )
            
            SettingsItem(
                title = "Восстановить стандартные дела",
                onClick = { showRestoreDialog = true }
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            Text("О приложении", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 8.dp))
            Text("MicroPlanner v1.0.0", style = MaterialTheme.typography.bodyMedium)
            Text("Сделано для борьбы с прокрастинацией", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }

    if (showRestoreDialog) {
        AlertDialog(
            onDismissRequest = { showRestoreDialog = false },
            title = { Text("Восстановить дела?") },
            text = { Text("Все стандартные дела, которые вы скрыли, снова станут активными. Ваши личные дела не изменятся.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.restorePredefinedTasks()
                        showRestoreDialog = false
                    }
                ) {
                    Text("Восстановить")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRestoreDialog = false }) {
                    Text("Отмена")
                }
            }
        )
    }
}

@Composable
fun SettingsItem(title: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Text(
            text = title,
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

fun openUrl(context: Context, url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    context.startActivity(intent)
}

class SettingsViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}