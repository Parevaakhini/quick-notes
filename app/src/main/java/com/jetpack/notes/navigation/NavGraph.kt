package com.jetpack.notes.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.jetpack.notes.model.Note
import com.jetpack.notes.view.AddNotesScreen
import com.jetpack.notes.view.HomeScreen

@Composable
fun NavGraph(){
     val navController = rememberNavController()
     NavHost(navController = navController, startDestination = "Home_screen")
     {
       composable(route = "Home_screen"){
            HomeScreen(navController)
       }
          composable(route = "Notes_screen"){ backStackEntry ->
              val note = navController.previousBackStackEntry
                  ?.savedStateHandle
                  ?.get<Note>("selected_note")
               AddNotesScreen(
                  navController = navController,
                   existingNote = note)
          }
     }
}