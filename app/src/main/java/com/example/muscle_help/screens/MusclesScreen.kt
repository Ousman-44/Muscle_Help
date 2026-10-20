
package com.example.muscle_help.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import com.example.muscle_help.model.Exercice

val muscles = linkedMapOf(
    "pectorals" to "Pectoraux",
    "biceps" to "Biceps",
    "triceps" to "Triceps",
    "lats" to "Dos",
    "delts" to "Épaules",
    "quads" to "Quadriceps",
    "hamstrings" to "Ischio-jambiers",
    "glutes" to "Fessiers",
    "abs" to "Abdominaux"
)

@Composable
fun MusclesScreen(
    exercices: List<Exercice> = emptyList(),
    isLoading: Boolean = false,
    error: String? = null,
    onMuscleSelected: (String) -> Unit = {}
) {
    var selectedMuscle by remember {
        mutableStateOf("pectorals")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Choisis ton muscle",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(Modifier.height(8.dp))

        Text("Sélectionne un muscle pour voir les exercices.")

        Spacer(Modifier.height(16.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(muscles.toList()) { muscle ->
                FilterChip(
                    selected = selectedMuscle == muscle.first,
                    onClick = {
                        selectedMuscle = muscle.first
                        onMuscleSelected(muscle.first)
                    },
                    label = {
                        Text(muscle.second)
                    }
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = "Exercices : ${muscles[selectedMuscle]}",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(Modifier.height(12.dp))

        when {
            isLoading -> {
                CircularProgressIndicator()
            }

            error != null -> {
                Text("Erreur : $error")
            }

            exercices.isEmpty() -> {
                Text("Aucun exercice disponible.")
            }

            else -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(exercices, key = { it.id }) { exercice ->
                        CarteExercice(exercice)
                    }
                }
            }
        }
    }
}

@Composable
fun CarteExercice(exercice: Exercice) {
    var expanded by remember {
        mutableStateOf(false)
    }

    val uriHandler = LocalUriHandler.current

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                text = exercice.nom,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.height(6.dp))

            Text(
                "Matériel : ${exercice.equipements.joinToString()}"
            )

            if (expanded) {
                Spacer(Modifier.height(12.dp))

                Text(
                    "Instructions",
                    style = MaterialTheme.typography.titleSmall
                )

                exercice.instructions.forEachIndexed { index, instruction ->
                    Text("${index + 1}. $instruction")
                    Spacer(Modifier.height(6.dp))
                }

                if (exercice.gifUrl.isNotBlank()) {
                    TextButton(
                        onClick = {
                            uriHandler.openUri(exercice.gifUrl)
                        }
                    ) {
                        Text("Voir le GIF")
                    }
                }
            } else {
                Text("Appuie pour voir les détails")
            }
        }
    }
}
