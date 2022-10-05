package com.example.movieapp.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.movieapp.MainActivity
import com.example.movieapp.MovieNavDirections
import com.example.movieapp.R
import com.example.movieapp.adapter.OnClickedUpcoming
import com.example.movieapp.adapter.UpcomingAdapter
import com.example.movieapp.databinding.FragmentUpcomingBinding
import com.example.movieapp.listener.EndlessRecyclerViewScrollListener
import com.example.movieapp.model.Result
import com.example.movieapp.util.HideKeyboard
import com.example.movieapp.util.Resources
import com.example.movieapp.view_model.MovieViewModel
import com.google.android.material.snackbar.Snackbar


class UpcomingFragment : Fragment(), OnClickedUpcoming {

    private lateinit var scrollListener: EndlessRecyclerViewScrollListener
    private val TAG: String = "UpcomingFragment"
    private lateinit var upcomingBinding: FragmentUpcomingBinding
    private lateinit var viewModel: MovieViewModel
    private lateinit var upcomingAdapter: UpcomingAdapter

    override fun onStart() {
        super.onStart()
        (activity as MainActivity).mainBinding.searchMovieInput.clearFocus()
        HideKeyboard.hideKeyboard(activity as MainActivity)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).mainBinding.cvSearchView.visibility = View.VISIBLE
        (activity as MainActivity).mainBinding.movieBottomNav.visibility = View.VISIBLE
        viewModel = (activity as MainActivity).viewModel
        viewModel.getUpcomingMovie()
        setUpRecyclerView()
        viewModel.getUpcomingMovieList.observe(viewLifecycleOwner, { response ->
            Log.d(TAG,"Upcoming observer call")
            when(response){
                is Resources.Success ->{
                    hideUpcomingProgressBar()
                    response.data?.let { movieResponse ->
                        upcomingAdapter.differ.submitList(movieResponse.results.toList())
                    }
                }
                is Resources.Error ->{
                    hideUpcomingProgressBar()
                    response.message?.let{ message ->
                        Snackbar.make(view,"$message", Snackbar.LENGTH_LONG).show()
                    }
                }
                is Resources.Loading ->{
                    showUpcomingProgressBar()
                }
            }
        })
    }

    private fun hideUpcomingProgressBar(){
        upcomingBinding.upcomingProgressBar.visibility = View.INVISIBLE
    }

    private fun showUpcomingProgressBar(){
        upcomingBinding.upcomingProgressBar.visibility = View.VISIBLE
    }

    private fun setUpRecyclerView() {
        upcomingAdapter = UpcomingAdapter(this)
        upcomingBinding.rvUpcoming.adapter = upcomingAdapter
        val layoutManager = LinearLayoutManager((activity as MainActivity))
        upcomingBinding.rvUpcoming.layoutManager = layoutManager
        //implement scroll listener for getting the last position of RecyclerView
        scrollListener = object : EndlessRecyclerViewScrollListener(layoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                viewModel.getUpcomingMovie()
            }
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if(newState == RecyclerView.SCROLL_STATE_DRAGGING){
                    HideKeyboard.hideKeyboard((activity as MainActivity))
                }
            }
        }
        upcomingBinding.rvUpcoming.addOnScrollListener(scrollListener)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        upcomingBinding = FragmentUpcomingBinding.inflate(layoutInflater)
        return upcomingBinding.root
    }

    override fun onClickedUpcoming(result: Result) {
        val id: Long = result.id.toLong()
        if(id != null){
            val action = MovieNavDirections.actionGlobalMovieDetailsFragment(result)
            (activity as MainActivity).navController.navigate(action)
        }
    }


}