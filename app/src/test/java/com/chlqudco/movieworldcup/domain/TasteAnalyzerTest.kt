package com.chlqudco.movieworldcup.domain

import org.junit.Assert.assertEquals
import org.junit.Test

class TasteAnalyzerTest {
    private val analyzer = TasteAnalyzer()

    @Test
    fun selectedMoviesProduceGenreAndDecadeSummary() {
        val drama = MovieGenre(18, "드라마")
        val action = MovieGenre(28, "액션")
        val first = MovieCandidate(
            id = 1,
            title = "첫 영화",
            releaseDate = "1997-01-01",
            genreIds = listOf(18)
        )
        val second = MovieCandidate(
            id = 2,
            title = "두 번째 영화",
            releaseDate = "1999-01-01",
            genreIds = listOf(18, 28)
        )
        val third = MovieCandidate(
            id = 3,
            title = "세 번째 영화",
            releaseDate = "2015-01-01",
            genreIds = listOf(28)
        )
        val session = TournamentSession(
            id = "taste",
            allCandidates = listOf(first, second, third),
            genres = listOf(drama, action),
            selectedMovieIds = listOf(1, 2, 2, 3),
            champion = second,
            completed = true
        )

        val summary = analyzer.analyze(session)

        assertEquals(listOf("드라마", "액션"), summary.favoriteGenres)
        assertEquals("1990년대", summary.favoriteDecade)
        assertEquals(4, summary.choiceCount)
    }
}
