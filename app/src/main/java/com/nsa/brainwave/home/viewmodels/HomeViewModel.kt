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

class HomeViewModel: ViewModel() {


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

    private val _userDataModel: MutableLiveData<SubjectModel> = MutableLiveData()
    val userDataModel: LiveData<SubjectModel>
        get() = _userDataModel

    private val _examsList: MutableLiveData<List<Exam>> = MutableLiveData()
    val examsList: LiveData<List<Exam>>
        get() = _examsList

    private val _subjectsList: MutableLiveData<List<SubjectModel>> = MutableLiveData()
    val subjectsList: LiveData<List<SubjectModel>>
        get() = _subjectsList

    private val _testModel: MutableLiveData<Test> = MutableLiveData()
    val testModel: LiveData<Test>
        get() = _testModel

    private val _feedbackSubmitted: MutableLiveData<Boolean> = MutableLiveData()
    val feedbackSubmitted: LiveData<Boolean>
        get() = _feedbackSubmitted

     fun getUserData() {
        val docRef = db.collection("users").document(fUser!!.uid)
        _loading.value=true
         viewModelScope.launch(Dispatchers.IO) {
        docRef.get().addOnCompleteListener { task ->
            _loading.value=false
            if (task.isSuccessful) {
                val document = task.result
                if (document != null && document.exists()) {
                   _userModel.value=document.toObject<UserData>()!!.user!!
                    _userDataModel.value=document.toObject<UserData>()!!.lastSubjectModel!!
                    getAllSubjects(_userModel.value?.exam)
                } else {
                    _message.value="error, user not exist!"
                }
            } else {
              _message.value="error, try again later!"
            }

        }
         }
    }

    private fun getAllSubjects(exam: String?) {
        _loading.value=true

        viewModelScope.launch(Dispatchers.IO) {
            val docRef = db.collection("users").document(fUser!!.uid).collection("subjects")
            docRef.get().addOnCompleteListener { task ->
                _loading.value = false
                if (task.isSuccessful) {
                    val collection = task.result
                    if (collection != null && !collection.isEmpty) {
                        val subjectsList = arrayListOf<SubjectModel>()
                        collection.documents.forEach {
                            subjectsList.add(it.toObject<SubjectModel>()!!)
                        }
                        _subjectsList.value = subjectsList
                    } else {
                        addUserSubjects(exam)
                    }
                } else {
                    _message.value = "error, try again later!"
                }

            }
        }
    }

