package com.nsa.brainwave.home.models.Report

data class ReportModel (
    val date:Long?=null,
    val timeTaken:Int?=null,
    val totalQuestion:Int?=null,
    val correct:Int?=null,
    val wrong:Int?=null,
    val missed:Int?=null,
):java.io.Serializable