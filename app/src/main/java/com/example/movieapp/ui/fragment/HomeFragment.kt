package com.example.movieapp.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.movieapp.MainActivity
import com.example.movieapp.MovieNavDirections
import com.example.movieapp.databinding.FragmentHomeBinding
import com.example.movieapp.view_model.MovieViewModel
import com.example.movieapp.adapter.MovieAdapter
import com.example.movieapp.adapter.MovieTap
import com.example.movieapp.model.Result
import com.example.movieapp.util.HideKeyboard
import com.example.movieapp.util.Resources
import com.google.android.material.snackbar.Snackbar


class HomeFragment : Fragment(), MovieTap {

    private val TAG: String = "HomeFragment"
    private lateinit var homeBinding: FragmentHomeBinding
    private lateinit var viewModel: MovieViewModel
    private lateinit var trendingMovieAdapter: MovieAdapter
    private lateinit var popularMovieAdapter: MovieAdapter
    private lateinit var topRatedMovieAdapter: MovieAdapter
    private lateinit var nowPlayingMovieAdapter: MovieAdapter

    override fun onStart() {
        super.onStart()
        (activity as MainActivity).mainBinding.searchMovieInput.clearFocus()
        HideKeyboard.hideKeyboard(activity as MainActivity)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).mainBinding.cvSearchView.visibility = View.VISIBLE
        (activity as MainActivity).mainBinding.movieBottomNav.visibility = View.VISIBLE
        // we are getting ViewModel instance from the MainActivity because we are already
        // initialized ViewModel in Main Activity
        viewModel = (activity as MainActivity).viewModel
        //we are setting up recycler view adapter
        setUpTrendingRecyclerView()
        setUpTopRateRecyclerView()
        setUpNowPlayingRecyclerView()
        setUpPoplarRecyclerView()
        //assigned the call list page number value because when we rotate screen the page value
        // has increase but we won't want to increase page value
        viewModel.trendingPage = 1
        viewModel.nowPlayingPage = 1
        viewModel.popularPage = 1
        viewModel.topRatedPage = 1
        viewModel.searchPage = 1
        // we are calling the function because we have to show on the screen (just live trending,
        // popular, top rated, now playing movies)
        viewModel.getTrendingMovies()
        viewModel.getPopularMovies()
        viewModel.getTopRatedMovies()
        viewModel.getNowPlaying()
        //we are getting the list of movie
        viewModel.getTrendingMoviesList.observe(viewLifecycleOwner, { response->
            Log.d(TAG,"Trending observer call")
            when(response){
                is Resources.Success ->{
                    hideTrendingProgressBar()
                    response.data?.let { movieResponse ->
                        trendingMovieAdapter.differ.submitList(movieResponse.results.toList())
                    }
                }
                is Resources.Error ->{
                    hideTrendingProgressBar()
                    response.message?.let{ message ->
                        Snackbar.make(view,"$message", Snackbar.LENGTH_LONG).show()
                    }
                }
                is Resources.Loading ->{
                    showTrendingProgressBar()
                }
            }
        })
        viewModel.getPopularMoviesList.observe(viewLifecycleOwner, { response->
            Log.d(TAG,"Popular observer call")
            when(response){
                is Resources.Success ->{
                    hidePopularProgressBar()
                    response.data?.let { movieResponse ->
                        popularMovieAdapter.differ.submitList(movieResponse.results.toList())
                    }
                }
                is Resources.Error ->{
                    hidePopularProgressBar()
                    response.message?.let{ message ->
                        Snackbar.make(view,"$message", Snackbar.LENGTH_LONG).show()
                    }
                }
                is Resources.Loading ->{
                    showPopularProgressBar()
                }
            }
        })
        viewModel.getTopRatedMoviesList.observe(viewLifecycleOwner, { response->
            Log.d(TAG,"Top observer call")
            when(response){
                is Resources.Success ->{
                    hideTopRateProgressBar()
                    response.data?.let { movieResponse ->
                        topRatedMovieAdapter.differ.submitList(movieResponse.results.toList())
                    }
                }
                is Resources.Error ->{
                    hideTopRateProgressBar()
                    response.message?.let{ message ->
                        Snackbar.make(view,"$message", Snackbar.LENGTH_LONG).show()
                    }
                }
                is Resources.Loading ->{
                    showTopRateProgressBar()
                }
            }
        })
        viewModel.getNowPlayingMoviesList.observe(viewLifecycleOwner, { response->
            Log.d(TAG,"Now Playing observer call")
            when(response){
                is Resources.Success ->{
                    hideNowPlayingProgressBar()
                    response.data?.let { movieResponse ->
                        nowPlayingMovieAdapter.differ.submitList(movieResponse.results.toList())
                    }
                }
                is Resources.Error ->{
                    hideNowPlayingProgressBar()
                    response.message?.let{ message ->
                        Snackbar.make(view,"$message", Snackbar.LENGTH_LONG).show()
                    }
                }
                is Resources.Loading ->{
                    showNowPlayingProgressBar()
                }
            }
        })
        // we are handle click event if see more movies
        homeBinding.tvTrendingSeeMore.setOnClickListener {
            val action = MovieNavDirections.actionGlobalSeeMoreMovieFragment("trending")
            (activity as MainActivity).navController.navigate(action)
        }
        homeBinding.tvPopularSeeMore.setOnClickListener {
            val action = MovieNavDirections.actionGlobalSeeMoreMovieFragment("popular")
            (activity as MainActivity).navController.navigate(action)
        }
        homeBinding.tvTopRateSeeMore.setOnClickListener {
            val action = MovieNavDirections.actionGlobalSeeMoreMovieFragment("top_rated")
            (activity as MainActivity).navController.navigate(action)
        }
        homeBinding.tvNowPlayingSeeMore.setOnClickListener {
            val action = MovieNavDirections.actionGlobalSeeMoreMovieFragment("now_playing")
            (activity as MainActivity).navController.navigate(action)
        }

    }

    private fun setUpTrendingRecyclerView() {
        trendingMovieAdapter = MovieAdapter(this)
        homeBinding.rvTrendingMovie.adapter = trendingMovieAdapter
        homeBinding.rvTrendingMovie.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
    }
    private fun setUpPoplarRecyclerView() {
        popularMovieAdapter = MovieAdapter(this)
        homeBinding.rvPopularMovie.adapter = popularMovieAdapter
        homeBinding.rvPopularMovie.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
    }
    private fun setUpTopRateRecyclerView() {
        topRatedMovieAdapter = MovieAdapter(this)
        homeBinding.rvTopRateMovie.adapter = topRatedMovieAdapter
        homeBinding.rvTopRateMovie.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
    }
    private fun setUpNowPlayingRecyclerView() {
        nowPlayingMovieAdapter = MovieAdapter(this)
        homeBinding.rvNowPlayingMovie.adapter = nowPlayingMovieAdapter
        homeBinding.rvNowPlayingMovie.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
    }

    private fun hideTrendingProgressBar() {
        homeBinding.trendingProgressBar.visibility = View.INVISIBLE
    }
    private fun showTrendingProgressBar() {
        homeBinding.trendingProgressBar.visibility = View.INVISIBLE
    }
    private fun hidePopularProgressBar() {
        homeBinding.popularProgressBar.visibility = View.INVISIBLE
    }
    private fun showPopularProgressBar() {
        homeBinding.popularProgressBar.visibility = View.INVISIBLE
    }
    private fun hideTopRateProgressBar() {
        homeBinding.topRateProgressBar.visibility = View.INVISIBLE
    }
    private fun showTopRateProgressBar() {
        homeBinding.topRateProgressBar.visibility = View.INVISIBLE
    }
    private fun hideNowPlayingProgressBar() {
        homeBinding.nowPlayingProgressBar.visibility = View.INVISIBLE
    }
    private fun showNowPlayingProgressBar() {
        homeBinding.nowPlayingProgressBar.visibility = View.INVISIBLE
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeBinding = FragmentHomeBinding.inflate(layoutInflater)
        return homeBinding.root
    }

    override fun movieTap(result: Result) {
        val id: Long = result.id.toLong()
        if(id != null){
            val action = MovieNavDirections.actionGlobalMovieDetailsFragment(result)
            (activity as MainActivity).navController.navigate(action)
        }
    }


}