    private fun addUserSubjects(exam: String?) {
        _loading.value=true
        viewModelScope.launch(Dispatchers.IO) {
            realTimeDb.child("data")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {

                        viewModelScope.launch(Dispatchers.IO) {
                            Log.e("TAG", "onDataChange: ${dataSnapshot.value}")
                            val examsList = dataSnapshot.getValue<Exams>()?.exams!!

                            val subjectsList = arrayListOf<SubjectModel>()
                            examsList.find {
                                it.name == exam
                            }?.subjects?.forEach { sub ->
                                subjectsList.add(SubjectModel(sub, 0, 0, 0))
                            }

                            val referenceList = arrayListOf<DocumentReference>()

                            subjectsList.forEach {
                                referenceList.add(
                                    db.collection("users").document(fUser!!.uid)
                                        .collection("subjects")
                                        .document(it.name!!)
                                )
                            }
                            db.runBatch { batch ->
                                for (i in 0 until referenceList.size) {
                                    batch.set(referenceList[i], subjectsList[i])
                                }

                            }.addOnCompleteListener {
                                _loading.value = false
                                if (it.isSuccessful) {
                                    _subjectsList.value = subjectsList
                                } else {
                                    _message.value = it.exception?.message
                                }
                            }
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        // Getting Post failed, log a message
                        _loading.value = false
                        _message.value = databaseError.message
                        Log.e("TAG", "loadPost:onCancelled", databaseError.toException())
                    }
                })
        }
    }


    fun getUserExam(): String {
        return _userModel.value?.exam!!

    }

    fun getTestData(exam: String, subject: String, level: Int) {
//        val text="{\"exam\":\"JEE\",\"subject\":\"chemistry\",\"level\":5,\"questions\":[{\"question\":\"What is the formula for the compound sodium sulfide?\",\"options\":[\"Na2S\",\"NaS2\",\"Na2S2\",\"NaS\"],\"minimumTimeToSolve\":20,\"correctOption\":1},{\"question\":\"What is the charge of an atom in the ground state?\",\"options\":[\"Positive\",\"Negative\",\"Neutral\",\"Both Positive and Negative\"],\"minimumTimeToSolve\":30,\"correctOption\":2},{\"question\":\"What is the name of the reaction between an acid and a base?\",\"options\":[\"Neutralization\",\"Oxidation\",\"Reduction\",\"Hydrolysis\"],\"minimumTimeToSolve\":15,\"correctOption\":0},{\"question\":\"What is the name of the reaction between a metal and an acid?\",\"options\":[\"Neutralization\",\"Oxidation\",\"Reduction\",\"Hydrolysis\"],\"minimumTimeToSolve\":10,\"correctOption\":2},{\"question\":\"What is the name of the compound formed by the reaction between calcium oxide and water?\",\"options\":[\"Calcium hydroxide\",\"Calcium oxide\",\"Calcium carbonate\",\"Calcium sulfide\"],\"minimumTimeToSolve\":25,\"correctOption\":0}]}"
//        val first: Int = text.indexOf("{")
//        val last: Int = text.lastIndexOf("}") + 1
//        val json: String = text.substring(first, last)
//        _testModel.value=Gson().fromJson(json,Test::class.java)


        _loading.value=true
        viewModelScope.launch{
            val response = Repository().getCompleteionText(
                RequestModel(
                    max_tokens = 500,
                    temperature = 0.5,
                    model = "text-davinci-003",
                    prompt = "You need to create a compact JSON object with the following structure:" +
                            " The object should have the keys exam, subject, level, and questions. " +
                            "The exam and subject should be strings, level is a integer value between 1 to 100," +
                            " and the questions key should be an array of objects." +
                            " Each object in the questions array should have the keys question" +
                            " with actual random questions from the given subject and level, options," +
                            " minimumTimeToSolve, and correctOption. The options key should contain a list of 4 options." +
                            " The minimumTimeToSolve key should contain the minimum time in seconds required to solve " +
                            "the question. The correctOption key should contain a integer label indicating the" +
                            " correct option index from the options list and should be different for all questions." +
                            " Here exam is $exam, subject is $subject, level is $level and number of questions is 5." +
                            " Don't include line break(\n) or extra white spaces, just give a string version of json object"
                )
            )
            Log.e("TAG", "test_response $response: ")
            _loading.value = false
            when (response.status) {
                ApiResult.Status.ERROR -> {
                    _message.value = response.message!!
                }
                ApiResult.Status.SUCCESS -> {
                    if(!response?.data?.choices.isNullOrEmpty()) {
                        val text = response?.data!!.choices[0].text
                        val first: Int = text.indexOf("{")
                        val last: Int = text.lastIndexOf("}") + 1
                        val json: String = text.substring(first, last)
                        _testModel.value=Gson().fromJson(json,Test::class.java)
                    }
                }
                else -> {}
            }
     }

    }

    fun uploadUserImage(fileUri: Uri) {
        val userImageRef = storageRef.child("userImages/${fUser?.uid}.jpg")
        _loading.value=true
        viewModelScope.launch(Dispatchers.IO) {
            var uploadTask = userImageRef.putFile(fileUri)

            val urlTask = uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    Log.e("TAG", "uploadUserImage: ${task.result.bytesTransferred}", )
                    _loading.value=false
                    task.exception?.let {
                        _message.value="ImageLink Error\nTry Again!"
                        Log.e("TAG", "uploadUserImage: ${it.message}", )
                    }
                }
                userImageRef.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    updateFirebaseUserObject(downloadUri)
                }else {
                    _loading.value=false
                    _message.value="ImageLink Error\nTry Again!"
                }
            }
        }
    }

    private fun updateFirebaseUserObject(downloadUri: Uri) {
        val profileUpdates = userProfileChangeRequest {
            photoUri = downloadUri
        }
        fUser!!.updateProfile(profileUpdates)
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {
                    updateImageLinkInFirestore(downloadUri)

                } else {
                    _loading.value = false
                    _message.value = "${task.exception?.message}"
                }
            }
    }

    private fun updateImageLinkInFirestore(
        downloadUri: Uri
    ) {
        db.collection("users").document(fUser!!.uid)
            .update(
                mapOf(
                    "user.photoUrl" to downloadUri.toString()
                )
            ).addOnCompleteListener {
                _loading.value = false
                if (it.isSuccessful) {
                    getUserData()
                } else {
                    _message.value = "${it.exception?.message}"
                }
            }
    }

    fun submitFeedback(rating: String, feedback: String) {
        _loading.value=true
        viewModelScope.launch(Dispatchers.IO) {
            val feedback=Feedback(feedback,rating,System.currentTimeMillis())
            db.collection("feedback")
                .document(fUser!!.uid)
                .set(feedback)
                .addOnCompleteListener {
                    _loading.value=false
                    if(it.isSuccessful){
                        _feedbackSubmitted.value=true
                    }else{
                    _message.value="error ${it.exception!!.message}"
                    }
                }
        }
    }
}