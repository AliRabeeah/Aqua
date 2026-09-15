package com.alihalim.aqua

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alihalim.aqua.ui.AquaViewModel
import com.alihalim.aqua.ui.navigation.AquaNavigation
import com.alihalim.aqua.ui.theme.AquaTheme

class MainActivity : AppCompatActivity() {

    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { /* result handled silently */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestNotificationPermissionIfNeeded()

        setContent {
            val viewModel: AquaViewModel = viewModel()

            val themeMode by viewModel.themeMode.collectAsState()
            val accentHex by viewModel.accentHex.collectAsState()
            val language by viewModel.languageCode.collectAsState()

            // Apply the saved locale; AppCompat flips the layout direction
            // to RTL for Arabic and LTR for English automatically.
            ApplyLocale(language)

            AquaTheme(themeMode = themeMode, accentHex = accentHex) {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AquaNavigation(
                        viewModel = viewModel,
                        onLanguageChanged = { code -> applyLocale(code) }
                    )
                }
            }
        }
    }

    @androidx.compose.runtime.Composable
    private fun ApplyLocale(language: String) {
        androidx.compose.runtime.LaunchedEffect(language) {
            val current = AppCompatDelegate.getApplicationLocales()
            if (current.isEmpty || current[0]?.language != language) {
                applyLocale(language)
            }
        }
    }

    private fun applyLocale(code: String) {
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(code))
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) ==
                android.content.pm.PackageManager.PERMISSION_GRANTED
            if (!granted) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}
