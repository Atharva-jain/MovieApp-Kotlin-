package com.example.movieapp.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.movieapp.MainActivity
import com.example.movieapp.MovieNavDirections
import com.example.movieapp.adapter.SearchMovieAdapter
import com.example.movieapp.adapter.SearchMovieHasClicked
import com.example.movieapp.databinding.FragmentSearchMovieBinding
import com.example.movieapp.listener.EndlessRecyclerViewScrollListener
import com.example.movieapp.model.Result
import com.example.movieapp.util.Constants.Companion.SEARCH_MOVIE_TIME_DELAY
import com.example.movieapp.util.HideKeyboard
import com.example.movieapp.util.Resources
import com.example.movieapp.view_model.MovieViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class SearchMovieFragment : Fragment(), SearchMovieHasClicked {

    private lateinit var scrollListener: EndlessRecyclerViewScrollListener
    private val TAG: String = "SearchViewFragment"
    private lateinit var searchMovieBinding: FragmentSearchMovieBinding
    private lateinit var searchMovieAdapter: SearchMovieAdapter
    private lateinit var viewModel: MovieViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()
        (activity as MainActivity).mainBinding.cvSearchView.visibility = View.VISIBLE
        (activity as MainActivity).mainBinding.movieBottomNav.visibility = View.VISIBLE
        viewModel = (activity as MainActivity).viewModel
        var job: Job? = null
        (activity as MainActivity).mainBinding.searchMovieText.addTextChangedListener { editable ->
            job?.cancel()
            job = MainScope().launch {
                delay(SEARCH_MOVIE_TIME_DELAY)
                editable?.let {
                    if(editable.toString().isNotEmpty()){
                        viewModel.searchPage = 1
                        viewModel.searchMovie(editable.toString())
                    }
                }
            }
        }
        viewModel.getSearchMoviesList.observe(viewLifecycleOwner, { response ->
            when(response){
                is Resources.Success ->{
                    hideSearchProgressBar()
                    response.data?.let { movieResponse ->
                        if(movieResponse.results.isNotEmpty()){
                            searchMovieBinding.cvSearchNotResult.visibility = View.GONE
                            searchMovieAdapter.differ.submitList(movieResponse.results.toList())
                        }else{
                            val list = movieResponse.results
                            list.clear()
                            searchMovieAdapter.differ.submitList(list)
                            searchMovieBinding.cvSearchNotResult.visibility = View.VISIBLE
                        }
                    }
                }
                is Resources.Error ->{
                    hideSearchProgressBar()
                    response.message?.let{ message ->
                        Snackbar.make(view,"$message", Snackbar.LENGTH_LONG).show()
                    }
                }
                is Resources.Loading ->{
                    showSearchProgressBar()
                }
            }
        })

    }

    private fun hideSearchProgressBar() {
        searchMovieBinding.searchProgressBar.visibility = View.INVISIBLE
    }
    private fun showSearchProgressBar() {
        searchMovieBinding.searchProgressBar.visibility = View.VISIBLE
    }

    private fun setUpRecyclerView() {
        searchMovieAdapter = SearchMovieAdapter(this)
        searchMovieBinding.rvSearchMovie.adapter = searchMovieAdapter
        val layoutManager = LinearLayoutManager((activity as MainActivity))
        searchMovieBinding.rvSearchMovie.layoutManager = layoutManager
        //implement scroll listener for getting the last position of RecyclerView
        scrollListener = object : EndlessRecyclerViewScrollListener(layoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                val query = (activity as MainActivity).mainBinding.searchMovieText.text.toString()
                viewModel.searchMovie(query)
            }
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if(newState == RecyclerView.SCROLL_STATE_DRAGGING){
                    HideKeyboard.hideKeyboard((activity as MainActivity))
                }
            }
        }
        searchMovieBinding.rvSearchMovie.addOnScrollListener(scrollListener)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        searchMovieBinding = FragmentSearchMovieBinding.inflate(layoutInflater)
        return searchMovieBinding.root
    }

    override fun searchMovieHasClicked(result: Result) {
        val id: Long = result.id.toLong()
        if(id != null){
            val action = MovieNavDirections.actionGlobalMovieDetailsFragment(result)
            (activity as MainActivity).navController.navigate(action)
        }
    }
}