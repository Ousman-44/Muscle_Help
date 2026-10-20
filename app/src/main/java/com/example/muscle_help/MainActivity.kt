
package com.example.muscle_help

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.muscle_help.ui.theme.MuscleHelpTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MuscleHelpTheme {
                Navigation()
            }
        }
    }
}
