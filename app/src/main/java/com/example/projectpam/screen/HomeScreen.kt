package com.example.projectpam.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.projectpam.data.ExerciseUi
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.animateColorAsState


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    activities: List<ExerciseUi>,
    onAddExercise: (name: String, durationMin: Int, timeDisplay: String) -> Unit,
    onUpdateExercise: (ExerciseUi) -> Unit,
    onDeleteExercise: (ExerciseUi) -> Unit,
) {
    var showAddSheet by remember { mutableStateOf(false) }
    var showEditSheet by remember { mutableStateOf(false) }
    var exerciseToEdit by remember { mutableStateOf<ExerciseUi?>(null) }

    val totalKcal = activities.sumOf { it.calories }
    val goalKcal = 500
    val progress = (totalKcal.toFloat() / goalKcal.coerceAtLeast(1)).coerceIn(0f, 1f)

    val todayText = remember {
        val today = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("d MMM, EEEE", Locale.ENGLISH)
        today.format(formatter)
    }

    Box(modifier = Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 32.dp)
        ) {
            Text(
                text = todayText,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            KcalRing(
                current = totalKcal,
                goal = goalKcal,
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Daily exercise",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Log your activities and track calories burned",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF6B7280)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ====== LIST AKTIVITAS (YANG SCROLL) ======
            if (activities.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f) // biar ngisi ruang tengah
                        .fillMaxWidth()
                        .background(
                            color = Color(0xFFF3F4F6),
                            shape = RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Belum ada aktivitas.\nTap \"Add exercise\" untuk menambah.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF9CA3AF)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(activities) { exercise ->
                        ExerciseCard(
                            exercise = exercise,
                            onEdit = {
                                exerciseToEdit = exercise
                                showEditSheet = true
                            },
                            onDelete = { onDeleteExercise(exercise) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ====== BUTTON ADD EXERCISE ======
            Button(
                onClick = { showAddSheet = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF22C55E),
                    contentColor = Color.White
                )
            ) {
                Text("Add exercise")
            }
        }

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

        if (showEditSheet && exerciseToEdit != null) {
            ModalBottomSheet(
                onDismissRequest = {
                    showEditSheet = false
                    exerciseToEdit = null
                },
                dragHandle = { BottomSheetDefaults.DragHandle() }
            ) {
                EditExerciseScreen(
                    exercise = exerciseToEdit!!,
                    onSave = { updated ->
                        onUpdateExercise(updated)
                        showEditSheet = false
                        exerciseToEdit = null
                    },
                    onCancel = {
                        showEditSheet = false
                        exerciseToEdit = null
                    }
                )
            }
        }
    }
}


@Composable
private fun KcalRing(
    current: Int,
    goal: Int,
    progress: Float,
    modifier: Modifier = Modifier
) {
    val orange = Color(0xFFFFA935)
    val green = Color(0xFF22C55E)

    // animasi untuk progress lingkaran
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        label = "kcalProgress"
    )

    // kalau sudah memenuhi goal, warna jadi hijau
    val targetColor = if (current >= goal) green else orange
    val ringColor by animateColorAsState(
        targetValue = targetColor,
        label = "ringColor"
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier.size(220.dp)
        ) {
            val strokeWidth = 18.dp.toPx()
            val radius = size.minDimension / 2f - strokeWidth / 2f

            // track abu-abu
            drawCircle(
                color = Color(0xFFE5E7EB),
                style = Stroke(width = strokeWidth)
            )

            // progress dengan animasi + warna dinamis
            drawArc(
                color = ringColor,
                startAngle = -90f,
                sweepAngle = 360f * animatedProgress,
                useCenter = false,
                size = Size(radius * 2, radius * 2),
                topLeft = androidx.compose.ui.geometry.Offset(
                    (size.width - radius * 2) / 2f,
                    (size.height - radius * 2) / 2f
                ),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = current.toString(),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = ringColor
            )
            Text(
                text = "kcal burned today",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF4B5563)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Goal: $goal kcal",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF9CA3AF)
            )
        }
    }
}



@Composable
private fun ExerciseCard(
    exercise: ExerciseUi,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF3F4F6)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // bulatan huruf depan
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = Color(0xFF22C55E),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = exercise.name.firstOrNull()?.uppercase() ?: "-",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = exercise.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "${exercise.durationMin} min • ${exercise.calories} kcal burned",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF6B7280)
                )
                Text(
                    text = exercise.time,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF9CA3AF)
                )
            }

            IconButton(onClick = onEdit) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = "Edit",
                    tint = Color(0xFF4B5563)
                )
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Delete",
                    tint = Color(0xFFEF4444)
                )
            }
        }
    }
}
