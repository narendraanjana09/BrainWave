package com.nsa.brainwave.Login.frags

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.nsa.brainwave.App
import com.nsa.brainwave.Login.adapters.ExamsAdapter
import com.nsa.brainwave.Login.models.SubjectModel
import com.nsa.brainwave.home.adapters.SubjectsAdapter
import com.nsa.brainwave.Login.models.User
import com.nsa.brainwave.Login.models.UserData
import com.nsa.brainwave.Login.models.exams.Exams
import com.nsa.brainwave.MainActivity
import com.nsa.brainwave.R
import com.nsa.brainwave.databinding.FragmentChooseExamBinding
import com.nsa.brainwave.util.Util
import com.nsa.myApp.SharedPref


class Fragment_Choose_Exam : Fragment()  {


    private lateinit var binding: FragmentChooseExamBinding
    private val examList= arrayListOf<com.nsa.brainwave.Login.models.exams.Exam>()
    private lateinit var examsAdapter: ExamsAdapter
    private var currentSelection=-1

    private val db = Firebase.firestore
    private val realTimeDb = Firebase.database.reference

    private val auth= Firebase.auth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=DataBindingUtil.inflate(inflater, R.layout.fragment_choose_exam, container, false)
        return binding.root;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Got this layout manager class from google flex-box library
        val layoutManager = FlexboxLayoutManager(context)
        layoutManager.flexDirection = FlexDirection.ROW
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter=examsAdapter

        binding.nextBtn.setOnClickListener {
          if(currentSelection==-1){
              Util.showToast(requireContext(),"Please select a exam!")
          }else{
              Util.showProgress(findNavController())
              val fUser = auth.currentUser
              val user=User(
                  name = fUser?.displayName!!,
                  uid = fUser.uid!!,
                  email = fUser.email!!,
                  photoUrl = fUser.photoUrl.toString(),
                  exam = examList[currentSelection].name!!,
                  dateJoined =Util.getCurrentDate()
              )
              val userData=UserData(
                  user,
                  SubjectModel(
                      "",0,0,0
                  )
              )
              saveUserData(userData)
          }
        }
    }

    private fun saveUserData(userData: UserData) {
        db.collection("users").document(userData.user?.uid!!)
            .set(userData)
            .addOnCompleteListener {
                Util.hideProgress(findNavController(),R.id.fragment_Choose_Exam)
                if(it.isSuccessful){
                    Util.showToast(requireContext(),"Data Updated!")
                    App.prefs.push(SharedPref.examSelected,true)
                    (requireActivity() as MainActivity).goToHome()
                }else{
                    Util.showToast(requireContext(),"error")
                }
            }


    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        examsAdapter= ExamsAdapter(examList,object : ExamsAdapter.ClickCallBack{
            override fun onClicked(position: Int) {
                if(currentSelection==-1){
                    examList[position].isSelected=true
                    currentSelection=position
                }else{
                    examList[currentSelection].isSelected=false
                    examsAdapter.notifyItemChanged(currentSelection)
                    examList[position].isSelected=true
                    currentSelection=position
                }
                examsAdapter.notifyItemChanged(currentSelection)
            }
        })
        readExams()
    }

    private fun readExams() {

        Util.showProgress(findNavController())
        realTimeDb.child("data")
            .addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    Util.hideProgress(findNavController(),R.id.fragment_Choose_Exam)
                    Log.e("TAG", "onDataChange: ${dataSnapshot.value}", )
                   val exams = dataSnapshot.getValue<Exams>()
                   examList.clear()
                    examList.addAll(exams?.exams!!)
                    examsAdapter.notifyItemRangeInserted(0,examList.size)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Getting Post failed, log a message
                    Util.hideProgress(findNavController(),R.id.fragment_Choose_Exam)
                    Log.e("TAG", "loadPost:onCancelled", databaseError.toException())
                }
            })
    }

}