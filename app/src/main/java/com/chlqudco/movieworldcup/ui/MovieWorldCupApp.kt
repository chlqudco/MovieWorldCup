package com.chlqudco.movieworldcup.ui

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chlqudco.movieworldcup.domain.TasteAnalyzer
import com.chlqudco.movieworldcup.domain.TournamentMode
import com.chlqudco.movieworldcup.share.ResultShareManager
import com.chlqudco.movieworldcup.ui.components.AppBackground
import com.chlqudco.movieworldcup.ui.screens.AboutScreen
import com.chlqudco.movieworldcup.ui.screens.HistoryScreen
import com.chlqudco.movieworldcup.ui.screens.HomeScreen
import com.chlqudco.movieworldcup.ui.screens.LoadingScreen
import com.chlqudco.movieworldcup.ui.screens.MatchScreen
import com.chlqudco.movieworldcup.ui.screens.ResultScreen
import com.chlqudco.movieworldcup.ui.screens.SetupScreen
import com.chlqudco.movieworldcup.ui.theme.CinemaMuted
import com.chlqudco.movieworldcup.ui.theme.CinemaRed

@Composable
fun MovieWorldCupApp(
    viewModel: MovieWorldCupViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(state.message) {
        state.message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.dismissMessage()
        }
    }

    BackHandler(enabled = !state.isInitializing && state.screen != AppScreen.HOME) {
        if (state.screen == AppScreen.LOADING) {
            viewModel.openSetup()
        } else {
            viewModel.goHome()
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        AppBackground(modifier = Modifier.padding(innerPadding)) {
            if (state.isInitializing) {
                InitializingScreen()
            } else {
                AnimatedContent(
                    targetState = state.screen,
                    modifier = Modifier.fillMaxSize(),
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "screenTransition"
                ) { screen ->
                    when (screen) {
                        AppScreen.HOME -> HomeScreen(
                            session = state.session,
                            history = state.history,
                            onStart = viewModel::openSetup,
                            onQuickStart = {
                                viewModel.selectMode(TournamentMode.POPULAR)
                                viewModel.selectSize(16)
                                viewModel.startTournament()
                            },
                            onResume = viewModel::resumeTournament,
                            onGenreStart = {
                                viewModel.selectMode(TournamentMode.GENRE)
                                viewModel.openSetup()
                            },
                            onHistory = viewModel::openHistory,
                            onAbout = viewModel::openAbout,
                            modifier = Modifier.fillMaxSize()
                        )

                        AppScreen.SETUP -> SetupScreen(
                            selectedMode = state.selectedMode,
                            selectedSize = state.selectedSize,
                            selectedGenre = state.selectedGenre,
                            genres = state.genres,
                            hasActiveSession = state.session?.completed == false,
                            onBack = viewModel::goHome,
                            onModeSelected = viewModel::selectMode,
                            onSizeSelected = viewModel::selectSize,
                            onGenreSelected = viewModel::selectGenre,
                            onStart = { viewModel.startTournament() },
                            modifier = Modifier.fillMaxSize()
                        )

                        AppScreen.LOADING -> LoadingScreen(
                            isLoading = state.isLoading,
                            loadingMessage = state.loadingMessage,
                            errorMessage = state.errorMessage,
                            suggestedSize = state.suggestedSize,
                            onRetry = { viewModel.startTournament() },
                            onSuggestedSize = viewModel::startTournament,
                            onBack = viewModel::openSetup,
                            modifier = Modifier.fillMaxSize()
                        )

                        AppScreen.MATCH -> state.session?.let { session ->
                            MatchScreen(
                                session = session,
                                selectedMovieId = state.selectedMovieId,
                                isSelectionLocked = state.isSelectionLocked,
                                roundAnnouncement = state.roundAnnouncement,
                                onSelectMovie = viewModel::selectMovie,
                                onUndo = viewModel::undoSelection,
                                onExit = viewModel::goHome,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        AppScreen.RESULT -> state.session?.let { session ->
                            val summary = remember(session) { TasteAnalyzer().analyze(session) }
                            ResultScreen(
                                session = session,
                                summary = summary,
                                onShare = {
                                    val shared = ResultShareManager.share(context, session, summary)
                                    if (!shared) {
                                        Toast.makeText(
                                            context,
                                            "공유 이미지를 만들지 못했습니다.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                },
                                onUndo = viewModel::undoSelection,
                                onRestart = viewModel::openSetup,
                                onHome = viewModel::goHome,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        AppScreen.HISTORY -> HistoryScreen(
                            history = state.history,
                            onBack = viewModel::goHome,
                            modifier = Modifier.fillMaxSize()
                        )

                        AppScreen.ABOUT -> AboutScreen(
                            onBack = viewModel::goHome,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InitializingScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(color = CinemaRed)
        Text(
            text = "진행 상태 확인 중",
            modifier = Modifier.padding(top = 18.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = CinemaMuted
        )
    }
}
