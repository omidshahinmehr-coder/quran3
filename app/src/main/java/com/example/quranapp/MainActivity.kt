package com.example.quranapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.quranapp.data.QuranRepository
import com.example.quranapp.ui.QuranViewModel
import com.example.quranapp.ui.SearchScreen
import com.example.quranapp.ui.SurahListScreen
import com.example.quranapp.ui.SurahScreen
import com.example.quranapp.ui.TafsirScreen
import java.net.URLDecoder
import java.net.URLEncoder

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier) {
                    QuranApp()
                }
            }
        }
    }
}

@Composable
fun QuranApp() {
    val context = LocalContext.current
    val repo = remember { QuranRepository(context.applicationContext) }
    val viewModel: QuranViewModel = viewModel(factory = viewModelFactory(repo))
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "surahList") {
        composable("surahList") {
            SurahListScreen(
                viewModel = viewModel,
                onOpenSurah = { surahNumber -> navController.navigate("surah/$surahNumber") },
                onOpenSearch = { navController.navigate("search") }
            )
        }

        composable(
            "surah/{surahNumber}",
            arguments = listOf(navArgument("surahNumber") { type = NavType.IntType })
        ) { backStackEntry ->
            val surahNumber = backStackEntry.arguments?.getInt("surahNumber") ?: 1
            SurahScreen(
                viewModel = viewModel,
                surahNumber = surahNumber,
                onBack = { navController.popBackStack() },
                onOpenSearch = { navController.navigate("search") },
                onOpenTafsir = { globalAyahId, surahName, ayahNumber ->
                    val encodedName = URLEncoder.encode(surahName, "UTF-8")
                    navController.navigate("tafsir/$globalAyahId/$encodedName/$ayahNumber")
                }
            )
        }

        composable("search") {
            SearchScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onOpenSurah = { surahNumber ->
                    navController.navigate("surah/$surahNumber") {
                        popUpTo("surahList")
                    }
                }
            )
        }

        composable(
            "tafsir/{globalAyahId}/{surahName}/{ayahNumber}",
            arguments = listOf(
                navArgument("globalAyahId") { type = NavType.IntType },
                navArgument("surahName") { type = NavType.StringType },
                navArgument("ayahNumber") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val globalAyahId = backStackEntry.arguments?.getInt("globalAyahId") ?: 0
            val surahName = URLDecoder.decode(backStackEntry.arguments?.getString("surahName") ?: "", "UTF-8")
            val ayahNumber = backStackEntry.arguments?.getInt("ayahNumber") ?: 0
            TafsirScreen(
                viewModel = viewModel,
                globalAyahId = globalAyahId,
                surahName = surahName,
                ayahNumber = ayahNumber,
                onBack = { navController.popBackStack() }
            )
        }
    }
}

private fun viewModelFactory(repo: QuranRepository) =
    object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return QuranViewModel(repo) as T
        }
    }
