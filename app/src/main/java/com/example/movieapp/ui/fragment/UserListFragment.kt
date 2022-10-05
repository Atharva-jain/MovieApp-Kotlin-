package com.example.movieapp.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.movieapp.MainActivity
import com.example.movieapp.MovieNavDirections
import com.example.movieapp.R
import com.example.movieapp.adapter.OnClickedListMovie
import com.example.movieapp.adapter.UserListAdapter
import com.example.movieapp.databinding.FragmentUserListBinding
import com.example.movieapp.model.Result
import com.example.movieapp.view_model.MovieViewModel
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query


class UserListFragment : Fragment(), OnClickedListMovie {

    private val TAG: String = "UserListFragment"
    private var query: Query? = null
    private lateinit var userListBinding: FragmentUserListBinding
    private lateinit var userListAdapter: UserListAdapter
    private val args by navArgs<UserListFragmentArgs>()
    private var userType = ""
    private lateinit var mViewModel: MovieViewModel
    private val firebaseInstance = FirebaseFirestore.getInstance()
    private val historyCollection = firebaseInstance.collection("history")
    private val favoriteCollection = firebaseInstance.collection("favorite")
    private val watchlistCollection = firebaseInstance.collection("watchlist")

    override fun onStart() {
        super.onStart()
        userListAdapter?.startListening()
    }

    override fun onStop() {
        super.onStop()
        userListAdapter?.startListening()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).mainBinding.cvSearchView.visibility = View.VISIBLE
        (activity as MainActivity).mainBinding.movieBottomNav.visibility = View.VISIBLE
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        userType = args.userListType
        Log.d(TAG, "User List fragment are called $uid")
        if (userType == "history") {
            Log.d(TAG, "history has set")
            query = historyCollection.whereEqualTo("userId", uid)
        }
        if (userType == "watchlist") {
            Log.d(TAG, "watchlist has set")
            query = watchlistCollection.whereEqualTo("userId", uid)
        }
        if (userType == "favorite") {
            Log.d(TAG, "favorite are set")
            query = favoriteCollection.whereEqualTo("userId", uid)
        }
        if (query != null) {
            Log.d(TAG, "Query are executed")
            val recyclerOptions =
                FirestoreRecyclerOptions.Builder<Result>().setQuery(query!!, Result::class.java)
                    .build()
            userListAdapter = UserListAdapter(recyclerOptions, this)
            userListBinding.rvUserList.adapter = userListAdapter
            userListAdapter.startListening()
        }
        userListBinding.rvUserList.layoutManager = LinearLayoutManager((activity as MainActivity))

        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                Log.d(TAG, "OnSwiped called")
                val position = viewHolder.adapterPosition
                val result = userListAdapter.snapshots[position]
                if (userType == "history") {
                    Log.d(TAG, "History Removed")
                    mViewModel.removeHistory(result)
                    Snackbar.make(view, "History Removed Successfully", Snackbar.LENGTH_LONG)
                        .apply {
                            setAction("UNDO") {
                                mViewModel.addHistory(result)
                            }
                            show()
                        }
                }
                if (userType == "watchlist") {
                    Log.d(TAG, "WatchList Removed")
                    mViewModel.removeWatchlist(result)
                    Snackbar.make(view, "Watchlist Removed Successfully", Snackbar.LENGTH_LONG)
                        .apply {
                            setAction("UNDO") {
                                mViewModel.addWatchlist(result)
                            }
                            show()
                        }
                }
                if (userType == "favorite") {
                    Log.d(TAG, "Favorite Removed")
                    mViewModel.removeFavorite(result)
                    Snackbar.make(view, "Favorite Removed Successfully", Snackbar.LENGTH_LONG)
                        .apply {
                            setAction("UNDO") {
                                mViewModel.addFavorite(result)
                            }
                            show()
                        }
                }
            }
        }
        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(userListBinding.rvUserList)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        userListBinding = FragmentUserListBinding.inflate(layoutInflater)
        mViewModel = (activity as MainActivity).viewModel
        return userListBinding.root
    }

    override fun onClickedListMovie(result: Result) {
        val id: Long = result.id.toLong()
        if (id != null) {
            val action = MovieNavDirections.actionGlobalMovieDetailsFragment(result)
            (activity as MainActivity).navController.navigate(action)
        }
    }

    override fun isListEmpty(value: Boolean) {
        Log.d(TAG, "List has Called $value")
        if (value) {
            Log.d(TAG, "List has get value $value")
            if (userType == "history") {
                Log.d(TAG, "List has set History")
                userListBinding.cvNoList.visibility = View.VISIBLE
                userListBinding.ivNoList.setImageResource(R.drawable.ic_history_profile)
                userListBinding.tvNoList.setText(R.string.no_history)
            }
            if (userType == "watchlist") {
                Log.d(TAG, "List has set Watchlist")
                userListBinding.cvNoList.visibility = View.VISIBLE
                userListBinding.ivNoList.setImageResource(R.drawable.ic_watchlist_profile)
                userListBinding.tvNoList.setText(R.string.no_Watchlist)
            }
            if (userType == "favorite") {
                Log.d(TAG, "List has set Favorite")
                userListBinding.cvNoList.visibility = View.VISIBLE
                userListBinding.ivNoList.setImageResource(R.drawable.ic_favorite)
                userListBinding.tvNoList.setText(R.string.no_favorite)
            }
        } else {
            Log.d(TAG, "List has hide $value")
            userListBinding.cvNoList.visibility = View.GONE
        }
    }


}