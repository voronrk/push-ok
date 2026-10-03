package com.example.microplanner.ui.home

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.microplanner.data.AppStats
import com.example.microplanner.domain.model.DurationType
import com.example.microplanner.domain.model.Task

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onNavigateToMyTasks: () -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("MicroPlanner", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = onNavigateToMyTasks) {
                        Icon(Icons.Default.List, contentDescription = "Мои дела")
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Настройки")
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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Что делать?",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            when (val state = uiState.taskDisplayState) {
                is TaskDisplayState.Idle -> {
                    MainButtons(viewModel)
                    Spacer(modifier = Modifier.height(32.dp))
                    StatsBlock(uiState.stats, uiState.abGroup, context)
                }
                is TaskDisplayState.ReadyToStart -> {
                    TaskCardReady(state.task, viewModel)
                }
                is TaskDisplayState.InProgress -> {
                    TaskCardInProgress(state.task, viewModel)
                }
                is TaskDisplayState.Completed -> {
                    TaskCardCompleted(state.minutesSpent, viewModel, context, uiState.abGroup)
                }
            }
        }
    }

    // Диалог пропуска для дел "без ограничений"
    if (uiState.showSkipDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissSkipDialog() },
            title = { Text("Пропустить дело?") },
            text = { Text("Как вы хотите поступить с этим делом?") },
            confirmButton = {
                TextButton(onClick = { viewModel.skipUntilTomorrow() }) {
                    Text("Не предлагать до завтра")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.skipNow() }) {
                    Text("Пропустить сейчас")
                }
            }
        )
    }
}

@Composable
fun MainButtons(viewModel: HomeViewModel) {
    Button(
        onClick = { viewModel.getRandomTask(DurationType.SHORT) },
        modifier = Modifier.fillMaxWidth().height(64.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Быстрое дело", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text("до 15 минут", fontSize = 12.sp)
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    Button(
        onClick = { viewModel.getRandomTask(DurationType.MEDIUM) },
        modifier = Modifier.fillMaxWidth().height(64.dp),
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Среднее дело", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text("до 30 минут", fontSize = 12.sp)
        }
    }
}

@Composable
fun StatsBlock(stats: AppStats, abGroup: String, context: Context) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Выполнено: ${stats.totalCompleted}", fontSize = 16.sp)
            Text("Пропущено: ${stats.totalSkipped}", fontSize = 16.sp)
            Text("Серия: ${stats.currentStreak}", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text(
                "Отнято у безделья: ${formatMinutes(stats.totalTimeReclaimedMinutes)}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            if (abGroup == "B") {
                Spacer(modifier = Modifier.height(16.dp))
                DonationLink(context, "https://example.com/donate-in-stats")
            }
        }
    }
}

@Composable
fun TaskCardReady(task: Task, viewModel: HomeViewModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                task.title,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { viewModel.startTask() },
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text("Начать", fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.height(12.dp))

            TextButton(
                onClick = { viewModel.onSkipClicked() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Пропустить")
            }
            TextButton(
                onClick = { viewModel.markAsIrrelevant() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Неактуально", color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
fun TaskCardInProgress(task: Task, viewModel: HomeViewModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                task.title,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { viewModel.completeTask() },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Сделано", fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = { viewModel.notDoneTask() },
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text("Не сделано", fontSize = 18.sp, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
fun TaskCardCompleted(
    minutesSpent: Int,
    viewModel: HomeViewModel,
    context: Context,
    abGroup: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Отличная работа!",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text("Затрачено: $minutesSpent мин.", fontSize = 18.sp)

            if (abGroup == "A") {
                Spacer(modifier = Modifier.height(24.dp))
                DonationLink(context, "https://example.com/donate-after-completion")
            }

            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = { viewModel.closeTaskCard() },
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text("Закрыть", fontSize = 18.sp)
            }
        }
    }
}

@Composable
fun DonationLink(context: Context, url: String) {
    TextButton(onClick = { openUrl(context, url) }) {
        Text("❤️ Понравилось? Поддержи разработчика", fontSize = 14.sp)
    }
}

fun openUrl(context: Context, url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    context.startActivity(intent)
}

fun formatMinutes(totalMinutes: Int): String {
    val hours = totalMinutes / 60
    val minutes = totalMinutes % 60
    return if (hours > 0) "$hours ч. $minutes мин." else "$minutes мин."
}