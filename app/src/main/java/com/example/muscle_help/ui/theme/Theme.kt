
package com.example.muscle_help.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val CouleursMuscleHelp = darkColorScheme(
    primary = BleuPrincipal,
    onPrimary = TextePrincipal,

    primaryContainer = BleuFonce,
    onPrimaryContainer = TextePrincipal,

    secondary = BleuClair,
    onSecondary = FondPrincipal,

    background = FondPrincipal,
    onBackground = TextePrincipal,

    surface = FondCartes,
    onSurface = TextePrincipal,

    surfaceVariant = FondCartes,
    onSurfaceVariant = TexteSecondaire,

    surfaceContainer = FondCartes,

    outline = Bordure
)

@Composable
fun MuscleHelpTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = CouleursMuscleHelp,
        content = content
    )
}
