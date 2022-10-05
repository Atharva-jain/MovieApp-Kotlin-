package com.example.movieapp
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.movieapp.databinding.ActivityMainBinding
import com.example.movieapp.repository.MovieRepository
import com.example.movieapp.view_model.MovieViewModel
import com.example.movieapp.view_model.MovieViewModelProviderFactory

class MainActivity : AppCompatActivity() {
    private val TAG: String = "MainActivity"
    lateinit var mainBinding: ActivityMainBinding
    lateinit var viewModel: MovieViewModel
    lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.movieFragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController
        mainBinding.movieBottomNav.setupWithNavController(navController)
        mainBinding.searchMovieInput.requestFocus()
        val repository = MovieRepository()
        val viewModelProviderFactory = MovieViewModelProviderFactory(application, repository)
        viewModel = ViewModelProvider(this, viewModelProviderFactory).get(MovieViewModel::class.java)
        mainBinding.searchMovieText.addTextChangedListener { editable ->
            val hasFocus = mainBinding.searchMovieInput.hasWindowFocus()
            val id  = navController.currentDestination?.id
            Log.d(TAG,"focus : $hasFocus")
            if(id != R.id.searchMovieFragment){
                if(mainBinding.searchMovieText.text.toString().isNotEmpty() && hasFocus){
                    val action = MovieNavDirections.actionGlobalSearchMovieFragment()
                    navController.navigate(action)
                }
            }
        }
    }
}
