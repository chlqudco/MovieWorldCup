package com.chlqudco.movieworldcup.data.remote

import com.google.gson.annotations.SerializedName

data class TmdbMoviePageDto(
    val page: Int = 0,
    val results: List<TmdbMovieDto> = emptyList(),
    @SerializedName("total_pages") val totalPages: Int = 0
)

data class TmdbMovieDto(
    val id: Int = 0,
    val title: String? = null,
    @SerializedName("original_title") val originalTitle: String? = null,
    @SerializedName("poster_path") val posterPath: String? = null,
    @SerializedName("release_date") val releaseDate: String? = null,
    @SerializedName("genre_ids") val genreIds: List<Int> = emptyList(),
    val overview: String? = null,
    @SerializedName("vote_average") val voteAverage: Double = 0.0
)

data class TmdbGenreListDto(
    val genres: List<TmdbGenreDto> = emptyList()
)

data class TmdbGenreDto(
    val id: Int = 0,
    val name: String = ""
)
