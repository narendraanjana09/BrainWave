package com.nsa.brainwave.home.viewmodels

import androidx.lifecycle.*
import com.nsa.brainwave.home.repository.TimerRepositoryImpl
import com.nsa.brainwave.util.Util
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class TimerViewModel:ViewModel() {

    
    private val timerRepo=TimerRepositoryImpl()

    val countDown: LiveData<String> = timerRepo.data.map {
        Util.getTime(it)
    }.asLiveData().distinctUntilChanged()

    // Progress state of circle bar
    val progress: LiveData<Int> = timerRepo.data.map {
        it
    }.asLiveData().distinctUntilChanged()

    fun start(seconds: Int) {
        viewModelScope.launch {
            // Count down 10 seconds
            timerRepo.startCountDown(seconds)
        }
    }
    fun stop(){
        viewModelScope.launch {
            timerRepo.stop()
        }
    }
}