package com.chlqudco.movieworldcup.domain

data class MovieCandidate(
    val id: Int = 0,
    val title: String = "",
    val originalTitle: String = "",
    val posterPath: String = "",
    val releaseDate: String = "",
    val genreIds: List<Int> = emptyList(),
    val overview: String = "",
    val voteAverage: Double = 0.0
) {
    val posterUrl: String
        get() = "https://image.tmdb.org/t/p/w500$posterPath"

    val releaseYear: String
        get() = releaseDate.take(4).ifBlank { "연도 미상" }
}

data class MovieGenre(
    val id: Int = 0,
    val name: String = ""
)

enum class TournamentMode(val displayName: String) {
    POPULAR("인기 영화"),
    GENRE("장르별")
}

data class UndoCheckpoint(
    val roundMovies: List<MovieCandidate> = emptyList(),
    val winners: List<MovieCandidate> = emptyList(),
    val matchIndex: Int = 0,
    val roundSize: Int = 0,
    val selectedMovieIds: List<Int> = emptyList()
)

data class TournamentSession(
    val id: String = "",
    val mode: TournamentMode = TournamentMode.POPULAR,
    val size: Int = 16,
    val seed: Long = 0L,
    val genreId: Int? = null,
    val genreName: String? = null,
    val allCandidates: List<MovieCandidate> = emptyList(),
    val genres: List<MovieGenre> = emptyList(),
    val roundMovies: List<MovieCandidate> = emptyList(),
    val winners: List<MovieCandidate> = emptyList(),
    val matchIndex: Int = 0,
    val roundSize: Int = 16,
    val selectedMovieIds: List<Int> = emptyList(),
    val champion: MovieCandidate? = null,
    val completed: Boolean = false,
    val startedAt: Long = 0L,
    val undoCheckpoint: UndoCheckpoint? = null
) {
    fun currentPair(): Pair<MovieCandidate, MovieCandidate>? {
        val leftIndex = matchIndex * 2
        val rightIndex = leftIndex + 1
        if (leftIndex !in roundMovies.indices || rightIndex !in roundMovies.indices) return null
        return roundMovies[leftIndex] to roundMovies[rightIndex]
    }

    fun roundLabel(): String = when (roundSize) {
        2 -> "결승"
        else -> "${roundSize}강"
    }

    fun matchCount(): Int = roundMovies.size / 2
}

data class TasteSummary(
    val favoriteGenres: List<String> = emptyList(),
    val favoriteDecade: String = "취향 수집 중",
    val choiceCount: Int = 0
)

data class PlayHistory(
    val tournamentId: String = "",
    val playedAt: Long = 0L,
    val champion: MovieCandidate = MovieCandidate(),
    val modeName: String = "",
    val size: Int = 16,
    val favoriteGenres: List<String> = emptyList(),
    val favoriteDecade: String = ""
)

data class CandidateBundle(
    val movies: List<MovieCandidate>,
    val genres: List<MovieGenre>,
    val seed: Long
)
