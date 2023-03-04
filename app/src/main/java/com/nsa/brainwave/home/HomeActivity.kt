package com.nsa.brainwave.home

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.nsa.brainwave.App
import com.nsa.brainwave.MainActivity
import com.nsa.brainwave.R
import com.nsa.brainwave.databinding.ActivityHomeBinding
import com.nsa.brainwave.home.ui.FragmentSelectLevel
import com.nsa.brainwave.home.viewmodels.PassDataViewModel
import com.nsa.brainwave.util.Util
import org.imaginativeworld.oopsnointernet.callbacks.ConnectionCallback
import org.imaginativeworld.oopsnointernet.dialogs.signal.NoInternetDialogSignal

class HomeActivity : AppCompatActivity(), OnUserEarnedRewardListener {

    private lateinit var binding:ActivityHomeBinding
    private lateinit var analytics: FirebaseAnalytics
    private lateinit var navController: NavController
    private var lastDestination:Int?=null

    private var rewardedInterstitialAd:RewardedInterstitialAd? = null
    private final var TAG = "MainActivity"
    private val passDataViewModel: PassDataViewModel by lazy {
        ViewModelProvider(this@HomeActivity).get(PassDataViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_home)
        analytics = Firebase.analytics
        Util.loadNoInternet(this,lifecycle)

        navController=(supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment).navController
        binding.bottomNav.setupWithNavController(navController)

        navController.addOnDestinationChangedListener(object :NavController.OnDestinationChangedListener{
            override fun onDestinationChanged(
                controller: NavController,
                destination: NavDestination,
                arguments: Bundle?
            ) {
                when(destination.id){
                    R.id.fragmentTest,
                    R.id.fragmentScoreCard,
                    R.id.fragmentSubject,
                    R.id.fragmentSelectLevel,
                    R.id.fragmentProgress->{
                        if (!(lastDestination==R.id.fragmentHome || lastDestination==R.id.fragmentProfile) || destination.id != R.id.fragmentProgress) {
                            hideBottomNav()
                        }
                    }
                    else->{
                        showBottomNav()
                    }
                }
                lastDestination=destination.id
            }
        })

        MobileAds.initialize(this) { initializationStatus ->
            loadAd()
        }
    }



    private fun loadAd() {
        RewardedInterstitialAd.load(this, getString(R.string.ads_rewarded_id),
            AdRequest.Builder().build(), object : RewardedInterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedInterstitialAd) {
                    Log.e(TAG, "Ad was loaded.")
                    rewardedInterstitialAd = ad
                    setCallback()

                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.e(TAG,"add load error "+ adError.message)
                    rewardedInterstitialAd = null
                }
            })
    }

    private fun setCallback() {
        rewardedInterstitialAd?.fullScreenContentCallback = object: FullScreenContentCallback() {
            override fun onAdClicked() {
                // Called when a click is recorded for an ad.
                Log.e(TAG, "Ad was clicked.")
            }

            override fun onAdDismissedFullScreenContent() {
                // Called when ad is dismissed.
                // Set the ad reference to null so you don't show the ad a second time.
                Log.e(TAG, "Ad dismissed fullscreen content.")
                rewardedInterstitialAd = null
                passDataViewModel.setAdDismissed(true)
                loadAd()

            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                // Called when ad fails to show.
                Log.e(TAG, "Ad failed to show fullscreen content.")
                rewardedInterstitialAd = null
                passDataViewModel.setAdDismissed(true)
                loadAd()
            }

            override fun onAdImpression() {
                // Called when an impression is recorded for an ad.
                Log.e(TAG, "Ad recorded an impression.")
            }

            override fun onAdShowedFullScreenContent() {
                // Called when ad is shown.
                Log.e(TAG, "Ad showed fullscreen content.")
            }
        }
    }

    fun showBottomNav() {
        binding.constraintLayout.transitionToStart()
    }

    fun hideBottomNav() {
        binding.constraintLayout.transitionToEnd()
    }


    fun logout() {
        Firebase.auth.signOut()
        App.prefs.clear()
        startActivity(Intent(this,MainActivity::class.java))
        finish()
    }

    override fun onUserEarnedReward(p0: RewardItem) {
        Log.e(TAG, "onUserEarnedReward: ${p0.type},${p0.amount}", )
    }


    fun showAd() {
        passDataViewModel.setAdDismissed(false)
        rewardedInterstitialAd?.show(this@HomeActivity,this@HomeActivity)
    }
}