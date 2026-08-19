package com.chlqudco.movieworldcup.data

import com.chlqudco.movieworldcup.data.remote.TmdbApi
import com.chlqudco.movieworldcup.data.remote.TmdbMovieDto
import com.chlqudco.movieworldcup.domain.CandidateBundle
import com.chlqudco.movieworldcup.domain.MovieCandidate
import com.chlqudco.movieworldcup.domain.MovieGenre
import com.chlqudco.movieworldcup.domain.TournamentMode
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import java.time.LocalDate
import kotlin.random.Random

class NotEnoughCandidatesException(
    val available: Int,
    val requested: Int
) : IllegalStateException("후보 영화가 ${available}개만 조회되었습니다.")

class MovieRepository(
    private val api: TmdbApi
) {
    suspend fun loadCandidates(
        size: Int,
        mode: TournamentMode,
        genreId: Int?
    ): CandidateBundle = coroutineScope {
        val genresRequest = async {
            runCatching {
                api.movieGenres("ko-KR").genres
                    .filter { it.id > 0 && it.name.isNotBlank() }
                    .map { MovieGenre(it.id, it.name) }
            }.getOrDefault(supportedGenres)
        }
        val minimumVoteCount = if (mode == TournamentMode.POPULAR) 300 else 80
        val firstPages = (1..4).map { page ->
            async { discoverPage(page, minimumVoteCount, genreId) }
        }.awaitAll()
        val collected = firstPages.flatten().toMutableList()

        if (collected.toCandidates().size < size) {
            val additionalPages = (5..8).map { page ->
                async { discoverPage(page, 20, genreId) }
            }.awaitAll()
            collected += additionalPages.flatten()
        }

        val candidates = collected.toCandidates()
        if (candidates.size < size) {
            throw NotEnoughCandidatesException(candidates.size, size)
        }

        CandidateBundle(
            movies = candidates,
            genres = genresRequest.await(),
            seed = Random.nextLong()
        )
    }

    private suspend fun discoverPage(
        page: Int,
        minimumVoteCount: Int,
        genreId: Int?
    ): List<TmdbMovieDto> = api.discoverMovies(
        language = "ko-KR",
        region = "KR",
        includeAdult = false,
        includeVideo = false,
        sortBy = "popularity.desc",
        minimumVoteCount = minimumVoteCount,
        releaseDateLimit = LocalDate.now().toString(),
        page = page,
        genreId = genreId
    ).results

    private fun List<TmdbMovieDto>.toCandidates(): List<MovieCandidate> = asSequence()
        .filter { !it.posterPath.isNullOrBlank() }
        .filter { !it.title.isNullOrBlank() || !it.originalTitle.isNullOrBlank() }
        .distinctBy(TmdbMovieDto::id)
        .map {
            MovieCandidate(
                id = it.id,
                title = it.title?.takeIf(String::isNotBlank)
                    ?: it.originalTitle?.takeIf(String::isNotBlank)
                    ?: "제목 정보 없음",
                originalTitle = it.originalTitle.orEmpty(),
                posterPath = it.posterPath.orEmpty(),
                releaseDate = it.releaseDate.orEmpty(),
                genreIds = it.genreIds,
                overview = it.overview.orEmpty(),
                voteAverage = it.voteAverage
            )
        }
        .toList()

    companion object {
        val supportedGenres = listOf(
            MovieGenre(28, "액션"),
            MovieGenre(12, "모험"),
            MovieGenre(16, "애니메이션"),
            MovieGenre(35, "코미디"),
            MovieGenre(80, "범죄"),
            MovieGenre(18, "드라마"),
            MovieGenre(10751, "가족"),
            MovieGenre(14, "판타지"),
            MovieGenre(27, "공포"),
            MovieGenre(9648, "미스터리"),
            MovieGenre(10749, "로맨스"),
            MovieGenre(878, "SF"),
            MovieGenre(53, "스릴러"),
            MovieGenre(10752, "전쟁")
        )
    }
}
