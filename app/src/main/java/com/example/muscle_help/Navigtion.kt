
package com.example.muscle_help

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.example.muscle_help.screens.GeneratorScreen
import com.example.muscle_help.screens.MusclesScreen
import com.example.muscle_help.screens.PPLScreen

// Les trois destinations
data object DestinationMuscle
data object DestinationProgrammePPL
data object DestinationCreationSeance

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Navigation(
    modifier: Modifier = Modifier
) {
    // Liste des écrans de navigation
    val backStack = remember {
        mutableStateListOf<Any>(DestinationMuscle)
    }

    // Écran actuellement affiché
    val currentDestination = backStack.lastOrNull()

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

                // Bouton Muscles
                NavigationBarItem(
                    selected = currentDestination == DestinationMuscle,
                    onClick = {
                        backStack.clear()
                        backStack.add(DestinationMuscle)
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

                // Bouton PPL
                NavigationBarItem(
                    selected = currentDestination == DestinationProgrammePPL,
                    onClick = {
                        backStack.clear()
                        backStack.add(DestinationProgrammePPL)
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

                // Bouton Générateur
                NavigationBarItem(
                    selected = currentDestination == DestinationCreationSeance,
                    onClick = {
                        backStack.clear()
                        backStack.add(DestinationCreationSeance)
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

                entry<DestinationMuscle> {
                    MusclesScreen()
                }

                entry<DestinationProgrammePPL> {
                    PPLScreen()
                }

                entry<DestinationCreationSeance> {
                    GeneratorScreen()
                }
            }
        )
    }
}
