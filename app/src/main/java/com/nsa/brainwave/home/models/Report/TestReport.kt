package com.nsa.brainwave.home.models.Report

import com.nsa.brainwave.home.models.Test.Test

data class TestReport (
    val reportModel:ReportModel?=null,
    val test:Test?=null
):java.io.Serializable