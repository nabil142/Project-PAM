package com.example.projectpam.screen

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.projectpam.data.ExerciseUi
import com.example.projectpam.ui.theme.ProjectPAMTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    activities: List<ExerciseUi>,
    onAddExercise: (name: String, durationMin: Int, time: String) -> Unit,
    onUpdateExercise: (ExerciseUi) -> Unit
) {
    var showAddSheet by remember { mutableStateOf(false) }
    var showEditSheet by remember { mutableStateOf(false) }
    var selectedExercise by remember { mutableStateOf<ExerciseUi?>(null) }

    val goalCalories = 500
    val totalCalories = activities.sumOf { it.calories }

    val targetProgress = (totalCalories.toFloat() / goalCalories.toFloat())
        .coerceIn(0f, 1f)

    val animatedProgress by animateFloatAsState(
        targetValue = targetProgress,
        animationSpec = tween(durationMillis = 1200),
        label = "kcalProgress"
    )

    // ---------- Bottom Sheet: Add ----------
    if (showAddSheet) {
        ModalBottomSheet(
            onDismissRequest = { showAddSheet = false },
            dragHandle = { BottomSheetDefaults.DragHandle() }
        ) {
            AddExerciseScreen(
                onSave = { name, duration, time ->
                    onAddExercise(name, duration, time)
                    showAddSheet = false
                },
                onCancel = { showAddSheet = false }
            )
        }
    }

    // ---------- Bottom Sheet: Edit ----------
    if (showEditSheet && selectedExercise != null) {
        ModalBottomSheet(
            onDismissRequest = {
                showEditSheet = false
                selectedExercise = null
            },
            dragHandle = { BottomSheetDefaults.DragHandle() }
        ) {
            EditExerciseScreen(
                exercise = selectedExercise!!,
                onSave = { newName, newDuration, newTime ->
                    val updated = selectedExercise!!.copy(
                        name = newName,
                        durationMin = newDuration,
                        calories = newDuration * 5,
                        time = newTime
                    )
                    onUpdateExercise(updated)
                    showEditSheet = false
                    selectedExercise = null
                },
                onCancel = {
                    showEditSheet = false
                    selectedExercise = null
                }
            )
        }
    }

    // ---------- Main Content ----------
    Scaffold(
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Button(
                    onClick = { showAddSheet = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF00C853),
                        contentColor = Color.White
                    )
                ) {
                    Text(text = "Add exercise")
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header tanggal
            Text(
                text = "2 May, Monday",
                style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Donut kalori dengan animasi
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier.size(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val strokeWidth = 18.dp.toPx()
                        val diameter = size.minDimension - strokeWidth
                        val topLeft = Offset(
                            (size.width - diameter) / 2f,
                            (size.height - diameter) / 2f
                        )

                        drawArc(
                            color = Color(0xFF00C853),
                            startAngle = -90f,
                            sweepAngle = 360f * animatedProgress,
                            useCenter = false,
                            style = Stroke(
                                width = strokeWidth,
                                cap = StrokeCap.Round
                            ),
                            topLeft = topLeft,
                            size = Size(diameter, diameter)
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = totalCalories.toString(),
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32)
                        )
                        Text(
                            text = "kcal burned today",
                            fontSize = 14.sp,
                            color = Color(0xFF8D6E63)
                        )
                    }
                }
            }

            Text(
                text = "Goal: $goalCalories kcal",
                fontSize = 12.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 12.dp)
            )

            // Daily exercise title
            Text(
                text = "Daily exercise",
                style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "Log your activities and track calories burned",
                style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // List aktivitas
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(activities) { activity ->
                    ExerciseCard(
                        activity = activity,
                        onEditClick = {
                            selectedExercise = activity
                            showEditSheet = true
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ExerciseCard(
    activity: ExerciseUi,
    onEditClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF5F5F5)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF00C853)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = activity.name.firstOrNull()?.toString() ?: "",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
            ) {
                Text(
                    text = activity.name,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "${activity.durationMin} min • ${activity.calories} kcal burned",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Text(
                    text = activity.time,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            Text(
                text = "Edit",
                color = Color(0xFF2962FF),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable { onEditClick() }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    val dummy = listOf(
        ExerciseUi(1, "Running", 30, 300, "Today • 07:30"),
        ExerciseUi(2, "Walking", 20, 90, "Today • 09:15")
    )
    ProjectPAMTheme {
        HomeScreen(
            activities = dummy,
            onAddExercise = { _, _, _ -> },
            onUpdateExercise = {}
        )
    }
}
