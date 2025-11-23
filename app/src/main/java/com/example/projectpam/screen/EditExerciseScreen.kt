package com.example.projectpam.screen

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import com.example.projectpam.data.ExerciseUi
import com.example.projectpam.ui.theme.ProjectPAMTheme
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import androidx.compose.foundation.clickable

@Composable
fun EditExerciseScreen(
    exercise: ExerciseUi,
    onSave: (name: String, durationMin: Int, timeDisplay: String) -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current

    var name by remember { mutableStateOf(exercise.name) }
    var durationText by remember { mutableStateOf(exercise.durationMin.toString()) }

    // calendar untuk picker; kita mulai dari sekarang saja
    val calendar = remember { Calendar.getInstance() }

    // default isi time: pakai yang sudah ada di exercise
    var timeText by remember { mutableStateOf(exercise.time) }

    // TimePickerDialog
    val timePickerDialog = remember {
        TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)

                timeText = SimpleDateFormat(
                    "d MMM, yyyy • HH:mm",
                    Locale.getDefault()
                ).format(calendar.time)
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        )
    }

    // DatePickerDialog → setelah pilih tanggal lanjut pilih jam
    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                timePickerDialog.show()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .navigationBarsPadding()
            .imePadding()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Edit exercise",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Activity type") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        )

        OutlinedTextField(
            value = durationText,
            onValueChange = { durationText = it },
            label = { Text("Duration (minutes)") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        )

        OutlinedTextField(
            value = timeText,
            onValueChange = { },
            readOnly = true,
            label = { Text("Time / Date") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
                .clickable { datePickerDialog.show() }
        )

        Button(
            onClick = {
                val duration = durationText.toIntOrNull() ?: 0
                if (name.isNotBlank() && duration > 0 && timeText.isNotBlank()) {
                    onSave(name, duration, timeText)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .padding(bottom = 12.dp),
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF00C853),
                contentColor = Color.White
            )
        ) {
            Text("Save")
        }

        OutlinedButton(
            onClick = onCancel,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, Color(0xFFB0BEC5)),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color(0xFF607D8B)
            )
        ) {
            Text("Cancel")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EditExerciseScreenPreview() {
    val dummy = ExerciseUi(
        id = 1,
        name = "Running",
        durationMin = 30,
        calories = 300,
        time = "23 Nov, 2025 • 06:44"
    )
    ProjectPAMTheme {
        EditExerciseScreen(
            exercise = dummy,
            onSave = { _, _, _ -> },
            onCancel = {}
        )
    }
}
