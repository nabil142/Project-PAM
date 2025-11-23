package com.example.projectpam.screen

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.projectpam.ui.theme.ProjectPAMTheme
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExerciseScreen(
    onSave: (name: String, durationMin: Int, timeDisplay: String) -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current

    // ---- DROPDOWN STATE ----
    val activityOptions = listOf("Running", "Walking", "Gym", "Swimming", "Cycling")
    var expanded by remember { mutableStateOf(false) }
    var activityType by remember { mutableStateOf(activityOptions.first()) }

    // ---- DURATION STATE ----
    var durationText by remember { mutableStateOf("") }

    // ---- TIME / DATE STATE ----
    val calendar = remember { Calendar.getInstance() }
    var timeText by remember {
        mutableStateOf(
            SimpleDateFormat(
                "d MMM, yyyy • HH:mm",
                Locale.getDefault()
            ).format(calendar.time)
        )
    }

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
            text = "Add exercise",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        // ========== DROPDOWN: ACTIVITY TYPE ==========
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        ) {
            TextField(
                value = activityType,
                onValueChange = { },
                readOnly = true,
                label = { Text("Activity type") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                activityOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            activityType = option
                            expanded = false
                        }
                    )
                }
            }
        }

        // ========== DURATION ==========
        OutlinedTextField(
            value = durationText,
            onValueChange = { durationText = it },
            label = { Text("Duration (minutes)") },
            placeholder = { Text("e.g. 30") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        )

        // ========== TIME / DATE ==========
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

        // ========== BUTTONS ==========
        Button(
            onClick = {
                val duration = durationText.toIntOrNull() ?: 0
                if (activityType.isNotBlank() && duration > 0 && timeText.isNotBlank()) {
                    onSave(activityType, duration, timeText)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .padding(bottom = 8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF00C853), // hijau
                contentColor = Color.White
            )
        ) {
            Text("Save")
        }
        OutlinedButton(
            onClick = onCancel,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text("Cancel")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddExerciseScreenPreview() {
    ProjectPAMTheme {
        AddExerciseScreen(
            onSave = { _, _, _ -> },
            onCancel = {}
        )
    }
}
