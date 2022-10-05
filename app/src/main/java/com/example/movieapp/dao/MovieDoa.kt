package com.example.movieapp.dao


import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.movieapp.model.Result
import com.example.movieapp.model.User
import com.example.movieapp.util.HasInternetConnection
import com.example.movieapp.util.Resources
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await
import java.io.IOException

class MovieDoa {

    private val TAG: String = "MovieDoa"
    private val mAuth = FirebaseAuth.getInstance()
    private var mCurrentUser = mAuth.currentUser
    private val uId = mCurrentUser?.uid
    private val firebaseInstance = FirebaseFirestore.getInstance()
    private val fireStorageReference = FirebaseStorage.getInstance().reference
    private val userCollection = firebaseInstance.collection("users")
    private val historyCollection = firebaseInstance.collection("history")
    private val favoriteCollection = firebaseInstance.collection("favorite")
    private val watchlistCollection = firebaseInstance.collection("watchlist")

    val loginResult: MutableLiveData<Resources<Boolean>> = MutableLiveData()
    val userResult: MutableLiveData<Resources<Boolean>> = MutableLiveData()

    suspend fun loginInWithFirebase(idToken: String) {
        try {
            loginResult.postValue(Resources.Loading())
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            mAuth.signInWithCredential(credential).await()
            loginResult.postValue(Resources.Success(true))
        } catch (t: Throwable) {
            when (t) {
                is IOException -> loginResult.postValue(Resources.Error("Network Failure", false))
                else -> loginResult.postValue(
                    Resources.Error(
                        "Conversion Error ${t.message}",
                        false
                    )
                )
            }
        }
    }

    suspend fun addUserData(user: User) {
        try {
            userResult.postValue(Resources.Loading())
            userCollection.document(user.userId).set(user).await()
            userResult.postValue(Resources.Success(true))
        } catch (t: Throwable) {
            when (t) {
                is IOException -> userResult.postValue(Resources.Error("Network Failure", false))
                else -> userResult.postValue(
                    Resources.Error(
                        "Conversion Error ${t.message}",
                        false
                    )
                )
            }
        }
    }

    suspend fun getUserData(uId: String): DocumentSnapshot? {
        return userCollection.document(uId).get().await()
    }

    suspend fun updateUserName(uid: String, userName: String) {
        try {
            userCollection.document(uid).update("userName", userName).await()
        } catch (e: Exception) {
            Log.d(TAG, "userName Has not updated $e")
        }
    }

    suspend fun updateUserUrl(uid: String, userUrl: Uri, imageExtension: String) {
        try {
            val fileRef: StorageReference =
                fireStorageReference.child("${System.currentTimeMillis()}.$imageExtension")
            fileRef.putFile(userUrl).await()
            val uri = fileRef.downloadUrl.await().toString()
            userCollection.document(uid).update("userUrl", uri).await()
        } catch (e: Exception) {
            Log.d(TAG, "User Url are not updated $e")
        }
    }

    suspend fun isUserExist(uid: String): Boolean {
        return try {
            assignedCurrentUser()
            val data = userCollection.document(uid).get().await()
            data.exists()
        } catch (e: java.lang.Exception) {
            Log.d(TAG, "data are not getting $uid")
            false
        }
    }

    suspend fun addHistory(result: Result) {
        try {
            val referenceId = userCollection.document().id
            assignedCurrentUser()
            val uid = mCurrentUser?.uid
            Log.d(TAG, "Uid is $uid")
            if (uid != null) {
                result.userId = uid
                result.currentId = referenceId
                historyCollection.document(result.currentId).set(result).await()
            }
        } catch (e: Exception) {
            Log.d(TAG, "history are not added $e")
        }
    }

    suspend fun addFavorite(result: Result) {
        try {
            val referenceId = userCollection.document().id
            assignedCurrentUser()
            val uid = mCurrentUser?.uid
            Log.d(TAG, "Uid is $uid")
            if (uid != null) {
                result.userId = uid
                result.currentId = referenceId
                favoriteCollection.document(result.currentId).set(result).await()
            }
        } catch (e: Exception) {
            Log.d(TAG, "favorite are not added $e")
        }
    }

    suspend fun addWatchlist(result: Result) {
        try {
            val referenceId = userCollection.document().id
            assignedCurrentUser()
            val uid = mCurrentUser?.uid
            Log.d(TAG, "Uid is $uid")
            if (uid != null) {
                result.userId = uid
                result.currentId = referenceId
                watchlistCollection.document(result.currentId).set(result).await()
            }
        } catch (e: Exception) {
            Log.d(TAG, "watchlist are not added $e")
        }
    }

    suspend fun removeHistory(result: Result){
        try {
            assignedCurrentUser()
            val id = result.currentId
            historyCollection.document(id).delete().await()
        }catch (e: java.lang.Exception){
            Log.d(TAG, "History are not removed $e")
        }
    }

    suspend fun removeFavorite(result: Result){
        try {
            assignedCurrentUser()
            val id = result.currentId
            favoriteCollection.document(id).delete().await()
        }catch (e: java.lang.Exception){
            Log.d(TAG, "Favorite are not removed $e")
        }
    }

    suspend fun removeWatchlist(result: Result){
        try {
            assignedCurrentUser()
            val id = result.currentId
            watchlistCollection.document(id).delete().await()
        }catch (e: java.lang.Exception){
            Log.d(TAG, "Watchlist are not removed $e")
        }
    }

    private fun assignedCurrentUser(){
        mCurrentUser?.reload()
        mCurrentUser = FirebaseAuth.getInstance().currentUser
    }
}