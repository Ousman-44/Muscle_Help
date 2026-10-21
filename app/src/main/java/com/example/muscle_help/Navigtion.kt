
package com.example.muscle_help

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay

import com.example.muscle_help.data.AppContainer
import com.example.muscle_help.screens.MusclesScreen
import com.example.muscle_help.screens.PPLScreen
import com.example.muscle_help.screens.GeneratorScreen

import com.example.muscle_help.viewmodel.MusclesViewModel
import com.example.muscle_help.viewmodel.PPLViewModel
import com.example.muscle_help.viewmodel.GeneratorViewModel

data object DestinationMuscle
data object DestinationProgrammePPL
data object DestinationCreationSeance

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Navigation(
    modifier: Modifier = Modifier
) {

    // Historique de navigation
    val backStack = remember {
        mutableStateListOf<Any>(DestinationMuscle)
    }

    val destinationActuelle = backStack.lastOrNull()

    // Factory permettant de créer les ViewModels
    val factory = AppContainer.viewModelFactory

    Scaffold(
        modifier = modifier.fillMaxSize(),

        topBar = {
            TopAppBar(
                title = {
                    Text("Muscle Help")
                }
            )
        },

        bottomBar = {
            NavigationBar {

                // Onglet Muscles
                NavigationBarItem(
                    selected = destinationActuelle == DestinationMuscle,
                    onClick = {
                        if (destinationActuelle != DestinationMuscle) {
                            backStack.clear()
                            backStack.add(DestinationMuscle)
                        }
                    },
                    icon = {
                        Icon(
                            painter = painterResource(R.drawable.poids),
                            contentDescription = "Muscles"
                        )
                    },
                    label = {
                        Text("Muscles")
                    }
                )

                // Onglet PPL
                NavigationBarItem(
                    selected = destinationActuelle == DestinationProgrammePPL,
                    onClick = {
                        if (destinationActuelle != DestinationProgrammePPL) {
                            backStack.clear()
                            backStack.add(DestinationProgrammePPL)
                        }
                    },
                    icon = {
                        Icon(
                            painter = painterResource(R.drawable.liste),
                            contentDescription = "PPL"
                        )
                    },
                    label = {
                        Text("PPL")
                    }
                )

                // Onglet Générateur
                NavigationBarItem(
                    selected = destinationActuelle == DestinationCreationSeance,
                    onClick = {
                        if (destinationActuelle != DestinationCreationSeance) {
                            backStack.clear()
                            backStack.add(DestinationCreationSeance)
                        }
                    },
                    icon = {
                        Icon(
                            painter = painterResource(R.drawable.stylo),
                            contentDescription = "Générateur"
                        )
                    },
                    label = {
                        Text("Générateur")
                    }
                )
            }
        }

    ) { innerPadding ->

        NavDisplay(
            backStack = backStack,
            modifier = Modifier.padding(innerPadding),

            entryProvider = entryProvider {

                // Écran Muscles
                entry<DestinationMuscle> {

                    val vueModele: MusclesViewModel = viewModel(
                        factory = factory
                    )

                    val etat by vueModele.etat
                        .collectAsStateWithLifecycle()

                    MusclesScreen(
                        exercices = etat.exercices,
                        isLoading = etat.chargement,
                        error = etat.erreur,
                        onMuscleSelected = vueModele::recupererExercices
                    )
                }

                // Écran PPL
                entry<DestinationProgrammePPL> {

                    val vueModele: PPLViewModel = viewModel(
                        factory = factory
                    )

                    val etat by vueModele.etat
                        .collectAsStateWithLifecycle()

                    PPLScreen(
                        exercices = etat.exercices,
                        isLoading = etat.chargement,
                        error = etat.erreur,
                        onProgramSelected = vueModele::recupererProgramme
                    )
                }

                // Écran Générateur
                entry<DestinationCreationSeance> {

                    val vueModele: GeneratorViewModel = viewModel(
                        factory = factory
                    )

                    val etat by vueModele.etat
                        .collectAsStateWithLifecycle()

                    GeneratorScreen(
                        exercices = etat.exercices,
                        chargement = etat.chargement,
                        erreur = etat.erreur,
                        onGenererSeance = vueModele::genererSeance
                    )
                }
            }
        )
    }
}
