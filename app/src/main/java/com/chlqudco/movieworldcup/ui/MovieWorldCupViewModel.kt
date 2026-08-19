package com.chlqudco.movieworldcup.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.chlqudco.movieworldcup.BuildConfig
import com.chlqudco.movieworldcup.data.MovieRepository
import com.chlqudco.movieworldcup.data.NotEnoughCandidatesException
import com.chlqudco.movieworldcup.data.TournamentStore
import com.chlqudco.movieworldcup.data.remote.TmdbServiceFactory
import com.chlqudco.movieworldcup.domain.MovieGenre
import com.chlqudco.movieworldcup.domain.PlayHistory
import com.chlqudco.movieworldcup.domain.TasteAnalyzer
import com.chlqudco.movieworldcup.domain.TournamentEngine
import com.chlqudco.movieworldcup.domain.TournamentMode
import com.chlqudco.movieworldcup.domain.TournamentSession
import kotlinx.coroutines.Job
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

enum class AppScreen {
    HOME,
    SETUP,
    LOADING,
    MATCH,
    RESULT,
    HISTORY,
    ABOUT
}

data class MovieWorldCupUiState(
    val screen: AppScreen = AppScreen.HOME,
    val isInitializing: Boolean = true,
    val selectedMode: TournamentMode = TournamentMode.POPULAR,
    val selectedSize: Int = 16,
    val selectedGenre: MovieGenre = MovieRepository.supportedGenres.first(),
    val genres: List<MovieGenre> = MovieRepository.supportedGenres,
    val session: TournamentSession? = null,
    val history: List<PlayHistory> = emptyList(),
    val isLoading: Boolean = false,
    val loadingMessage: String = "후보 영화를 고르고 있어요",
    val errorMessage: String? = null,
    val suggestedSize: Int? = null,
    val roundAnnouncement: String? = null,
    val selectedMovieId: Int? = null,
    val isSelectionLocked: Boolean = false,
    val message: String? = null
)

class MovieWorldCupViewModel(application: Application) : AndroidViewModel(application) {
    private val store = TournamentStore(application)
    private val repository = MovieRepository(
        TmdbServiceFactory.create(BuildConfig.TMDB_READ_ACCESS_TOKEN)
    )
    private val engine = TournamentEngine()
    private val tasteAnalyzer = TasteAnalyzer()
    private val _uiState = MutableStateFlow(MovieWorldCupUiState())
    private var announcementJob: Job? = null
    private var loadingJob: Job? = null

    val uiState: StateFlow<MovieWorldCupUiState> = _uiState.asStateFlow()

    init {
        restoreState()
    }

    fun openSetup() {
        loadingJob?.cancel()
        _uiState.update {
            it.copy(
                screen = AppScreen.SETUP,
                errorMessage = null,
                suggestedSize = null,
                message = null
            )
        }
    }

    fun selectMode(mode: TournamentMode) {
        _uiState.update { it.copy(selectedMode = mode) }
    }

    fun selectSize(size: Int) {
        if (size == 16 || size == 32) {
            _uiState.update { it.copy(selectedSize = size) }
        }
    }

    fun selectGenre(genre: MovieGenre) {
        _uiState.update { it.copy(selectedGenre = genre) }
    }

    fun startTournament(sizeOverride: Int? = null) {
        loadingJob?.cancel()
        val current = _uiState.value
        val size = sizeOverride ?: current.selectedSize
        val mode = current.selectedMode
        val genre = current.selectedGenre
        _uiState.update {
            it.copy(
                screen = AppScreen.LOADING,
                isLoading = true,
                errorMessage = null,
                suggestedSize = null,
                selectedSize = size,
                loadingMessage = if (mode == TournamentMode.GENRE) {
                    "${genre.name} 영화 후보를 고르고 있어요"
                } else {
                    "인기 영화 후보를 고르고 있어요"
                }
            )
        }

        loadingJob = viewModelScope.launch {
            if (BuildConfig.TMDB_READ_ACCESS_TOKEN.isBlank()) {
                showLoadingError("TMDB 인증 토큰이 설정되지 않았습니다.")
                return@launch
            }

            runCatching {
                val bundle = repository.loadCandidates(
                    size = size,
                    mode = mode,
                    genreId = genre.id.takeIf { mode == TournamentMode.GENRE }
                )
                engine.create(
                    candidates = bundle.movies,
                    genres = bundle.genres,
                    mode = mode,
                    size = size,
                    seed = bundle.seed,
                    genreId = genre.id.takeIf { mode == TournamentMode.GENRE },
                    genreName = genre.name.takeIf { mode == TournamentMode.GENRE }
                ).also { store.saveSession(it) }
            }.onSuccess { session ->
                _uiState.update {
                    it.copy(
                        screen = AppScreen.MATCH,
                        isLoading = false,
                        session = session,
                        genres = session.genres.ifEmpty { it.genres },
                        roundAnnouncement = session.roundLabel(),
                        selectedMovieId = null,
                        isSelectionLocked = false
                    )
                }
                clearAnnouncementLater(session.roundLabel())
            }.onFailure { throwable ->
                if (throwable !is CancellationException) showLoadingError(throwable)
            }
        }
    }

