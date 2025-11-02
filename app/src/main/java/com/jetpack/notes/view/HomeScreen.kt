package com.jetpack.notes.view

import android.app.Application
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.jetpack.notes.model.Note
import com.jetpack.notes.ui.theme.PurpleGrey40
import com.jetpack.notes.ui.theme.lightSkyblue
import com.jetpack.notes.ui.theme.litesky
import com.jetpack.notes.ui.theme.sky1
import com.jetpack.notes.ui.theme.skyblue
import com.jetpack.notes.viewmodel.SavedNotesViewModel
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController){
    val context = LocalContext.current
    val notesViewModel : SavedNotesViewModel = viewModel()
    val notes = notesViewModel.notesList.collectAsState()
    val inputFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    val monthFormat = SimpleDateFormat("MMMM", Locale.getDefault())

    BackHandler {
        (context as? ComponentActivity)?.finish()
    }
    Scaffold (
        topBar = {
           HeaderTheme(title = "Home",
            navController = navController,
               showBackButton = false)
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.currentBackStackEntry?.savedStateHandle?.remove<Note>("selected_note")
                    navController.navigate("Notes_screen")
                },
                containerColor = skyblue,
                contentColor = PurpleGrey40
            ){
                Icon(
                    imageVector = Icons.Default.Create,
                    contentDescription = "")
            }
        }
        ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // ✅ Background Image (fills entire screen)
            Image(
                painter = painterResource(id = com.jetpack.notes.R.drawable.home_notes_bg),
                contentDescription = "Background Image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                alignment = Alignment.Center
            )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
           /* if (notes.value.isEmpty()) {
                Text(
                    text = "No notes yet. Click create icon to add Notes! ",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else {*/
            val groupedNotes = notes.value.groupBy { note ->
                try{
                    val date = inputFormat.parse(note.date)
                    monthFormat.format(date ?: Date())
                } catch (e : Exception){
                    "Unknown"
                }

            }
               /* LazyColumn {
                    items(notes.value) { note ->
                        NoteItem(note, onDeleteClick = {
                            notesViewModel.deleteNote(note)
                        },
                            onNoteClick = {
                                navController.currentBackStackEntry?.savedStateHandle?.set("selected_note", note)
                                navController.navigate("Notes_screen")
                            })

                    }
                }*/
            LazyColumn {
                groupedNotes.forEach { (month, monthNotes) ->
                    item {
                        Text(
                            text = month,
                            color = PurpleGrey40,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp)
                        )
                    }
                    items(monthNotes) {note ->
                        NoteItem(note, onDeleteClick = {
                            notesViewModel.deleteNote(note)
                        },
                            onNoteClick = {
                                navController.currentBackStackEntry?.savedStateHandle?.set("selected_note", note)
                                navController.navigate("Notes_screen")
                            })
                    }
                }
            }
//            }
        }
    }
    }

}

