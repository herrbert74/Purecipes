package com.purecipes.umbrella

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

class Greeting {
    fun greet(): String {
        return "Hello, ${Platform().platform}!"
    }
}

@Composable
fun HelloPlatformScreen(platform: String) {
	Box(
		modifier = Modifier.fillMaxSize(),
		contentAlignment = Alignment.Center
	) {
		Text(text = "Hello $platform!")
	}
}
