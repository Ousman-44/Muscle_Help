
package com.example.muscle_help.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.muscle_help.model.Exercice

@Composable
fun PPLScreen(
    exercices: List<Exercice> = emptyList(),
    isLoading: Boolean = false,
    error: String? = null,
    onProgramSelected: (String) -> Unit = {}
) {
    var selectedProgram by remember {
        mutableStateOf("Push")
    }

    val programs = listOf("Push", "Pull", "Legs")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Programmes PPL",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(Modifier.height(8.dp))

        Text("Choisis un programme d'entraînement.")

        Spacer(Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            programs.forEach { program ->
                FilterChip(
                    selected = selectedProgram == program,
                    onClick = {
                        selectedProgram = program
                        onProgramSelected(program)
                    },
                    label = { Text(program) }
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        val description = when (selectedProgram) {
            "Push" -> "Pectoraux, épaules et triceps"
            "Pull" -> "Dos et biceps"
            else -> "Quadriceps, ischios et fessiers"
        }

        ElevatedCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(16.dp)) {
                Text(
                    selectedProgram,
                    style = MaterialTheme.typography.titleLarge
                )

                Text(description)

                Spacer(Modifier.height(8.dp))

                Text("3 séries de 8 à 12 répétitions par exercice")
            }
        }

        Spacer(Modifier.height(16.dp))

        Text(
            "Exercices de la séance",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(Modifier.height(8.dp))

        when {
            isLoading -> CircularProgressIndicator()

            error != null -> Text("Erreur : $error")

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
