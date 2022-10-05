package com.example.movieapp.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.movieapp.MainActivity
import com.example.movieapp.MovieNavDirections
import com.example.movieapp.adapter.CategoryAdapter
import com.example.movieapp.adapter.OnClickCategory
import com.example.movieapp.databinding.FragmentCategoryBinding
import com.example.movieapp.model.GenreX
import com.example.movieapp.util.HideKeyboard
import com.example.movieapp.util.Resources
import com.example.movieapp.view_model.MovieViewModel

class CategoryFragment : Fragment(), OnClickCategory {

    private val TAG: String = "CategoryFragment"
    private lateinit var categoryBinding: FragmentCategoryBinding
    private lateinit var viewModel: MovieViewModel
    private lateinit var categoryAdapter: CategoryAdapter

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
        setUpRecyclerView()
        viewModel.getMovieCategory()
        viewModel.getMovieCategoryList.observe(viewLifecycleOwner, { response->
            when (response) {
                is Resources.Success -> {
                    val data = response.data
                    Log.d(TAG, "\n $data")
                    val genre = data?.genres
                    if(genre != null){
                        categoryAdapter.differ.submitList(genre)
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
    }

    private fun setUpRecyclerView() {
        categoryAdapter = CategoryAdapter(this)
        categoryBinding.rvCategory.adapter = categoryAdapter
        categoryBinding.rvCategory.layoutManager = LinearLayoutManager((activity as MainActivity))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        categoryBinding = FragmentCategoryBinding.inflate(layoutInflater)
        return categoryBinding.root
    }

    override fun onClickCategory(genre: GenreX) {
        val id  = genre.id.toString()
        val action = MovieNavDirections.actionGlobalSeeMoreMovieFragment(id)
        (activity as MainActivity).navController.navigate(action)
    }
}