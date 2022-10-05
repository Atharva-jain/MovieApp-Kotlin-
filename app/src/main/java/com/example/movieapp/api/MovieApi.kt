package com.example.movieapp.api

import com.example.movieapp.model.*
import com.example.movieapp.util.Constants.Companion.API_KEY
import com.example.movieapp.util.Constants.Companion.EN_US
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieApi {

    // this function are basically get the all trending movies that we have needed to display them
    @GET("3/trending/movie/week")
    suspend fun getTrendingMovies(
        @Query("page")
        page: Int = 1,
        @Query("api_key")
        apiKey: String = API_KEY,
    ):Response<Trending>

    // this function are basically get all popular movies that have we have needed to display them
    @GET("3/movie/popular")
    suspend fun getPopularMovie(
        @Query("page")
        page: Int = 1,
        @Query("api_key")
        apiKey: String = API_KEY,
    ):Response<Trending>

    // this function are basically get all Top Rated movies that have we have needed to display them
    @GET("3/movie/top_rated")
    suspend fun getTopRateMovie(
        @Query("page")
        page: Int = 1,
        @Query("api_key")
        apiKey: String = API_KEY,
    ):Response<Trending>

    // this function are basically get all Upcoming movies that have we have needed to display them
    @GET("3/movie/upcoming")
    suspend fun getUpcomingMovie(
        @Query("page")
        page: Int = 1,
        @Query("api_key")
        apiKey: String = API_KEY,
    ):Response<Trending>

    // this function are basically get all NowPlaying movies that have we have needed to display them
    @GET("3/movie/now_playing")
    suspend fun getNowPlayingMovies(
        @Query("page")
        page: Int = 1,
        @Query("api_key")
        apiKey: String = API_KEY,
    ):Response<Trending>

    // this function are basically return recommendation of movie according to there Movie id
    @GET("3/movie/{id}/recommendations")
    suspend fun getMovieRecommendation(
        @Path("id") id: Long,
        @Query("page") page: Int = 1,
        @Query("api_key") apiKey: String = API_KEY,
    ): Response<Trending>

    // this function are basically search a movie according to user as they want to search
    @GET("3/search/movie")
    suspend fun searchMovies(
        @Query("query")
        query: String,
        @Query("page")
        page: Int = 1,
        @Query("api_key")
        apiKey: String = API_KEY,
        @Query("include_adult")
        includeAdult: Boolean = false
    ): Response<Trending>

    // this function are get all details of specific movie
    @GET("3/movie/{id}")
    suspend fun getMovieDetails(
        @Path("id") id: Long,
        @Query("api_key")
        apiKey: String = API_KEY,
    ): Response<MovieDetails>

    // this function are get all Movie Cast details of specific movie
    @GET("3/movie/{id}/credits")
    suspend fun getCastDetails(
        @Path("id") id: Long,
        @Query("api_key")
        apiKey: String = API_KEY,
        @Query("language")
        language: String  = EN_US
    ): Response<Cast>

    // this function are get the movie video(eg. Trailer, Clips) by using movie id
    @GET("3/movie/{id}/videos")
    suspend fun getMovieVideo(
        @Path("id") id: Long,
        @Query("api_key")
        api_key: String = API_KEY
    ): Response<Video>

    // this function are get movie category(eg. Action, Thriller) by using Movie is
    @GET("3/genre/movie/list")
    suspend fun getMovieCategory(
        @Query("api_key") apiKey: String = API_KEY,
        @Query("language") language: String = "en-US"
    ): Response<Category>

    // thi function are get movie according to the genres
    @GET("3/discover/movie")
    suspend fun getGenreMovie(
        @Query("with_genres") genreId: Int,
        @Query("page") page: Int = 1,
        @Query("api_key") api_key: String = API_KEY
    ): Response<Trending>

}