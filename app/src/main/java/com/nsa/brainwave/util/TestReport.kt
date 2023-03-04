package com.nsa.brainwave.util

import com.nsa.brainwave.home.models.Report.ReportModel
import com.nsa.brainwave.home.models.Report.TestReport
import com.nsa.brainwave.home.models.Test.Test

object TestReport {

    fun getTestReport(timeTaken: Int, test: Test?): TestReport {
        return TestReport(getReportModel(timeTaken, test!!),test!!)
    }

    private fun getReportModel(timeTaken: Int, test: Test): ReportModel {
        var correct=0
        var wrong=0
        var missed=0
        test.questions!!.forEach {
            if(it.selectedOption==null){
                if(it.correctOption!!>3){//for options whose correct option is some times getting 4
                    correct++
                }else{
                    missed++
                }

            }else if(it.selectedOption==it.correctOption){
                correct++
            }else{
                wrong++
            }
        }
        return ReportModel(
            System.currentTimeMillis(),
            timeTaken,
            test.questions!!.size,
            correct, wrong, missed
        )
    }

}