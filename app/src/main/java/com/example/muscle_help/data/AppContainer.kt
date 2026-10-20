
package com.example.muscle_help.data

import com.example.muscle_help.viewmodel.ViewModelFactory

object AppContainer {

    private val api = ExerciceAPI()

    private val repository = ExerciceRepository(api)

    val viewModelFactory = ViewModelFactory(repository)
}
