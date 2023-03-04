package com.nsa.brainwave.util

import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.nsa.brainwave.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.imaginativeworld.oopsnointernet.callbacks.ConnectionCallback
import org.imaginativeworld.oopsnointernet.dialogs.signal.NoInternetDialogSignal
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern


object Util {
     fun getColoredText(text: String, color: String): String{
        return "<font color=$color>$text</font>"
    }
    fun getTime(seconds:Int): String {
        val sec = seconds % 60
        var hr = seconds / 60
        val min = hr % 60

        if(min==0){
            return "$sec"
        }
        return "$min:$sec"
    }

    fun showToast(context: Context,text: String){
        Toast.makeText(context,text,Toast.LENGTH_SHORT).show()
    }
    fun showProgress(navController: NavController){
        navController.navigate(
            R.id.fragmentProgress
        )
    }
    fun hideProgress(navController: NavController,destination: Int){
        navController.popBackStack(destinationId = destination, inclusive = false, saveState = false)
    }

    fun showMessageSheet(title: String, message: String, requireActivity: FragmentActivity){
        val messageBottomSheet=MessageBottomSheet(title,message)
        messageBottomSheet.show(requireActivity.supportFragmentManager,"message")
    }

    fun loadImage(context: Context, imageLink: String, imageView: ImageView) {
        Glide.with(context)
            .load(imageLink)
            .transition(DrawableTransitionOptions.withCrossFade())
            .error(com.google.android.material.R.drawable.mtrl_ic_error)
            .into(imageView)
    }

    fun isValidEmail(email: String?): Boolean {
        val emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$"
        val pat: Pattern = Pattern.compile(emailRegex)
        return if (email == null) false else pat.matcher(email).matches()
    }

    fun getScreenShot(view: View): Bitmap? {
        val view1 = view.rootView
//        View view1 = getWindow().getDecorView().getRootView();

        val bitmap = Bitmap.createBitmap(view1.getWidth(), view1.getHeight(), Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view1.draw(canvas)

        return bitmap
    }

    fun getCurrentDate(): String {
        val simpleDate = SimpleDateFormat("dd/MM/yyyy hh:mm a")
        return simpleDate.format(Date())
    }

    fun loadImage(link: String, imageView: ImageView) {
        CoroutineScope(Dispatchers.IO).launch {
            Glide.with(imageView.context)
                .load(link)
                .centerCrop()
                .error(R.drawable.splach_bg)
                .into(object : CustomTarget<Drawable?>() {
                    override fun onLoadCleared(placeholder: Drawable?) {}
                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        super.onLoadFailed(errorDrawable)
                        CoroutineScope(Dispatchers.Main).launch {
                            imageView.setImageDrawable(errorDrawable)
                        }
                    }

                    override fun onResourceReady(
                        resource: Drawable,
                        transition: Transition<in Drawable?>?
                    ) {
                        CoroutineScope(Dispatchers.Main).launch {
                            imageView.setImageDrawable(resource)
                        }
                    }
                })
        }
    }

    fun calculateProgress(totalAttended: Double, correctSolved: Double, missed: Double): Double {
         if(totalAttended==0.0){
            return 0.0
        }
        if(missed==totalAttended){
            return 0.0
        }
        //(((correctSolved as Double).div(totalAttended as Double)).times(100 as Double)).toInt()

        return  ((correctSolved*100)/totalAttended)-((missed*100)/totalAttended)

    }

    fun getTimeAsTest(input: Int): String {
        if(input==0){
            return "0"
        }
        val minutes: Int = input % 3600 / 60
        val seconds: Int = input % 3600 % 60

        if(minutes==0){
            return "$seconds sec"
        }
        if(seconds==0){
            return "$minutes min"
        }

       return "$minutes:$seconds mins"
    }

    fun getDate(date: Long): String? {
        //dd MMM yyyy
        val simpleDate = SimpleDateFormat("MMM dd, yyyy hh:mm a")
        return simpleDate.format(date)
    }

    fun CircularProgressIndicator.smoothProgress(percent: Int){
        val animation = ObjectAnimator.ofInt(this, "progress", percent)
        animation.duration = 800
        animation.interpolator = DecelerateInterpolator()
        animation.start()
    }
    fun LinearProgressIndicator.smoothProgress(percent: Int){
        val animation = ObjectAnimator.ofInt(this, "progress", percent)
        animation.duration = 800
        animation.interpolator = DecelerateInterpolator()
        animation.start()
    }
    fun loadNoInternet(activity: Activity,lifecycle:Lifecycle) {
        NoInternetDialogSignal.Builder(
            activity,
            lifecycle
        ).apply {
            dialogProperties.apply {
                connectionCallback = object : ConnectionCallback { // Optional
                    override fun hasActiveConnection(hasActiveConnection: Boolean) {
                        // ...
                    }
                }
                cancelable = false // Optional
                noInternetConnectionTitle = "No Internet" // Optional
                noInternetConnectionMessage =
                    "Check your Internet connection and try again." // Optional
                showInternetOnButtons = true // Optional
                pleaseTurnOnText = "Please turn on" // Optional
                wifiOnButtonText = "Wifi" // Optional
                mobileDataOnButtonText = "Mobile data" // Optional

                onAirplaneModeTitle = "No Internet" // Optional
                onAirplaneModeMessage = "You have turned on the airplane mode." // Optional
                pleaseTurnOffText = "Please turn off" // Optional
                airplaneModeOffButtonText = "Airplane mode" // Optional
                showAirplaneModeOffButtons = true // Optional
            }
        }.build()

    }
}