package com.example.movieapp.ui.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.example.movieapp.MainActivity
import com.example.movieapp.MovieNavDirections
import com.example.movieapp.R
import com.example.movieapp.databinding.FragmentProfileBinding
import com.example.movieapp.model.User
import com.example.movieapp.util.GetImageExtension
import com.example.movieapp.util.HideKeyboard
import com.example.movieapp.util.Resources
import com.example.movieapp.view_model.MovieViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser



class ProfileFragment : Fragment() {

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var profileBinding: FragmentProfileBinding
    private val TAG: String = "GoogleSignInFragment"
    private lateinit var mAuth: FirebaseAuth
    private var mCurrentUser: FirebaseUser? = null
    private lateinit var viewModel: MovieViewModel
    private lateinit var editUsernameText: TextInputEditText


    override fun onStart() {
        super.onStart()
        (activity as MainActivity).mainBinding.searchMovieInput.clearFocus()
        HideKeyboard.hideKeyboard(activity as MainActivity)
        if (isCurrentUserIsAvailable() == null) {
            Log.d(TAG,"User Is Null")
            profileBinding.btSignIn.visibility = View.VISIBLE
            profileBinding.cvProfile.visibility = View.GONE
        } else {
            Log.d(TAG,"User Is Not Null")
            val uid = mCurrentUser?.uid
            if(uid != null){
                viewModel.getUser(uid)
            }
            profileBinding.btSignIn.visibility = View.GONE
            profileBinding.cvProfile.visibility = View.VISIBLE
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Configure Google Sign In
        (activity as MainActivity).mainBinding.cvSearchView.visibility = View.VISIBLE
        (activity as MainActivity).mainBinding.movieBottomNav.visibility = View.VISIBLE
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient((activity as MainActivity), gso)

        viewModel.isUserExist.observe(viewLifecycleOwner,{ value ->
            if(value){
                isCurrentUserIsAvailable()
                val uid = mCurrentUser?.uid
                if(uid != null){
                    hideProfileProgressBar()
                    viewModel.getUser(uid)
                }
            }else{
                isCurrentUserIsAvailable()
                val id = mCurrentUser?.uid
                val name = mCurrentUser?.displayName
                val email = mCurrentUser?.email
                val url = mCurrentUser?.photoUrl.toString()
                if (id != null && name != null && email != null && url != null) {
                    val user = User(id, name, email, url)
                    viewModel.addUser(user)
                }
            }
        })

        viewModel.loginResult.observe(viewLifecycleOwner, { response ->
            Log.d(TAG,"Login Result Observer Has Called")
            when (response) {
                is Resources.Success -> {
                    isCurrentUserIsAvailable()
                    Log.d("athrva","Login Result Observer Has Succeed")
                    val id = mCurrentUser?.uid
                    if(id != null){
                        viewModel.isUserExist(id)
                    }
                }
                is Resources.Error -> {
                    hideProfileProgressBar()
                    Log.d(TAG,"Login Result Observer Has Found Error")
                    response.message?.let { message ->
                        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show()
                    }
                }
                is Resources.Loading -> {
                    Log.d(TAG,"Login Result Observer Has Loaded")
                    showProfileProgressBar()
                }
            }
        })

        viewModel.userResult.observe(viewLifecycleOwner, { response ->
            Log.d(TAG,"User Result Observer Has called")
            when (response) {
                is Resources.Success -> {
                    Log.d(TAG,"Login Result Observer Has Succeed")
                    hideProfileProgressBar()
                    updateUI()
                }
                is Resources.Error -> {
                    hideProfileProgressBar()
                    Log.d(TAG,"Login Result Observer Has Error")
                    response.message?.let { message ->
                        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show()
                    }
                }
                is Resources.Loading -> {
                    Log.d(TAG,"Login Result Observer Has Loaded")
                    showProfileProgressBar()
                }
            }
        })

        viewModel.userData.observe(viewLifecycleOwner,{ user ->
            Log.d(TAG,"Login Result Observer Has Called")
            if(user.exists()){
                val currentUser = user.toObject(User::class.java)
                if(currentUser != null){
                    Log.d(TAG,"Login Result Observer Has get Data")
                    updateUIForLogin(isCurrentUserIsAvailable())
                    profileBinding.tvProfileName.text = currentUser.userName
                    profileBinding.tvProfileEmail.text = currentUser.userEmail
                    Glide.with(profileBinding.ivProfilePic.context).load(currentUser.userUrl).circleCrop().into(profileBinding.ivProfilePic)
                }
            }else{
                Log.d(TAG,"Login Result Observer Has not get data")
                Toast.makeText(
                    (activity as MainActivity),
                    "user are not available",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

        profileBinding.btSignIn.setOnClickListener {
            Log.d(TAG,"sign in button called")
            signIn()
        }

        profileBinding.cvLogout.setOnClickListener {
            isCurrentUserIsAvailable()
            Log.d(TAG,"Login out Called")
            if(mCurrentUser != null){
                mAuth.signOut()
                googleSignInClient.signOut()
                updateUIForLogin(isCurrentUserIsAvailable())
            }
        }

        profileBinding.ivEditProfileImage.setOnClickListener{
            val galleryIntent = Intent()
            galleryIntent.action = Intent.ACTION_GET_CONTENT
            galleryIntent.type = "image/*"
            galleryResultLauncher.launch(galleryIntent)
        }

        profileBinding.ivEditProfileName.setOnClickListener {
            createEditUsernameAlertDialogBox()
        }

        profileBinding.cvFavorite.setOnClickListener {
            val userType = "favorite"
            val action = MovieNavDirections.actionGlobalUserListFragment(userType)
            (activity as MainActivity).navController.navigate(action)
        }

        profileBinding.cvHistory.setOnClickListener {
            val userType = "history"
            val action = MovieNavDirections.actionGlobalUserListFragment(userType)
            (activity as MainActivity).navController.navigate(action)
        }

        profileBinding.cvWatchList.setOnClickListener {
            val userType = "watchlist"
            val action = MovieNavDirections.actionGlobalUserListFragment(userType)
            (activity as MainActivity).navController.navigate(action)
        }
    }

    private val galleryResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        if(result.resultCode == Activity.RESULT_OK){
            val data = result.data
            val uri = data?.data
            if(uri != null){
                val imageExtension = GetImageExtension.getFileExtension((activity as MainActivity), uri)
                if (imageExtension != null){
                    isCurrentUserIsAvailable()
                    val uid = mCurrentUser?.uid
                    if(uid != null){
                        viewModel.updateUserUrl(uid, uri, imageExtension)
                        Glide.with(profileBinding.ivProfilePic.context).load(uri).circleCrop().into(profileBinding.ivProfilePic)
                    }
                }
            }
        }
    }

    private fun createEditUsernameAlertDialogBox(){
        val builder = AlertDialog.Builder((activity as MainActivity))
        builder.setTitle("Change Username")
        val view = layoutInflater.inflate(R.layout.edit_profile_username_layout, null)
        builder.setView(view)
        editUsernameText = view.findViewById(R.id.editUsernameInput)
        builder.setPositiveButton("Change", DialogInterface.OnClickListener { dialog, id ->
            if(!editUsernameText.text.isNullOrEmpty()){
                val uid = mCurrentUser?.uid
                if(uid != null){
                    viewModel.updateUserName(uid, editUsernameText.text.toString())
                    profileBinding.tvProfileName.text = editUsernameText.text.toString()
                }
            }
        })
        builder.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, id ->
            dialog.dismiss()
        })
        val dialog = builder.create()
        dialog.show()
    }

    private fun updateUI() {
        isCurrentUserIsAvailable()
        Log.d(TAG,"Update UI has called")
        val uid = mCurrentUser?.uid
        if (uid != null) {
            Log.d(TAG,"Update Ui has Get Uid")
            viewModel.getUser(uid)
        }
    }

    private fun showProfileProgressBar() {
        Log.d(TAG,"Progress bar are visible")
        profileBinding.profileLayout.isEnabled = false
        profileBinding.profileProgress.visibility = View.VISIBLE
    }

    private fun hideProfileProgressBar() {
        Log.d(TAG,"Progress bar are not visible")
        profileBinding.profileLayout.isEnabled = true
        profileBinding.profileProgress.visibility = View.INVISIBLE
    }


    private fun updateUIForLogin(currentUser: FirebaseUser?) {
        if (currentUser != null) {
            Log.d(TAG,"User are logged in UpdateUIForLogin")
            profileBinding.btSignIn.visibility = View.GONE
            profileBinding.cvProfile.visibility = View.VISIBLE
        } else {
            Log.d(TAG,"User are not logged in UpdateUIForLogin")
            profileBinding.btSignIn.visibility = View.VISIBLE
            profileBinding.cvProfile.visibility = View.GONE
        }
    }

    private fun signIn() {
        Log.d(TAG,"sign in intent are called")
        val signInIntent = googleSignInClient.signInIntent
        resultLauncher.launch(signInIntent)
    }

    private fun isCurrentUserIsAvailable(): FirebaseUser? {
        mCurrentUser = mAuth.currentUser
        return mCurrentUser
    }

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                val data: Intent? = result.data
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    val account = task.getResult(ApiException::class.java)!!
                    Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                    val token = account.idToken
                    if (token != null) {
                        Log.d(TAG,"View model login function are called")
                        viewModel.login(token)
                    }
                } catch (e: ApiException) {
                    // Google Sign In failed, update UI appropriately
                    Log.w(TAG, "Google sign in failed", e)
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        profileBinding = FragmentProfileBinding.inflate(layoutInflater)
        mAuth = FirebaseAuth.getInstance()
        mCurrentUser = mAuth.currentUser
        viewModel = (activity as MainActivity).viewModel
        return profileBinding.root
    }
}