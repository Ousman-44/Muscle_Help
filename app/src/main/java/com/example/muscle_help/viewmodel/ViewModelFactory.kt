
package com.example.muscle_help.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.muscle_help.data.ExerciceRepository

class ViewModelFactory(
    private val repository: ExerciceRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        @Suppress("UNCHECKED_CAST")
        return when {
            modelClass.isAssignableFrom(
                MusclesViewModel::class.java
            ) -> MusclesViewModel(repository) as T

            modelClass.isAssignableFrom(
                PPLViewModel::class.java
            ) -> PPLViewModel(repository) as T

            modelClass.isAssignableFrom(
                GeneratorViewModel::class.java
            ) -> GeneratorViewModel(repository) as T

            else -> throw IllegalArgumentException(
                "ViewModel inconnu"
            )
        }
    }
}
