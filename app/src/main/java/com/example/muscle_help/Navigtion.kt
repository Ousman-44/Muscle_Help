package com.example.muscle_help

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay


class DestinationMuscle
class DestinationProgrammePPL
class DestinationCreationSeance

class Navigtion(){

    @Composable
    fun AppNavigation() {
        val backStack = remember {
            mutableStateListOf<Any>(DestinationMuscle())
        }
        NavDisplay(
            backStack = backStack,
            entryProvider = entryProvider{
                entry<DestinationMuscle>{
                    MuscleScreen()
                }
                entry<DestinationProgrammePPL>{
                    ProgrammePPLScreen()
                }
                entry<DestinationCreationSeance>{
                    CreationSeanceScreen()
                }
            }
        )
    }
}
