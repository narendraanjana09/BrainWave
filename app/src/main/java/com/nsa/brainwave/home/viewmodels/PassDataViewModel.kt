package com.nsa.brainwave.home.viewmodels

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.google.gson.Gson
import com.nsa.brainwave.Login.models.SubjectModel
import com.nsa.brainwave.Login.models.User
import com.nsa.brainwave.Login.models.UserData
import com.nsa.brainwave.Login.models.exams.Exam
import com.nsa.brainwave.Login.models.exams.Exams
import com.nsa.brainwave.home.api.ApiResult
import com.nsa.brainwave.home.models.Api.RequestModel
import com.nsa.brainwave.home.models.Test.Test
import com.nsa.brainwave.home.models.rating.Feedback
import com.nsa.brainwave.home.repository.Repository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PassDataViewModel: ViewModel() {


    private val db = Firebase.firestore
    val storageRef = Firebase.storage.reference
    private val realTimeDb = Firebase.database.reference
    private val fUser= Firebase.auth.currentUser

    private val _loading: MutableLiveData<Boolean> = MutableLiveData(false)
    val loading: LiveData<Boolean>
        get() = _loading

    private val _message: MutableLiveData<String> = MutableLiveData()
    val message: LiveData<String>
        get() = _message

    private val _userModel: MutableLiveData<User> = MutableLiveData()
    val userModel: LiveData<User>
        get() = _userModel

    private val _ad_dismissed: MutableLiveData<Boolean> = MutableLiveData(false)
    val ad_dismissed: LiveData<Boolean>
        get() = _ad_dismissed


    fun setAdDismissed(value:Boolean){
        _ad_dismissed.value=value
    }





}