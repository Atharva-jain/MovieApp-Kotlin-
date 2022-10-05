package com.example.movieapp.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.movieapp.MainActivity
import com.example.movieapp.MovieNavDirections
import com.example.movieapp.adapter.*
import com.example.movieapp.databinding.FragmentMovieDetailsBinding
import com.example.movieapp.model.*
import com.example.movieapp.util.Constants.Companion.YOUTUBE_API_KEY
import com.example.movieapp.util.HasInternetConnection
import com.example.movieapp.util.HideKeyboard
import com.example.movieapp.util.Resources
import com.example.movieapp.view_model.MovieViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.android.youtube.player.YouTubeStandalonePlayer
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MovieDetailsFragment : Fragment(), OnVideoWatchClicked, OnClickedRecommendation {

    private val TAG: String = "MovieDetailsFragment"
    private lateinit var movieDetailsBinding: FragmentMovieDetailsBinding
    private val args by navArgs<MovieDetailsFragmentArgs>()
    private lateinit var viewModel: MovieViewModel
    private lateinit var castAdapter: CastAdapter
    private lateinit var videoAdapter: VideoAdapter
    private lateinit var result: Result
    private lateinit var recommendationAdapter: RecommendationAdapter

    override fun onStart() {
        super.onStart()
        (activity as MainActivity).mainBinding.searchMovieInput.clearFocus()
        HideKeyboard.hideKeyboard(activity as MainActivity)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).mainBinding.cvSearchView.visibility = View.VISIBLE
        (activity as MainActivity).mainBinding.movieBottomNav.visibility = View.VISIBLE
        result = args.movieId
        val id = args.movieId.id.toLong()
        viewModel = (activity as MainActivity).viewModel
        viewModel.recommendationPage = 1
        viewModel.getMovieDetails(id)
        viewModel.getCastDetails(id)
        viewModel.getMovieVideo(id)
        viewModel.getMovieRecommendations(id)
        setUpRecyclerView()
        viewModel.addHistory(result)
        viewModel.getMovieData.observe(viewLifecycleOwner, { response ->
            when (response) {
                is Resources.Success -> {
                    val data = response.data
                    Log.d(TAG, "\n $data")
                    if (data != null) {
                        setUpMovieDetails(data)
                    }
                }
                is Resources.Error -> {
                    response.message.let { message ->
                        Log.d(TAG, "\n Title = $message \n")
                    }

                }
                is Resources.Loading -> {
                    Log.d(TAG, "Loading .......")
                }
            }
        })
        viewModel.getMovieCast.observe(viewLifecycleOwner, { response ->
            when (response) {
                is Resources.Success -> {
                    val data = response.data
                    if (data != null) {
                        val cast = data.cast
                        castAdapter.differ.submitList(cast)
                        val crew = data.crew
                        getDirectorName(crew)
                    }

                }
                is Resources.Error -> {
                    response.message.let { message ->
                        Log.d(TAG, "\n Title = $message \n")
                    }

                }
                is Resources.Loading -> {
                    Log.d(TAG, "Loading .......")
                }
            }
        })
        viewModel.getMovieVideo.observe(viewLifecycleOwner, { response ->
            when (response) {
                is Resources.Success -> {
                    val result = response.data
                    if (result != null) {
                        videoAdapter.differ.submitList(result.results)
                    }
                }
                is Resources.Error -> {
                    response.message.let { message ->
                        Log.d(TAG, "\n Title = $message \n")
                    }

                }
                is Resources.Loading -> {
                    Log.d(TAG, "Loading .......")
                }
            }

        })
        viewModel.getMovieRecommendation.observe(viewLifecycleOwner,{ response ->
            when (response) {
                is Resources.Success -> {
                    val result = response.data
                    if (result != null) {
                        recommendationAdapter.differ.submitList(result.results.toList())
                    }
                }
                is Resources.Error -> {
                    response.message.let { message ->
                        Log.d(TAG, "\n Title = $message \n")
                    }

                }
                is Resources.Loading -> {
                    Log.d(TAG, "Loading .......")
                }
            }
        })

        movieDetailsBinding.btAddFavorite.setOnClickListener {
            if(HasInternetConnection.hasInternetConnection((activity as MainActivity).application)){
                val uid  = FirebaseAuth.getInstance().currentUser?.uid
                if(uid != null){
                    viewModel.addFavorite(result)
                    Snackbar.make(view, "Favorite added", Snackbar.LENGTH_LONG).show()
                }
                else{
                    Snackbar.make(view,"Please Sign in", Snackbar.LENGTH_LONG).show()
                }
            }else{
                Snackbar.make(view,"No Internet", Snackbar.LENGTH_LONG).show()
            }
        }
        movieDetailsBinding.btAddWatchlist.setOnClickListener {
            if(HasInternetConnection.hasInternetConnection((activity as MainActivity).application)){
                val uid  = FirebaseAuth.getInstance().currentUser?.uid
                if(uid != null){
                    viewModel.addWatchlist(result)
                    Snackbar.make(view, "Watchlist added", Snackbar.LENGTH_LONG).show()
                }
                else{
                    Snackbar.make(view,"Please Sign in", Snackbar.LENGTH_LONG).show()
                }
            }else{
                Snackbar.make(view,"No Internet", Snackbar.LENGTH_LONG).show()
            }
        }


    }

    private fun getDirectorName(crew: List<Crew>) {
        var director = ""
        crew.forEach{ crewData ->
            if(crewData.job == "Director"){
                director = crewData.name
            }
        }
        movieDetailsBinding.tvDirectorDetails?.text = director
    }

    private fun setUpRecyclerView() {
        castAdapter = CastAdapter()
        movieDetailsBinding.rvTopBilledCastDetails?.adapter = castAdapter
        movieDetailsBinding.rvTopBilledCastDetails?.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        videoAdapter = VideoAdapter(this)
        movieDetailsBinding.rvMediaDetails?.adapter = videoAdapter
        movieDetailsBinding.rvMediaDetails?.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        recommendationAdapter = RecommendationAdapter(this)
        movieDetailsBinding.rvRecommendationDetails?.adapter = recommendationAdapter
        movieDetailsBinding.rvRecommendationDetails?.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
    }


    private fun setUpMovieDetails(data: MovieDetails) {
        val urlOfBackground = "https://image.tmdb.org/t/p/original/${data.backdrop_path}"
        movieDetailsBinding.ivBacgroundImageDetails?.let {
            Glide.with(this).load(urlOfBackground).into(it)
        }
        val urlOfPoster = "https://image.tmdb.org/t/p/original/${data.poster_path}"
        movieDetailsBinding.ivPosterImageDetails?.let {
            Glide.with(this).load(urlOfPoster).into(it)
        }
        movieDetailsBinding.tvMovieTitleDetails?.text = data.original_title
        movieDetailsBinding.tvReleaseDateDetails?.text = data.release_date
        GlobalScope.launch {
            val genres: String = getGeneresString(data.genres)
            withContext(Dispatchers.Main) {
                movieDetailsBinding.tvGenresDetails?.text = "Genres: $genres"
            }
        }
        val movieTime = getMovieTimeIntoMinuteAndHour(data.runtime)
        movieDetailsBinding.tvWatchTimeDetails?.text = movieTime
        movieDetailsBinding.tvStatusDetails?.text = "Status: ${data.status}"
        GlobalScope.launch {
            val languages: String = getLanguagesString(data.spoken_languages)
            withContext(Dispatchers.Main) {
                movieDetailsBinding.tvLanguageDetails?.text = "Languages: $languages"
            }
        }
        try {
            val voteAverage = data.vote_average.toString().replace(".", "")
            movieDetailsBinding.tvAverageVoteDetails?.text = "$voteAverage%"
            val voteInteger = Integer.parseInt(voteAverage)
            if (voteInteger == 0) {
                movieDetailsBinding.pbAverageVoteDeatils?.progress = 0
                movieDetailsBinding.tvAverageVoteDetails?.text = "NA"
            }
            movieDetailsBinding.pbAverageVoteDeatils?.progress = voteInteger
        } catch (e: Exception) {
            Log.d(TAG, "$e")
            movieDetailsBinding.pbAverageVoteDeatils?.progress = 0
            movieDetailsBinding.tvAverageVoteDetails?.text = "NA"
        }
        movieDetailsBinding.tvDescriptionDetails?.text = data.overview


    }

    private fun getLanguagesString(spokenLanguages: List<SpokenLanguage>): String {
        val maxLen = spokenLanguages.lastIndex
        val list = ArrayList(spokenLanguages)
        var languagesString = ""
        for (index in list.indices) {
            languagesString += if (index == maxLen) {
                "${spokenLanguages[index].name}"
            } else {
                "${spokenLanguages[index].name}, "
            }
        }
        return languagesString
    }

    private fun getMovieTimeIntoMinuteAndHour(runtime: Int): String {
        val startTime = "00:00"
        val h = runtime / 60 + Integer.parseInt(startTime.substring(0, 1))
        val m = runtime % 60 + Integer.parseInt(startTime.substring(3, 4))
        return "${h}h ${m}m"
    }

    private fun getGeneresString(genres: List<Genre>): String {
        val maxLen = genres.lastIndex
        val list = ArrayList(genres)
        var genericString = ""
        for (index in list.indices) {
            genericString += if (index == maxLen) {
                "${genres[index].name}"
            } else {
                "${genres[index].name}, "
            }
        }
        return genericString
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        movieDetailsBinding = FragmentMovieDetailsBinding.inflate(layoutInflater)
        return movieDetailsBinding.root
    }

    override fun onVideoWatchClicked(result: ResultX) {
        val videoId = result.key
        val intent =
            YouTubeStandalonePlayer.createVideoIntent(
                (activity as MainActivity),
                YOUTUBE_API_KEY,
                videoId,
                0,
                true,
                true
            )
        startActivity(intent)

    }

    override fun onClickedRecommendation(result: Result) {
        val id = result.id.toLong()
        val action = MovieNavDirections.actionGlobalMovieDetailsFragment(result)
        (activity as MainActivity).navController.navigate(action)
    }

}