package com.jetpack.notes.view

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.widget.Button
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.jetpack.notes.model.Note
import com.jetpack.notes.notification.ReminderReceiver
import com.jetpack.notes.ui.theme.PurpleGrey40
import com.jetpack.notes.ui.theme.lightSkyblue
import com.jetpack.notes.ui.theme.sky1
import com.jetpack.notes.ui.theme.skyblue
import com.jetpack.notes.viewmodel.AddNotesViewModel
import com.jetpack.notes.viewmodel.SavedNotesViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNotesScreen(
    navController: NavHostController,
    viewModel: AddNotesViewModel = viewModel(),
    existingNote: Note? = null
) {
    val text by viewModel.title.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    val description by viewModel.description.collectAsState()

    val context = LocalContext.current
    val activity = context as? android.app.Activity
    val calendar = Calendar.getInstance()
    val interactionSource = remember { MutableInteractionSource() }
    val scrollState = rememberScrollState()

    BackHandler(enabled = true) {
    }

    LaunchedEffect(Unit) {
        viewModel.initialize(existingNote)
    }

    Scaffold(
        topBar = {
            HeaderTheme(
                title = "Notes",
                navController = navController,
                showBackButton = true,
                onBellClick = {
                    val noteDateCalendar = parseDateToCalendar(selectedDate)
                    if (noteDateCalendar == null) {
                        Toast.makeText(
                            context,
                            "Please select a valid date first",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@HeaderTheme
                    }
                    val now = Calendar.getInstance()
                    TimePickerDialog(
                        context,
                        { _, hour, minute ->
                            noteDateCalendar.set(Calendar.HOUR_OF_DAY, hour)
                            noteDateCalendar.set(Calendar.MINUTE, minute)
                            noteDateCalendar.set(Calendar.SECOND, 0)

                            if (noteDateCalendar.timeInMillis <= now.timeInMillis) {
                                Toast.makeText(
                                    context,
                                    "Selected time has already Passed",
                                    Toast.LENGTH_SHORT
                                ).show()
                                return@TimePickerDialog
                            }
                            val intent = Intent(context, ReminderReceiver::class.java).apply {
                                putExtra("noteTitle", text)
                                putExtra("noteDescription", description)
                            }
                            val pendingIntent = PendingIntent.getBroadcast(
                                context,
                                System.currentTimeMillis().toInt(),
                                intent,
                                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                            )

                            val alarmManager =
                                context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

                            //  Permission check and scheduling logic
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                if (alarmManager.canScheduleExactAlarms()) {
                                    alarmManager.setExactAndAllowWhileIdle(
                                        AlarmManager.RTC_WAKEUP,
                                        noteDateCalendar.timeInMillis,
                                        pendingIntent
                                    )
                                    val timeText = String.format("%02d:%02d", hour, minute)
                                    Toast.makeText(
                                        context,
                                        "Reminder set for $selectedDate $timeText",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    // Ask user to allow exact alarm permission
                                    val permissionIntent =
                                        Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                                    context.startActivity(permissionIntent)
                                    Toast.makeText(
                                        context,
                                        "Please enable exact alarm permission",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            } else {
                                // For Android < 12
                                alarmManager.setExactAndAllowWhileIdle(
                                    AlarmManager.RTC_WAKEUP,
                                    noteDateCalendar.timeInMillis,
                                    pendingIntent
                                )
                                val timeText = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(noteDateCalendar.time)
                                Toast.makeText(
                                    context,
                                    "Reminder set for $selectedDate $timeText",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        now.get(Calendar.HOUR_OF_DAY),
                        now.get(Calendar.MINUTE),
                        true
                    ).show()
                })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = text,
                onValueChange = { viewModel.onTitleChange(it) },
                label = {
                    Text(
                        "Title of the Note",
                        color = PurpleGrey40
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 10.dp),
//                    .border(1.dp, skyblue, RoundedCornerShape(8.dp)),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = sky1,
                    unfocusedContainerColor = sky1,
                    cursorColor = skyblue,
                    focusedBorderColor = skyblue,
                    unfocusedBorderColor = skyblue
                )
            )
            /*
                        OutlinedTextField(
                            value = selectedDate,
                            onValueChange = { },
                            label = { Text("Date") },
                            readOnly = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 10.dp)
                                .clickable {
                                    val activity = context as? android.app.Activity

                                    val calendar = Calendar.getInstance()

                                    val year = calendar.get(Calendar.YEAR)
                                    val month = calendar.get(Calendar.MONTH)
                                    val day = calendar.get(Calendar.DAY_OF_MONTH)
                                    activity?.let { activity ->
                                        DatePickerDialog(
                                            activity, { _, mYear, mMonth, mDayOfMonth ->
                                                val pickedDate = "$mDayOfMonth-${mMonth + 1}-$mYear"
                                                viewModel.onDateSelected(pickedDate)
                                            }, year, month, day
                                        ).show()
                                    }
                                },
            //                    .border(1.dp, skyblue, RoundedCornerShape(8.dp))
            //                    .background(sky1, RoundedCornerShape(8.dp)),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = sky1,
                                unfocusedContainerColor = sky1,
                                cursorColor = skyblue,
                                focusedBorderColor = skyblue,
                                unfocusedBorderColor = skyblue
                            ),


                            )
            */

            LaunchedEffect(interactionSource) {
                interactionSource.interactions.collect { interaction ->
                    if (interaction is PressInteraction.Release) {
                        activity?.let { act ->
                            DatePickerDialog(
                                act,
                                { _, year, month, dayOfMonth ->
                                    val pickedDate = "$dayOfMonth-${month + 1}-$year"
                                    viewModel.onDateSelected(pickedDate)
                                },
                                calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH)
                            ).show()
                        }
                    }
                }
            }

            OutlinedTextField(
                value = selectedDate,
                onValueChange = {},
                label = { Text("Date") },
                readOnly = true,
                interactionSource = interactionSource,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 10.dp),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = sky1,
                    unfocusedContainerColor = sky1,
                    cursorColor = skyblue,
                    focusedBorderColor = skyblue,
                    unfocusedBorderColor = skyblue
                ),
            )

            OutlinedTextField(
                value = description,
                onValueChange = { viewModel.onDescription(it) },
                label = {
                    Text(
                        "Description of the Note",
                        color = PurpleGrey40
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 10.dp),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = sky1,
                    unfocusedContainerColor = sky1,
                    cursorColor = skyblue,
                    focusedBorderColor = skyblue,
                    unfocusedBorderColor = skyblue
                )
            )

            Button(
                onClick = {
                    viewModel.saveNote(context) { success, message ->
                        activity?.runOnUiThread {
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                            if (success) {
                                // Navigate only after successful save, on main thread
                                navController.navigate("Home_screen") {
                                    /*popUpTo("AddNotes_screen") {
                                        inclusive = true
                                    } // optional: remove AddNotes from backstack*/
                                }
                                if (existingNote == null) {
                                    viewModel.onTitleChange("")
                                    viewModel.onDateSelected("")
                                    viewModel.onDescription("")
                                }
                            }
                            /*  if(success){
//                                Toast.makeText(context, "Note Saved", Toast.LENGTH_LONG).show()
                                navController.navigate("Home_screen")
                                viewModel.onTitleChange("")
                                viewModel.onDateSelected("")
                                viewModel.onDescription("")
                            } else{
//                                Toast.makeText(context,"Failed to save Note", Toast.LENGTH_LONG).show()
                            }*/
                        }
                    }

                },
                modifier = Modifier
                    .padding(16.dp),
                enabled = true,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    contentColor = PurpleGrey40,
                    containerColor = sky1
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 10.dp),
                border = BorderStroke(width = 2.dp, brush = SolidColor(skyblue)),
                contentPadding = PaddingValues(
                    start = 20.dp,
                    top = 12.dp,
                    end = 20.dp,
                    bottom = 12.dp
                ),
                interactionSource = remember { MutableInteractionSource() }
            ) {
                Text(
                    text = if (existingNote != null) "Update Note" else "Save Note",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontStyle = FontStyle.Italic,
                    fontFamily = FontFamily.Serif
                )
            }

        }
    }
}


fun convertMillisToDate(millis: Long): String {
    val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
    return formatter.format(Date(millis))
}

fun parseDateToCalendar(dateString: String): Calendar? {
    return try {
        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val date = sdf.parse(dateString)
        Calendar.getInstance().apply { time = date!! }
    } catch (e: Exception) {
        null
    }
}
