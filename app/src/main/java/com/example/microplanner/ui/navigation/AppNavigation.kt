package com.example.microplanner.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.microplanner.ui.edit.EditTaskScreen
import com.example.microplanner.ui.home.HomeScreen
import com.example.microplanner.ui.settings.SettingsScreen
import com.example.microplanner.ui.tasks.MyTasksScreen

object Routes {
    const val HOME = "home"
    const val MY_TASKS = "my_tasks"
    const val EDIT_TASK = "edit_task/{taskId}"
    const val SETTINGS = "settings"
}

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Routes.HOME) {
        composable(Routes.HOME) {
            HomeScreen(
                onNavigateToMyTasks = { navController.navigate(Routes.MY_TASKS) },
                onNavigateToSettings = { navController.navigate(Routes.SETTINGS) }
            )
        }
        
        composable(Routes.MY_TASKS) {
            MyTasksScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEdit = { taskId -> 
                    navController.navigate("edit_task/$taskId") 
                }
            )
        }
        
        composable(
            route = Routes.EDIT_TASK,
            arguments = listOf(navArgument("taskId") { 
                type = NavType.StringType
                nullable = true 
            })
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId")
            EditTaskScreen(
                taskId = taskId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.SETTINGS) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}