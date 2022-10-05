package com.example.movieapp.ui.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.movieapp.MainActivity
import com.example.movieapp.R
import com.example.movieapp.databinding.FragmentSplashScreenBinding

class SplashScreen : Fragment() {

    private lateinit var splashScreenBinding: FragmentSplashScreenBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).mainBinding.cvSearchView.visibility = View.GONE
        (activity as MainActivity).mainBinding.movieBottomNav.visibility = View.GONE
        Handler(Looper.getMainLooper()).postDelayed({
            try {
                findNavController().navigate(R.id.action_splashScreen_to_homeFragment)
            }catch (e : Exception){
                Log.d("FragmentSplashScreen","does not navigate $e")
            }
        }, 3000)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        splashScreenBinding = FragmentSplashScreenBinding.inflate(layoutInflater)
        return splashScreenBinding.root
    }

}