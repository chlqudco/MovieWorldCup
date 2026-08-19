package com.chlqudco.movieworldcup.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface TmdbApi {
    @GET("3/discover/movie")
    suspend fun discoverMovies(
        @Query("language") language: String,
        @Query("region") region: String,
        @Query("include_adult") includeAdult: Boolean,
        @Query("include_video") includeVideo: Boolean,
        @Query("sort_by") sortBy: String,
        @Query("vote_count.gte") minimumVoteCount: Int,
        @Query("primary_release_date.lte") releaseDateLimit: String,
        @Query("page") page: Int,
        @Query("with_genres") genreId: Int?
    ): TmdbMoviePageDto

    @GET("3/genre/movie/list")
    suspend fun movieGenres(
        @Query("language") language: String
    ): TmdbGenreListDto
}
