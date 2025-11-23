package com.example.projectpam

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.projectpam.data.ExerciseViewModel
import com.example.projectpam.screen.HomeScreen
import com.example.projectpam.ui.theme.ProjectPAMTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ProjectPAMTheme {
                ExerciseApp()
            }
        }
    }
}
@Composable
fun ExerciseApp() {
    val viewModel: ExerciseViewModel = viewModel()
    val state = viewModel.uiState.value

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        HomeScreen(
            activities = state.exercises,
            onAddExercise = { name, durationMin, time ->
                viewModel.addExercise(name, durationMin, time)
            },
            onUpdateExercise = { updated ->
                viewModel.updateExercise(exercise = updated)
            },
            onDeleteExercise = { deleted ->
                viewModel.deleteExercise(id = deleted.id)
            }
        )
    }
}


