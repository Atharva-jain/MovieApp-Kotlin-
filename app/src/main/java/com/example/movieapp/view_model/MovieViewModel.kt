package com.example.movieapp.view_model

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.movieapp.MovieApplication
import com.example.movieapp.model.*
import com.example.movieapp.repository.MovieRepository
import com.example.movieapp.util.Resources
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class MovieViewModel(
    app: Application,
    private val movieRepository: MovieRepository
) : AndroidViewModel(app) {

    private val TAG = "MovieViewModel"
    val getTrendingMoviesList: MutableLiveData<Resources<Trending>> = MutableLiveData()
    val getPopularMoviesList: MutableLiveData<Resources<Trending>> = MutableLiveData()
    val getTopRatedMoviesList: MutableLiveData<Resources<Trending>> = MutableLiveData()
    val getNowPlayingMoviesList: MutableLiveData<Resources<Trending>> = MutableLiveData()
    val getMovieRecommendation: MutableLiveData<Resources<Trending>> = MutableLiveData()
    val getSearchMoviesList: MutableLiveData<Resources<Trending>> = MutableLiveData()
    val getMovieData: MutableLiveData<Resources<MovieDetails>> = MutableLiveData()
    val getMovieCast: MutableLiveData<Resources<Cast>> = MutableLiveData()
    val getMovieVideo: MutableLiveData<Resources<Video>> = MutableLiveData()
    val getUpcomingMovieList: MutableLiveData<Resources<Trending>> = MutableLiveData()
    val getMovieCategoryList: MutableLiveData<Resources<Category>> = MutableLiveData()
    val getGenreMovieList: MutableLiveData<Resources<Trending>> = MutableLiveData()
    val userData: MutableLiveData<DocumentSnapshot> = MutableLiveData()
    val isUserExist: MutableLiveData<Boolean> = MutableLiveData()
    val loginResult = movieRepository.loginResult
    val userResult = movieRepository.userResult

    var trendingPage = 1
    var popularPage = 1
    var topRatedPage = 1
    var nowPlayingPage = 1
    var searchPage = 1
    var recommendationPage = 1
    var upcomingPage = 1
    var genrePage = 1

    private var trendingResponse: Trending? = null
    private var popularResponse: Trending? = null
    private var topRateResponse: Trending? = null
    private var nowPlayingResponse: Trending? = null
    private var searchResponse: Trending? = null
    private var movieDetailsResponse: MovieDetails? = null
    private var movieRecommendationResponse: Trending? = null
    private var upcomingResponse: Trending? = null
    private var genreResponse: Trending? = null

    fun getTrendingMovies() {
        Log.d(TAG, "Trending Called In ViewModel")
        viewModelScope.launch {
            getTrendingMoviesList.postValue(Resources.Loading())
            try {
                if (hasInternetConnection()) {
                    val response = movieRepository.getTrendingMovies(trendingPage)
                    getTrendingMoviesList.postValue(handleTrendingResponse(response))
                } else {
                    getTrendingMoviesList.postValue(Resources.Error("No Internet Connection"))
                }

            } catch (t: Throwable) {
                when (t) {
                    is IOException -> getTrendingMoviesList.postValue(Resources.Error("Network Failure"))
                    else -> getTrendingMoviesList.postValue(Resources.Error("Conversion Error ${t.message}"))
                }
            }
        }
    }

    private fun handleTrendingResponse(response: Response<Trending>): Resources<Trending> {
        Log.d(TAG, "Trending Page Number $trendingPage")
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                if (trendingPage == 1) {
                    trendingResponse = resultResponse
                } else {
                    if (trendingResponse == null) {
                        trendingResponse = resultResponse
                    } else {
                        val newMovies = resultResponse.results
                        trendingResponse?.results?.addAll(newMovies)
                    }
                }
                trendingPage++
                return Resources.Success(trendingResponse ?: resultResponse)
            }
        }
        return Resources.Error(response.message())

    }

    fun getPopularMovies() {
        Log.d(TAG, "Popular Called In ViewModel")
        viewModelScope.launch {
            getPopularMoviesList.postValue(Resources.Loading())
            try {
                if (hasInternetConnection()) {
                    val response = movieRepository.getPopularMovie(popularPage)
                    getPopularMoviesList.postValue(handlePopularResponse(response))
                } else {
                    getPopularMoviesList.postValue(Resources.Error("No Internet Connection"))
                }
            } catch (t: Throwable) {
                when (t) {
                    is IOException -> getPopularMoviesList.postValue(Resources.Error("Network Failure"))
                    else -> getPopularMoviesList.postValue(Resources.Error("Conversion Error :-${t.message}"))
                }
            }
        }
    }

    private fun handlePopularResponse(response: Response<Trending>): Resources<Trending> {
        Log.d(TAG, "Popular Page Number $popularPage")
        if (response.isSuccessful) {
            Log.d(TAG, "Popular Handle Function Called")
            response.body()?.let { resultResponse ->
                if (popularPage == 1) {
                    popularResponse = resultResponse
                } else {
                    if (popularResponse == null) {
                        popularResponse = resultResponse
                    } else {
                        val newMovies = resultResponse.results
                        popularResponse?.results?.addAll(newMovies)
                    }
                }
                popularPage++
                return Resources.Success(popularResponse ?: resultResponse)
            }
        }
        return Resources.Error(response.message())
    }

    fun getTopRatedMovies() {
        Log.d(TAG, "Top Rated Called in ViewModel")
        viewModelScope.launch {
            getTopRatedMoviesList.postValue(Resources.Loading())
            try {
                if (hasInternetConnection()) {
                    val response = movieRepository.getTopRatedMovie(topRatedPage)
                    getTopRatedMoviesList.postValue(handleTopRatedResponse(response))
                } else {
                    getTopRatedMoviesList.postValue(Resources.Error("No Internet Connection"))
                }
            } catch (t: Throwable) {
                when (t) {
                    is IOException -> getTopRatedMoviesList.postValue(Resources.Error("Network Failure"))
                    else -> getTopRatedMoviesList.postValue(Resources.Error("Conversion Error ${t.message}"))
                }
            }
        }
    }

    private fun handleTopRatedResponse(response: Response<Trending>): Resources<Trending> {
        Log.d(TAG, "Top Rated Page Number $topRatedPage")
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                if (topRatedPage == 1) {
                    topRateResponse = resultResponse
                } else {
                    if (topRateResponse == null) {
                        topRateResponse = resultResponse
                    } else {
                        val newMovies = resultResponse.results
                        topRateResponse?.results?.addAll(newMovies)
                    }
                }
                topRatedPage++
                return Resources.Success(topRateResponse ?: resultResponse)
            }
        }
        return Resources.Error(response.message())
    }

    fun getNowPlaying() {
        Log.d(TAG, "Now Playing Called In ViewModel")
        viewModelScope.launch {
            getNowPlayingMoviesList.postValue(Resources.Loading())
            try {
                if (hasInternetConnection()) {
                    val response = movieRepository.getNowPlayingMovie(nowPlayingPage)
                    getNowPlayingMoviesList.postValue(handleNowPlayingResponse(response))
                } else {
                    getNowPlayingMoviesList.postValue(Resources.Error("No Internet Connection"))
                }
            } catch (t: Throwable) {
                when (t) {
                    is IOException -> getNowPlayingMoviesList.postValue(Resources.Error("Network Failure"))
                    else -> getNowPlayingMoviesList.postValue(Resources.Error("Conversion Error ${t.message}"))
                }
            }
        }
    }

    private fun handleNowPlayingResponse(response: Response<Trending>): Resources<Trending> {
        Log.d(TAG, "Now Playing Page Number $nowPlayingPage")
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                if (nowPlayingPage == 1) {
                    nowPlayingResponse = resultResponse
                } else {
                    if (nowPlayingResponse == null) {
                        nowPlayingResponse = resultResponse
                    } else {
                        val newMovies = resultResponse.results
                        nowPlayingResponse?.results?.addAll(newMovies)
                    }
                }
                nowPlayingPage++
                return Resources.Success(nowPlayingResponse ?: resultResponse)
            }
        }
        return Resources.Error(response.message())
    }

    fun searchMovie(query: String) {
        Log.d(TAG, "Search Called In ViewModel")
        viewModelScope.launch {
            getSearchMoviesList.postValue(Resources.Loading())
            try {
                if (hasInternetConnection()) {
                    val response = movieRepository.searchMovie(query, searchPage)
                    getSearchMoviesList.postValue(handleSearchMovieResponse(response))
                } else {
                    getSearchMoviesList.postValue(Resources.Error("No Internet Connection"))
                }
            } catch (t: Throwable) {
                when (t) {
                    is IOException -> getSearchMoviesList.postValue(Resources.Error("Network Failure"))
                    else -> getSearchMoviesList.postValue(Resources.Error("Conversion Error ${t.message}"))
                }
            }
        }
    }

    private fun handleSearchMovieResponse(response: Response<Trending>): Resources<Trending> {
        Log.d(TAG, "Search Query Page Number $searchPage")
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                if (searchPage == 1) {
                    searchResponse = resultResponse
                } else {
                    if (searchResponse == null) {
                        searchResponse = resultResponse
                    } else {
                        val newMovies = resultResponse.results
                        searchResponse?.results?.addAll(newMovies)
                    }
                }
                searchPage++
                return Resources.Success(searchResponse ?: resultResponse)
            }
        }
        return Resources.Error(response.message())
    }

    fun getMovieDetails(id: Long) {
        Log.d(TAG, "Movie Details Has Called")
        viewModelScope.launch {
            try {
                getMovieData.postValue(Resources.Loading())
                if (hasInternetConnection()) {
                    val response = movieRepository.getMovieDetails(id)
                    getMovieData.postValue(handleMovieDataResponse(response))
                } else {
                    getMovieData.postValue(Resources.Error("No Internet Connection"))
                }
            } catch (t: Throwable) {
                when (t) {
                    is IOException -> getMovieData.postValue(Resources.Error("Network Failure"))
                    else -> getMovieData.postValue(Resources.Error("Conversion Error ${t.message}"))
                }
            }
        }
    }

    private fun handleMovieDataResponse(response: Response<MovieDetails>): Resources<MovieDetails> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                movieDetailsResponse = resultResponse
                return Resources.Success(movieDetailsResponse ?: resultResponse)
            }
        }
        return Resources.Error(response.message())
    }

    fun getCastDetails(id: Long) {
        getMovieCast.postValue(Resources.Loading())
        viewModelScope.launch {
            try {
                if (hasInternetConnection()) {
                    val response = movieRepository.getCastDetails(id)
                    getMovieCast.postValue(handleCastResponse(response))
                } else {
                    getMovieCast.postValue(Resources.Error("No Internet Connection"))
                }
            } catch (t: Throwable) {
                when (t) {
                    is IOException -> getMovieCast.postValue(Resources.Error("Network Failure"))
                    else -> getMovieCast.postValue(Resources.Error("Conversion Error ${t.message}"))
                }
            }

        }
    }

    private fun handleCastResponse(response: Response<Cast>): Resources<Cast> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resources.Success(resultResponse)
            }
        }
        return Resources.Error(response.message())
    }

    fun getMovieVideo(id: Long) {
        viewModelScope.launch {
            getMovieVideo.postValue(Resources.Loading())
            try {
                if (hasInternetConnection()) {
                    val response = movieRepository.getMovieVideo(id)
                    getMovieVideo.postValue(handleVideoResponse(response))
                } else {
                    getMovieVideo.postValue(Resources.Error("No Internet Connection"))
                }
            } catch (t: Throwable) {
                when (t) {
                    is IOException -> getMovieVideo.postValue(Resources.Error("Network Failure"))
                    else -> getMovieVideo.postValue(Resources.Error("Conversion Error ${t.message}"))
                }
            }
        }
    }

    private fun handleVideoResponse(response: Response<Video>): Resources<Video> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resources.Success(resultResponse)
            }
        }
        return Resources.Error(response.message())
    }

    fun getMovieRecommendations(id: Long) {
        viewModelScope.launch {
            getMovieRecommendation.postValue(Resources.Loading())
            try {
                if (hasInternetConnection()) {
                    val response = movieRepository.getMovieRecommendations(id, recommendationPage)
                    getMovieRecommendation.postValue(handleRecommendationResponse(response))
                } else {
                    getMovieRecommendation.postValue(Resources.Error("No Internet Connection"))
                }
            } catch (t: Throwable) {
                when (t) {
                    is IOException -> getMovieRecommendation.postValue(Resources.Error("Network Failure"))
                    else -> getMovieRecommendation.postValue(Resources.Error("Conversion Error ${t.message}"))
                }
            }
        }
    }

    private fun handleRecommendationResponse(response: Response<Trending>): Resources<Trending> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                if (recommendationPage == 1) {
                    movieRecommendationResponse = resultResponse
                } else {
                    if (movieDetailsResponse == null) {
                        movieRecommendationResponse = resultResponse
                    } else {
                        val newMovies = resultResponse.results
                        movieRecommendationResponse?.results?.addAll(newMovies)
                    }
                }
                recommendationPage++
                return Resources.Success(movieRecommendationResponse ?: resultResponse)
            }
        }
        return Resources.Error(response.message())
    }

    fun getUpcomingMovie(){
        viewModelScope.launch {
            getUpcomingMovieList.postValue(Resources.Loading())
            try {
                if (hasInternetConnection()) {
                    val response = movieRepository.getUpComingMovie(upcomingPage)
                    getUpcomingMovieList.postValue(handleUpcomingResponse(response))
                } else {
                    getUpcomingMovieList.postValue(Resources.Error("No Internet Connection"))
                }
            } catch (t: Throwable) {
                when (t) {
                    is IOException -> getUpcomingMovieList.postValue(Resources.Error("Network Failure"))
                    else -> getUpcomingMovieList.postValue(Resources.Error("Conversion Error ${t.message}"))
                }
            }
        }
    }

    private fun handleUpcomingResponse(response: Response<Trending>): Resources<Trending> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                if (upcomingPage == 1) {
                    upcomingResponse = resultResponse
                } else {
                    if (upcomingResponse == null) {
                        upcomingResponse = resultResponse
                    } else {
                        val newMovies = resultResponse.results
                        upcomingResponse?.results?.addAll(newMovies)
                    }
                }
                upcomingPage++
                return Resources.Success(upcomingResponse ?: resultResponse)
            }
        }
        return Resources.Error(response.message())
    }

    fun getMovieCategory(){
        viewModelScope.launch {
            getMovieCategoryList.postValue(Resources.Loading())
            try {
                if (hasInternetConnection()) {
                    val response = movieRepository.getMovieCategory()
                    getMovieCategoryList.postValue(handleCategoryResponse(response))
                } else {
                    getMovieCategoryList.postValue(Resources.Error("No Internet Connection"))
                }
            } catch (t: Throwable) {
                when (t) {
                    is IOException -> getMovieCategoryList.postValue(Resources.Error("Network Failure"))
                    else -> getMovieCategoryList.postValue(Resources.Error("Conversion Error ${t.message}"))
                }
            }

        }
    }

    private fun handleCategoryResponse(response: Response<Category>): Resources<Category> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resources.Success(resultResponse)
            }
        }
        return Resources.Error(response.message())
    }

    fun getGenreMovie(genreId: Int){
        Log.d(TAG, "Genre Called In ViewModel")
        viewModelScope.launch {
            getGenreMovieList.postValue(Resources.Loading())
            try {
                if (hasInternetConnection()) {
                    val response = movieRepository.getGenresMovie(genreId, genrePage)
                    getGenreMovieList.postValue(handleGenreResponse(response))
                } else {
                    getGenreMovieList.postValue(Resources.Error("No Internet Connection"))
                }
            } catch (t: Throwable) {
                when (t) {
                    is IOException -> getGenreMovieList.postValue(Resources.Error("Network Failure"))
                    else -> getGenreMovieList.postValue(Resources.Error("Conversion Error ${t.message}"))
                }
            }
        }
    }

    private fun handleGenreResponse(response: Response<Trending>): Resources<Trending> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                if (genrePage == 1) {
                    genreResponse = resultResponse
                } else {
                    if (genreResponse == null) {
                        genreResponse = resultResponse
                    } else {
                        val newMovies = resultResponse.results
                        genreResponse?.results?.addAll(newMovies)
                    }
                }
                genrePage++
                return Resources.Success(genreResponse ?: resultResponse)
            }
        }
        return Resources.Error(response.message())
    }

    fun login(idToken: String){
        viewModelScope.launch {
            movieRepository.loginIn(idToken)
        }
    }

    fun addUser(user: User){
        viewModelScope.launch {
            movieRepository.addUser(user)
        }
    }

    fun getUser(uid : String){
        viewModelScope.launch {
            try {
                val user = movieRepository.getUser(uid)
                if(user != null){
                    userData.postValue(user)
                }
            }catch (e: Exception){
                Log.d(TAG,"Data Are not Get From Firebase $e")
            }
        }
    }

    private fun hasInternetConnection(): Boolean {
        val connectivityManager =
            getApplication<MovieApplication>().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities =
                connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.activeNetworkInfo?.run {
                return when (type) {
                    ConnectivityManager.TYPE_WIFI -> true
                    ConnectivityManager.TYPE_MOBILE -> true
                    ConnectivityManager.TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }

    fun updateUserUrl(uid: String, userUrl: Uri, imageExtension: String){
        viewModelScope.launch {
            movieRepository.updateUserUrl(uid, userUrl, imageExtension)
        }
    }

    fun updateUserName(uid: String, userName: String) {
        viewModelScope.launch {
            movieRepository.updateUserName(uid, userName)
        }
    }

    fun isUserExist(uid : String){
        viewModelScope.launch {
            val value = movieRepository.isUserExist(uid)
            isUserExist.postValue(value)
        }
    }

    fun addHistory(result: Result){
        viewModelScope.launch {
            movieRepository.addHistory(result)
        }
    }

    fun addFavorite(result: Result){
        viewModelScope.launch {
            movieRepository.addFavorite(result)
        }
    }

    fun addWatchlist(result: Result){
        viewModelScope.launch {
            movieRepository.addWatchlist(result)
        }
    }

    fun removeHistory(result: Result){
        viewModelScope.launch {
            movieRepository.removeHistory(result)
        }
    }

    fun removeFavorite(result: Result){
        viewModelScope.launch {
            movieRepository.removeFavorite(result)
        }
    }

    fun removeWatchlist(result: Result){
        viewModelScope.launch {
            movieRepository.removeWatchlist(result)
        }
    }

}