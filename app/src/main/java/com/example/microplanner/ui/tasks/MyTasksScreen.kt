package com.example.microplanner.ui.tasks

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.microplanner.domain.model.Task

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTasksScreen(
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (String?) -> Unit,
    viewModel: TasksViewModel = viewModel()
) {
    val allTasks by viewModel.allTasks.collectAsState()

    // Фильтруем: показываем все активные дела + неактивные пользовательские.
    // Неактивные стандартные дела НЕ показываем.
    val visibleTasks = allTasks.filter { task ->
        task.isActive || !task.isPredefined
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Мои дела") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { onNavigateToEdit(null) }) {
                Icon(Icons.Default.Add, contentDescription = "Добавить дело")
            }
        }
    ) { paddingValues ->
        if (visibleTasks.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("Список дел пуст. Добавьте новое!")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(visibleTasks, key = { it.id }) { task ->
                    TaskItem(
                        task = task,
                        onEditClick = {
                            if (task.isPredefined) {
                                // Стандартное дело: создаём копию, затем редактируем копию
                                viewModel.editPredefinedTask(task.id) { copyId ->
                                    onNavigateToEdit(copyId)
                                }
                            } else {
                                // Пользовательское дело: редактируем напрямую
                                onNavigateToEdit(task.id)
                            }
                        },
                        onDeleteClick = { viewModel.deleteTask(task.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun TaskItem(task: Task, onEditClick: () -> Unit, onDeleteClick: () -> Unit) {
    val itemAlpha = if (task.isActive) 1.0f else 0.5f
    val textDecoration = if (task.isActive) null else TextDecoration.LineThrough

    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .alpha(itemAlpha)
            ) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.bodyLarge,
                    textDecoration = textDecoration
                )
                Text(
                    text = getPeriodicityText(task.periodicityDays),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onEditClick) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = "Редактировать",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            if (!task.isPredefined) {
                IconButton(onClick = onDeleteClick) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Удалить",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

fun getPeriodicityText(days: Int): String {
    return when (days) {
        0 -> "Без ограничений"
        1 -> "Раз в 1 день"
        2 -> "Раз в 2 дня"
        3 -> "Раз в 3 дня"
        7 -> "Раз в 7 дней"
        14 -> "Раз в 14 дней"
        30 -> "Раз в 30 дней"
        else -> "Раз в $days дней"
    }
}