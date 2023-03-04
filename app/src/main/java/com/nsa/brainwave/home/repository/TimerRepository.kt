package com.nsa.brainwave.home.repository

import android.os.CountDownTimer
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import javax.inject.Inject


interface TimerRepository {
    val data: Flow<Int>
    suspend fun startCountDown(count: Int)
    suspend fun stop()
}


class TimerRepositoryImpl: TimerRepository {
    private val _data: MutableStateFlow<Int?> = MutableStateFlow(null)
    override val data: Flow<Int>
        get() = _data.filterNotNull()

    private var stop=false

    override suspend fun startCountDown(count: Int) {
        for(i in 0..count){
            if(stop){
                break
            }
            _data.emit(count - i)
            // Delay 1 sec
            delay(1000L)
    }
    }

    override suspend fun stop() {
        stop=true
    }
}