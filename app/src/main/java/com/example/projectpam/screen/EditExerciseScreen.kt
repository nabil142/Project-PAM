package com.example.projectpam.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.projectpam.data.ExerciseUi
import com.example.projectpam.ui.theme.ProjectPAMTheme

// samakan dengan warna hijau di AddExercise
private val AppGreen = Color(0xFF00C853)

@Composable
fun EditExerciseScreen(
    exercise: ExerciseUi,
    onSave: (ExerciseUi) -> Unit,
    onCancel: () -> Unit
) {
    var activityType by remember { mutableStateOf(exercise.name) }
    var durationText by remember { mutableStateOf(exercise.durationMin.toString()) }
    var timeText by remember { mutableStateOf(exercise.time) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Text(
            text = "Edit exercise",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        val textFieldColors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = AppGreen,
            focusedLabelColor = AppGreen,
            cursorColor = AppGreen
        )

        OutlinedTextField(
            value = activityType,
            onValueChange = { activityType = it },
            label = { Text("Activity type") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            colors = textFieldColors
        )

        OutlinedTextField(
            value = durationText,
            onValueChange = { durationText = it },
            label = { Text("Duration (minutes)") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            colors = textFieldColors
        )

        OutlinedTextField(
            value = timeText,
            onValueChange = { timeText = it },
            label = { Text("Time / Date") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            colors = textFieldColors
        )

        Button(
            onClick = {
                val duration = durationText.toIntOrNull() ?: 0
                if (activityType.isNotBlank() && duration > 0) {
                    val updated = exercise.copy(
                        name = activityType,
                        durationMin = duration,
                        time = timeText
                    )
                    onSave(updated)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AppGreen,
                contentColor = Color.White
            )
        ) {
            Text("Save")
        }

        OutlinedButton(
            onClick = onCancel,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = AppGreen
            ),
            border = ButtonDefaults.outlinedButtonBorder.copy(
                width = 1.dp,
                brush = androidx.compose.ui.graphics.SolidColor(AppGreen)
            )
        ) {
            Text("Cancel")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EditExerciseScreenPreview() {
    ProjectPAMTheme {
        EditExerciseScreen(
            exercise = ExerciseUi(
                id = 1,
                name = "Running",
                durationMin = 30,
                calories = 300,
                time = "Today • 07:30"
            ),
            onSave = {},
            onCancel = {}
        )
    }
}
