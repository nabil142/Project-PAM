package com.example.projectpam

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.projectpam.data.ExerciseUi
import com.example.projectpam.screen.HomeScreen
import com.example.projectpam.ui.theme.ProjectPAMTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProjectPAMTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ExerciseApp()
                }
            }
        }
    }
}

@Composable
fun ExerciseApp() {
    // state list aktivitas di sini
    val activities = remember {
        mutableStateListOf(
            ExerciseUi(1, "Running", 30, 300, "Today • 07:30"),
            ExerciseUi(2, "Walking", 20, 90, "Today • 09:15")
        )
    }

    HomeScreen(
        activities = activities,
        onAddExercise = { name, durationMin, time ->
            val newId = (activities.maxOfOrNull { it.id } ?: 0) + 1
            val newExercise = ExerciseUi(
                id = newId,
                name = name,
                durationMin = durationMin,
                calories = durationMin * 5,
                time = time
            )
            activities.add(newExercise)
        },
        onUpdateExercise = { updated ->
            val index = activities.indexOfFirst { it.id == updated.id }
            if (index != -1) {
                activities[index] = updated
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun ExerciseAppPreview() {
    ProjectPAMTheme {
        ExerciseApp()
    }
}
