package com.nsa.brainwave.home.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.nsa.brainwave.Login.models.SubjectModel
import com.nsa.brainwave.Login.models.UserData
import com.nsa.brainwave.home.models.Test.Question
import com.nsa.brainwave.home.models.Test.Test
import com.nsa.brainwave.util.TestReport
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TestViewModel: ViewModel() {


    private val db = Firebase.firestore
    private val realTimeDb = Firebase.database.reference
    private val fUser= Firebase.auth.currentUser

    private val _loading: MutableLiveData<Boolean> = MutableLiveData(false)
    val loading: LiveData<Boolean>
        get() = _loading

    private val _message: MutableLiveData<String> = MutableLiveData()
    val message: LiveData<String>
        get() = _message

    private val _currentQuestion: MutableLiveData<Question> = MutableLiveData()
    val currentQuestion: LiveData<Question>
        get() = _currentQuestion

    private val _testModel: MutableLiveData<Test> = MutableLiveData()
    val testModel: LiveData<Test>
        get() = _testModel

    private val _questionNo: MutableLiveData<Int> = MutableLiveData(0)
    val questionNo: LiveData<Int>
        get() = _questionNo

    private val _reportModel: MutableLiveData<com.nsa.brainwave.home.models.Report.TestReport> = MutableLiveData()
    val reportModel: LiveData<com.nsa.brainwave.home.models.Report.TestReport>
        get() = _reportModel

    private val _previousTestList: MutableLiveData<List<com.nsa.brainwave.home.models.Report.TestReport>> = MutableLiveData()
    val previousTestList: LiveData<List<com.nsa.brainwave.home.models.Report.TestReport>>
        get() = _previousTestList



    fun setTest(test: Test){
        _testModel.value=test
    }

    fun showQuetion(i: Int) {
        _questionNo.value=i
        _currentQuestion.value= _testModel.value!!.questions?.get(i)
    }

    fun optionSelected(position: Int) {
        _testModel.value!!.questions?.get(_questionNo.value!!)?.selectedOption =position
        showQuetion(_questionNo.value!!)
    }

    fun finishTest(time: Int, test: Test) {
        _loading.value=true
        viewModelScope.launch(Dispatchers.IO){
            //getting test Report
            val report = TestReport.getTestReport(time, test)

            //for adding question data to main user object
            val userRef = db.collection("users").document(fUser!!.uid)


            //for increasing attended question value to that subjact
           val subjectRef=  db.collection("users")
                .document(fUser!!.uid)
                .collection("subjects")
                .document(test.subject!!)

            //for adding test details
            val testRef = subjectRef
                .collection("tests")
                .document(report.reportModel!!.date.toString())


            //getting user data
            userRef.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document = task.result
                    if (document != null && document.exists()) {

                        //updating user data
                        val userData=document.toObject<UserData>()
                        userData.let {
                            it?.lastSubjectModel?.missed = it?.lastSubjectModel?.missed?.plus(report.reportModel!!.missed!!)
                            it?.lastSubjectModel?.totalAttended = it?.lastSubjectModel?.totalAttended?.plus(
                                report.reportModel!!.totalQuestion!!
                            )
                            it?.lastSubjectModel?.correctSolved = it?.lastSubjectModel?.correctSolved?.plus(
                                report.reportModel!!.correct!!
                            )
                        }
                        //uploading userdata to firestore
                        userRef.set(userData!!).addOnCompleteListener {
                            if(it.isSuccessful){

                                //getting subject data
                                subjectRef.get()
                                    .addOnCompleteListener {
                                        if(it.isSuccessful){

                                            //updating subject model
                                            val subjectModel=it.result.toObject<SubjectModel>()
                                            subjectModel?.missed = subjectModel?.missed?.plus(report.reportModel!!.missed!!)
                                            subjectModel?.totalAttended = subjectModel?.totalAttended?.plus(
                                                report.reportModel!!.totalQuestion!!
                                            )
                                            subjectModel?.correctSolved = subjectModel?.correctSolved?.plus(
                                                report.reportModel!!.correct!!
                                            )

                                            //uploading subject data
                                            subjectRef.set(subjectModel!!)
                                                .addOnCompleteListener {
                                                    if (it.isSuccessful) {
                                                        //uploading test data
                                                        testRef.set(report)
                                                            .addOnCompleteListener {
                                                                _loading.value=false
                                                                if (it.isSuccessful) {
                                                                    _reportModel.value = report
                                                                } else {
                                                                    _message.value = it.exception?.message
                                                                }
                                                            }
                                                    } else {
                                                        _loading.value=false
                                                        _message.value = it.exception?.message
                                                    }
                                                }

                                        }else{
                                            _loading.value=false
                                            _message.value = it.exception?.message
                                        }
                                    }


                            }else{
                                _loading.value=false
                                _message.value = it.exception?.message
                            }
                        }



                    } else {
                        _loading.value=false
                        _message.value="error, user data exist!"
                    }
                } else {
                    _loading.value=false
                    _message.value="error, try again later!"
                }

            }
        }


    }

    fun getPreviousTests(subject: String) {

        _loading.value=true
        //for increasing attended question value to that subjact
        val subjectRef=  db.collection("users")
            .document(fUser!!.uid)
            .collection("subjects")
            .document(subject)

        //for adding test details
        val testRef = subjectRef
            .collection("tests")

        viewModelScope.launch(Dispatchers.IO){

            testRef.get().addOnCompleteListener{
                _loading.value=false
                if(it.isSuccessful){
                    val collection = it.result
                    if (collection != null && !collection.isEmpty) {
                        val testsList= arrayListOf<com.nsa.brainwave.home.models.Report.TestReport>()
                        collection.documents.forEach {
                            testsList.add(it.toObject<com.nsa.brainwave.home.models.Report.TestReport>()!!)
                        }
                        _previousTestList.value=testsList
                    }else{
                        _previousTestList.value= arrayListOf()
                    }

                }else{
                    _message.value="error, ${it.exception?.message}"
                }
            }

        }


    }

    fun countOptionsSelected(): Int {
        var counter=0
        _testModel.value!!.questions!!.forEach {
            if(it.selectedOption!=null){
                counter++
            }
        }
        return counter
    }


}