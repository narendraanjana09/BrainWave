package com.nsa.brainwave.home.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.nsa.brainwave.Login.models.SubjectModel
import com.nsa.brainwave.R
import com.nsa.brainwave.databinding.DrawerLayoutBinding
import com.nsa.brainwave.databinding.FragmentHomeBinding
import com.nsa.brainwave.databinding.FragmentLogoBinding
import com.nsa.brainwave.home.HomeActivity
import com.nsa.brainwave.home.adapters.SubjectsAdapter
import com.nsa.brainwave.home.viewmodels.HomeViewModel
import com.nsa.brainwave.util.Util
import com.nsa.brainwave.util.Util.showToast
import com.nsa.myApp.SharedPref


class FragmentHome : Fragment() {


    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by lazy {
        ViewModelProvider(requireActivity() as HomeActivity).get(HomeViewModel::class.java)
    }
    private val subjectsList= arrayListOf<SubjectModel>()
    private lateinit var subjectsAdapter:SubjectsAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        return binding.root;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val layoutManager = FlexboxLayoutManager(context)
        layoutManager.flexDirection = FlexDirection.ROW
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter=subjectsAdapter

        enableDrawerListener()
        initilizeDrawerClickListners()

        viewModel.loading.observe(viewLifecycleOwner){
            if(it){
                Util.showProgress(findNavController())
            }else{
                Util.hideProgress(findNavController(),R.id.fragmentHome)
            }

        }
        viewModel.message.observe(viewLifecycleOwner){
           showToast(requireContext(), it)
        }

        viewModel.userModel.observe(viewLifecycleOwner){
            binding.swipeRefresh.isRefreshing=false
            if(!it.photoUrl.isNullOrEmpty() && !it.photoUrl.equals("null") ){
                Util.loadImage(requireContext(),it.photoUrl,binding.profileImageView)
            }
            binding.drawerNavView.examNameTv.text=it.exam
        }

        viewModel.subjectsList.observe(viewLifecycleOwner){
            subjectsList.clear()
            subjectsList.addAll(it)
            subjectsAdapter.notifyItemRangeChanged(0,subjectsList.size)
        }

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.getUserData()
        }

    }

    //we have all drawer related setOnClickListener
    private fun initilizeDrawerClickListners() {
        binding.profileBtn.setOnClickListener{
            openDrawer()
        }
        binding.drawerNavView.showAdBtn.setOnClickListener {
           //(requireActivity() as HomeActivity).showAd()
        }


        binding.drawerNavView.logoutBtn.setOnClickListener {
            (requireActivity() as HomeActivity).logout()
        }
        binding.drawerNavView.closeDrawerBtn.setOnClickListener {
            closeDrawer()
        }
        binding.drawerNavView.feedbackBtn.setOnClickListener {
            closeDrawer()
            findNavController().navigate(
                FragmentHomeDirections.actionFragmentHomeToFragmentFeedback()
            )
        }
    }

    private fun enableDrawerListener() {
        binding.drawerLayout.addDrawerListener(object :DrawerLayout.DrawerListener{
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {

            }

            override fun onDrawerOpened(drawerView: View) {

            }

            override fun onDrawerClosed(drawerView: View) {
                (requireActivity() as HomeActivity).showBottomNav()
            }

            override fun onDrawerStateChanged(newState: Int) {
            }
        })
    }

    private fun openDrawer() {
        (requireActivity() as HomeActivity).hideBottomNav()
        binding.drawerLayout.openDrawer(GravityCompat.START)
    }
    private fun closeDrawer() {
        binding.drawerLayout.closeDrawer(GravityCompat.START)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            if(binding.drawerLayout.isOpen){
                closeDrawer()
            }else{
                requireActivity().finish()
            }

        }


        subjectsAdapter= SubjectsAdapter(subjectsList,object :SubjectsAdapter.ClickCallBack{
            override fun onClicked(position: Int) {
                findNavController().navigate(
                    FragmentHomeDirections.actionFragmentHomeToFragmentSubject(
                        viewModel.getUserExam(),
                        subjectsList[position].name!!
                    )
                )
            }
        })

        viewModel.getUserData()
    }




}