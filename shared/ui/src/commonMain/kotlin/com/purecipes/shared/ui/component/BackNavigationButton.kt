package com.purecipes.shared.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun BackNavigationButton(onBack: () -> Unit, modifier: Modifier = Modifier)
