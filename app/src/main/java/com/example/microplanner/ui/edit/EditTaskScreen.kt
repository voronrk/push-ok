package com.example.microplanner.ui.edit

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.microplanner.domain.model.DurationType
import com.example.microplanner.ui.tasks.TasksViewModel
import com.example.microplanner.ui.tasks.getPeriodicityText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTaskScreen(
    taskId: String?,
    onNavigateBack: () -> Unit,
    viewModel: TasksViewModel = viewModel()
) {
    // Если taskId есть, мы в режиме редактирования. 
    // Для простоты MVP мы найдем дело по ID асинхронно, но здесь используем упрощенный подход:
    // передаем начальные значения, если это редактирование (в реальном приложении лучше загружать через ViewModel)
    // Чтобы не усложнять, сделаем загрузку внутри Composable через LaunchedEffect или просто передадим данные.
    // Упрощение: мы будем хранить состояние локально, а при сохранении вызывать viewModel.saveTask
    
    var title by remember { mutableStateOf("") }
    var selectedDuration by remember { mutableStateOf<DurationType?>(null) }
    var selectedPeriodicity by remember { mutableStateOf<Int?>(null) }
    var isLoaded by remember { mutableStateOf(false) }

    // Загрузка данных для редактирования
    LaunchedEffect(taskId) {
        if (taskId != null) {
            val task = viewModel.allTasks.value.find { it.id == taskId }
            if (task != null) {
                title = task.title
                selectedDuration = task.durationType
                selectedPeriodicity = task.periodicityDays
            }
        }
        isLoaded = true
    }

    val isFormValid = title.isNotBlank() && selectedDuration != null && selectedPeriodicity != null

    if (!isLoaded) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (taskId == null) "Новое дело" else "Редактировать") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            if (isFormValid) {
                                viewModel.saveTask(taskId, title, selectedDuration!!, selectedPeriodicity!!)
                                onNavigateBack()
                            }
                        },
                        enabled = isFormValid
                    ) {
                        Icon(Icons.Default.Check, contentDescription = "Сохранить")
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
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Название
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Название дела *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Длительность
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Длительность *", style = MaterialTheme.typography.titleMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = selectedDuration == DurationType.SHORT,
                        onClick = { selectedDuration = DurationType.SHORT },
                        label = { Text("Быстрое (до 15 мин)") }
                    )
                    FilterChip(
                        selected = selectedDuration == DurationType.MEDIUM,
                        onClick = { selectedDuration = DurationType.MEDIUM },
                        label = { Text("Среднее (до 30 мин)") }
                    )
                }
            }

            // Периодичность
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Периодичность *", style = MaterialTheme.typography.titleMedium)
                val periodicityOptions = listOf(0, 1, 2, 3, 7, 14, 30)
                periodicityOptions.forEach { days ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedPeriodicity == days,
                            onClick = { selectedPeriodicity = days }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(getPeriodicityText(days))
                    }
                }
            }
        }
    }
}