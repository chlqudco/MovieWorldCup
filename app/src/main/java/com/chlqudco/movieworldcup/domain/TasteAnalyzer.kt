package com.chlqudco.movieworldcup.domain

class TasteAnalyzer {
    fun analyze(session: TournamentSession): TasteSummary {
        val movieById = session.allCandidates.associateBy(MovieCandidate::id)
        val genreNameById = session.genres.associate { it.id to it.name }
        val selectedMovies = session.selectedMovieIds.mapNotNull(movieById::get)

        val favoriteGenres = selectedMovies
            .flatMap(MovieCandidate::genreIds)
            .groupingBy { it }
            .eachCount()
            .entries
            .sortedWith(compareByDescending<Map.Entry<Int, Int>> { it.value }.thenBy { genreNameById[it.key] })
            .mapNotNull { genreNameById[it.key] }
            .distinct()
            .take(3)
            .ifEmpty { listOfNotNull(session.genreName) }

        val favoriteDecade = selectedMovies
            .mapNotNull { it.releaseDate.take(4).toIntOrNull() }
            .map { it / 10 * 10 }
            .groupingBy { it }
            .eachCount()
            .maxWithOrNull(compareBy<Map.Entry<Int, Int>> { it.value }.thenBy { it.key })
            ?.key
            ?.let { "${it}년대" }
            ?: "연도 취향 탐색 중"

        return TasteSummary(
            favoriteGenres = favoriteGenres,
            favoriteDecade = favoriteDecade,
            choiceCount = selectedMovies.size
        )
    }

    fun toHistory(session: TournamentSession): PlayHistory {
        val champion = checkNotNull(session.champion)
        val summary = analyze(session)
        val modeName = when (session.mode) {
            TournamentMode.POPULAR -> session.mode.displayName
            TournamentMode.GENRE -> session.genreName ?: session.mode.displayName
        }
        return PlayHistory(
            tournamentId = session.id,
            playedAt = System.currentTimeMillis(),
            champion = champion,
            modeName = modeName,
            size = session.size,
            favoriteGenres = summary.favoriteGenres,
            favoriteDecade = summary.favoriteDecade
        )
    }
}
