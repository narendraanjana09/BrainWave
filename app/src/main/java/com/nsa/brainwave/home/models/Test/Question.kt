package com.nsa.brainwave.home.models.Test

data class Question(
    val correctOption: Int?=null,
    var selectedOption: Int?=null,
    val minimumTimeToSolve: Int?=null,
    val options: List<String>?=null,
    val question: String?=null
):java.io.Serializable