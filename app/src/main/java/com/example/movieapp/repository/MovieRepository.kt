package com.example.movieapp.repository

import android.net.Uri
import android.nfc.Tag
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.movieapp.api.RetrofitInstance
import com.example.movieapp.dao.MovieDoa
import com.example.movieapp.model.*
import com.example.movieapp.util.Constants.Companion.API_KEY
import com.example.movieapp.util.Resources
import com.google.firebase.firestore.DocumentSnapshot
import retrofit2.Response

class MovieRepository {

    private val TAG = "MovieRepository"
    private val movieDoa = MovieDoa()
    val loginResult: MutableLiveData<Resources<Boolean>> = movieDoa.loginResult
    val userResult: MutableLiveData<Resources<Boolean>> = movieDoa.userResult

    suspend fun getTrendingMovies(page: Int): Response<Trending> {
        Log.d(TAG, "Trending is called")
        return RetrofitInstance.api.getTrendingMovies(page)
    }

    suspend fun getPopularMovie(page: Int): Response<Trending> {
        Log.d(TAG, "Popular is called")
        return RetrofitInstance.api.getPopularMovie(page)
    }

    suspend fun getTopRatedMovie(page: Int): Response<Trending> {
        Log.d(TAG, "Top Rate is called")
        return RetrofitInstance.api.getTopRateMovie(page)
    }

    suspend fun getNowPlayingMovie(page: Int): Response<Trending> {
        Log.d(TAG, "Now Playing is called")
        return RetrofitInstance.api.getNowPlayingMovies(page)
    }

    suspend fun getUpComingMovie(page: Int): Response<Trending> {
        return RetrofitInstance.api.getUpcomingMovie(page)
    }

    suspend fun searchMovie(query: String, page: Int): Response<Trending> {
        return RetrofitInstance.api.searchMovies(query, page)
    }

    suspend fun getMovieDetails(id: Long): Response<MovieDetails> {
        return RetrofitInstance.api.getMovieDetails(id)
    }

    suspend fun getCastDetails(id: Long): Response<Cast> {
        return RetrofitInstance.api.getCastDetails(id)
    }

    suspend fun getMovieVideo(id: Long): Response<Video> {
        return RetrofitInstance.api.getMovieVideo(id)
    }

    suspend fun getMovieRecommendations(id: Long, page: Int): Response<Trending> {
        return RetrofitInstance.api.getMovieRecommendation(id, page)
    }

    suspend fun getMovieCategory(): Response<Category> {
        return RetrofitInstance.api.getMovieCategory()
    }

    suspend fun getGenresMovie(genreId: Int, page: Int): Response<Trending> {
        return RetrofitInstance.api.getGenreMovie(genreId, page)
    }

    suspend fun loginIn(tokenId: String) {
        movieDoa.loginInWithFirebase(tokenId)
    }

    suspend fun addUser(user: User) {
        movieDoa.addUserData(user)
    }

    suspend fun getUser(uId: String): DocumentSnapshot? {
        return movieDoa.getUserData(uId)
    }

    suspend fun updateUserUrl(uid: String, userUrl: Uri, imageExtension: String) {
        movieDoa.updateUserUrl(uid, userUrl, imageExtension)
    }

    suspend fun updateUserName(uid: String, userName: String) {
        movieDoa.updateUserName(uid, userName)
    }

    suspend fun isUserExist(uid: String): Boolean {
        return movieDoa.isUserExist(uid)
    }

    suspend fun addHistory(result: Result) {
        movieDoa.addHistory(result)
    }

    suspend fun addFavorite(result: Result) {
        movieDoa.addFavorite(result)
    }

    suspend fun addWatchlist(result: Result) {
        movieDoa.addWatchlist(result)
    }

    suspend fun removeHistory(result: Result) {
        movieDoa.removeHistory(result)
    }

    suspend fun removeFavorite(result: Result) {
        movieDoa.removeFavorite(result)
    }

    suspend fun removeWatchlist(result: Result) {
        movieDoa.removeWatchlist(result)
    }

}