package com.example.movieapp.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.movieapp.MainActivity
import com.example.movieapp.MovieNavDirections
import com.example.movieapp.R
import com.example.movieapp.adapter.MovieAdapter
import com.example.movieapp.adapter.MovieTap
import com.example.movieapp.adapter.SeeMoreMovieAdapter
import com.example.movieapp.adapter.SeeMoreMovieTap
import com.example.movieapp.databinding.FragmentSeeMoreMovieBinding
import com.example.movieapp.listener.EndlessRecyclerViewScrollListener
import com.example.movieapp.model.Result
import com.example.movieapp.util.HideKeyboard
import com.example.movieapp.util.Resources
import com.example.movieapp.view_model.MovieViewModel
import com.google.android.material.snackbar.Snackbar


class SeeMoreMovieFragment : Fragment(), MovieTap, SeeMoreMovieTap {

    private val TAG: String = "SeeMoreMovieFragment"
    private lateinit var seeMoreMovieBinding: FragmentSeeMoreMovieBinding
    lateinit var viewModel: MovieViewModel
    lateinit var movieAdapter: SeeMoreMovieAdapter
    lateinit var scrollListener: EndlessRecyclerViewScrollListener
    private var movieType = ""
    private val args by navArgs<SeeMoreMovieFragmentArgs>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).mainBinding.cvSearchView.visibility = View.VISIBLE
        (activity as MainActivity).mainBinding.movieBottomNav.visibility = View.VISIBLE
        movieType = args.movieType
        viewModel = (activity as MainActivity).viewModel
        setUpRecyclerView()
        viewModel.trendingPage = 1
        viewModel.nowPlayingPage = 1
        viewModel.topRatedPage = 1
        viewModel.popularPage = 1
        viewModel.genrePage = 1
        when (movieType) {
            "trending" -> {
                viewModel.getTrendingMovies()
            }
            "popular" -> {
                viewModel.getPopularMovies()
            }
            "top_rated" -> {
                viewModel.getTopRatedMovies()
            }
            "now_playing" -> {
                viewModel.getNowPlaying()
            }
            else -> {
                try {
                    val id = Integer.parseInt(movieType)
                    viewModel.getGenreMovie(id)
                }catch (e: Exception){
                    Log.d(TAG,"Id are not converted :- $e")
                }
            }
        }

        //we are getting the list of movie
        viewModel.getTrendingMoviesList.observe(requireActivity(), { response ->
            Log.d(TAG, "Trending is get Data")
            when (response) {
                is Resources.Success -> {
                    hideProgressBar()
                    response.data?.let { movieResponse ->
                        movieAdapter.differ.submitList(movieResponse.results.toList())
                    }
                }
                is Resources.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        Snackbar.make(view, "$message", Snackbar.LENGTH_LONG).show()
                    }
                }
                is Resources.Loading -> {
                    showProgressBar()
                }
            }
        })
        viewModel.getPopularMoviesList.observe(viewLifecycleOwner, { response ->
            Log.d(TAG, "Popular is get Data")
            when (response) {
                is Resources.Success -> {
                    hideProgressBar()
                    response.data?.let { movieResponse ->
                        movieAdapter.differ.submitList(movieResponse.results.toList())
                    }
                }
                is Resources.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        Snackbar.make(view, "$message", Snackbar.LENGTH_LONG).show()
                    }
                }
                is Resources.Loading -> {
                    showProgressBar()
                }
            }
        })
        viewModel.getTopRatedMoviesList.observe(viewLifecycleOwner, { response ->
            Log.d(TAG, "Top Rated is get Data")
            when (response) {
                is Resources.Success -> {
                    hideProgressBar()
                    response.data?.let { movieResponse ->
                        movieAdapter.differ.submitList(movieResponse.results.toList())
                    }
                }
                is Resources.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        Snackbar.make(view, "$message", Snackbar.LENGTH_LONG).show()
                    }
                }
                is Resources.Loading -> {
                    showProgressBar()
                }
            }
        })
        viewModel.getNowPlayingMoviesList.observe(viewLifecycleOwner, { response ->
            Log.d(TAG, "Now Playing is get Data")
            when (response) {
                is Resources.Success -> {
                    hideProgressBar()
                    response.data?.let { movieResponse ->
                        movieAdapter.differ.submitList(movieResponse.results.toList())
                    }
                }
                is Resources.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        Snackbar.make(view, "$message", Snackbar.LENGTH_LONG).show()
                    }
                }
                is Resources.Loading -> {
                    showProgressBar()
                }
            }
        })
        viewModel.getGenreMovieList.observe(viewLifecycleOwner, { response ->
            Log.d(TAG, "Genre is get Data")
            when (response) {
                is Resources.Success -> {
                    hideProgressBar()
                    response.data?.let { movieResponse ->
                        movieAdapter.differ.submitList(movieResponse.results.toList())
                    }
                }
                is Resources.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        Snackbar.make(view, "$message", Snackbar.LENGTH_LONG).show()
                    }
                }
                is Resources.Loading -> {
                    showProgressBar()
                }
            }
        })
    }

    private fun hideProgressBar() {
        seeMoreMovieBinding.seeMoreProgressBar.visibility = View.INVISIBLE
    }

    private fun showProgressBar() {
        seeMoreMovieBinding.seeMoreProgressBar.visibility = View.VISIBLE
    }

    private fun setUpRecyclerView() {
        movieAdapter = SeeMoreMovieAdapter(this)
        seeMoreMovieBinding.rvSeeMoreMovie.adapter = movieAdapter
        val layoutManager =
            GridLayoutManager((activity as MainActivity), 2, GridLayoutManager.VERTICAL, false)
        seeMoreMovieBinding.rvSeeMoreMovie.layoutManager = layoutManager
        seeMoreMovieBinding.rvSeeMoreMovie.setHasFixedSize(true)
        //implement scroll listener for getting the last position of RecyclerView
        scrollListener = object : EndlessRecyclerViewScrollListener(layoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                when (movieType) {
                    "trending" -> {
                        viewModel.getTrendingMovies()
                    }
                    "popular" -> {
                        viewModel.getPopularMovies()
                    }
                    "top_rated" -> {
                        viewModel.getTopRatedMovies()
                    }
                    "now_playing" -> {
                        viewModel.getNowPlaying()
                    }
                    else -> {
                        try {
                            val id = Integer.parseInt(movieType)
                            viewModel.getGenreMovie(id)
                        }catch (e: Exception){
                            Log.d(TAG,"Id are not converted :- $e")
                        }
                    }
                }
            }
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if(newState == RecyclerView.SCROLL_STATE_DRAGGING){
                    HideKeyboard.hideKeyboard((activity as MainActivity))
                }
            }
        }
        seeMoreMovieBinding.rvSeeMoreMovie.addOnScrollListener(scrollListener)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        seeMoreMovieBinding = FragmentSeeMoreMovieBinding.inflate(layoutInflater)
        return seeMoreMovieBinding.root
    }

    override fun movieTap(result: Result) {
        val id: Long = result.id.toLong()
        if(id != null){
            val action = MovieNavDirections.actionGlobalMovieDetailsFragment(result)
            (activity as MainActivity).navController.navigate(action)
        }
    }
}