    fun selectMovie(movieId: Int) {
        val current = _uiState.value
        val session = current.session ?: return
        if (current.isSelectionLocked || session.completed) return
        val pair = session.currentPair() ?: return
        if (movieId != pair.first.id && movieId != pair.second.id) return

        _uiState.update {
            it.copy(
                selectedMovieId = movieId,
                isSelectionLocked = true
            )
        }

        viewModelScope.launch {
            delay(260)
            runCatching {
                val outcome = engine.select(session, movieId)
                val history = if (outcome.session.completed) {
                    tasteAnalyzer.toHistory(outcome.session).also {
                        store.completeSession(outcome.session, it)
                    }
                } else {
                    store.saveSession(outcome.session)
                    null
                }
                outcome to history
            }.onSuccess { (outcome, savedHistory) ->
                    val nextSession = outcome.session
                    if (nextSession.completed) {
                        val history = checkNotNull(savedHistory)
                        _uiState.update {
                            it.copy(
                                screen = AppScreen.RESULT,
                                session = nextSession,
                                history = listOf(history) + it.history.filterNot { saved ->
                                    saved.tournamentId == history.tournamentId
                                },
                                selectedMovieId = null,
                                isSelectionLocked = false,
                                roundAnnouncement = null
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                session = nextSession,
                                selectedMovieId = null,
                                isSelectionLocked = false,
                                roundAnnouncement = outcome.newRoundLabel
                            )
                        }
                        outcome.newRoundLabel?.let(::clearAnnouncementLater)
                    }
                }.onFailure {
                    _uiState.update { state ->
                        state.copy(
                            selectedMovieId = null,
                            isSelectionLocked = false,
                            message = "선택을 처리하지 못했습니다. 다시 시도해 주세요."
                        )
                    }
                }
        }
    }

    fun undoSelection() {
        val session = _uiState.value.session ?: return
        val restored = engine.undo(session) ?: return
        viewModelScope.launch {
            runCatching {
                if (session.completed) {
                    store.restoreCompletedSession(restored, session.id)
                } else {
                    store.saveSession(restored)
                }
            }.onSuccess {
                _uiState.update {
                    it.copy(
                        screen = AppScreen.MATCH,
                        session = restored,
                        history = if (session.completed) {
                            it.history.filterNot { saved -> saved.tournamentId == session.id }
                        } else {
                            it.history
                        },
                        selectedMovieId = null,
                        isSelectionLocked = false,
                        roundAnnouncement = null,
                        message = "직전 선택을 되돌렸습니다."
                    )
                }
            }.onFailure {
                _uiState.update { it.copy(message = "선택을 되돌리지 못했습니다.") }
            }
        }
    }

    fun goHome() {
        announcementJob?.cancel()
        loadingJob?.cancel()
        _uiState.update {
            it.copy(
                screen = AppScreen.HOME,
                roundAnnouncement = null,
                selectedMovieId = null,
                isSelectionLocked = false,
                errorMessage = null,
                suggestedSize = null
            )
        }
    }

    fun resumeTournament() {
        val session = _uiState.value.session ?: return
        _uiState.update {
            it.copy(screen = if (session.completed) AppScreen.RESULT else AppScreen.MATCH)
        }
    }

    fun openHistory() {
        _uiState.update { it.copy(screen = AppScreen.HISTORY) }
    }

    fun openAbout() {
        _uiState.update { it.copy(screen = AppScreen.ABOUT) }
    }

    fun dismissMessage() {
        _uiState.update { it.copy(message = null) }
    }

    private fun restoreState() {
        viewModelScope.launch {
            val session = runCatching { store.loadSession() }.getOrNull()
                ?.takeIf { it.completed || it.currentPair() != null }
            val history = runCatching { store.loadHistory() }.getOrDefault(emptyList())
            _uiState.update {
                it.copy(
                    isInitializing = false,
                    screen = if (session != null && !session.completed) {
                        AppScreen.MATCH
                    } else {
                        AppScreen.HOME
                    },
                    session = session,
                    history = history,
                    genres = session?.genres?.takeIf { saved -> saved.isNotEmpty() } ?: it.genres
                )
            }
        }
    }

    private fun showLoadingError(throwable: Throwable) {
        val suggestedSize = if (
            throwable is NotEnoughCandidatesException &&
            throwable.requested == 32 &&
            throwable.available >= 16
        ) {
            16
        } else {
            null
        }
        showLoadingError(errorMessage(throwable), suggestedSize)
    }

    private fun showLoadingError(message: String, suggestedSize: Int? = null) {
        _uiState.update {
            it.copy(
                screen = AppScreen.LOADING,
                isLoading = false,
                errorMessage = message,
                suggestedSize = suggestedSize
            )
        }
    }

    private fun errorMessage(throwable: Throwable): String = when (throwable) {
        is NotEnoughCandidatesException -> "조건에 맞는 포스터 영화가 ${throwable.available}개뿐입니다."
        is HttpException -> when (throwable.code()) {
            401 -> "TMDB 인증에 실패했습니다. local.properties의 토큰을 확인해 주세요."
            404 -> "TMDB 영화 정보를 찾지 못했습니다."
            429 -> "요청이 잠시 많습니다. 조금 뒤 다시 시도해 주세요."
            else -> "TMDB 서버 응답에 문제가 있습니다. (${throwable.code()})"
        }
        is IOException -> "인터넷 연결을 확인하고 다시 시도해 주세요."
        else -> throwable.message ?: "후보 영화를 불러오지 못했습니다."
    }

    private fun clearAnnouncementLater(label: String) {
        announcementJob?.cancel()
        announcementJob = viewModelScope.launch {
            delay(950)
            _uiState.update {
                if (it.roundAnnouncement == label) it.copy(roundAnnouncement = null) else it
            }
        }
    }
}
