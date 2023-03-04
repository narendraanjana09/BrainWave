package com.nsa.brainwave.Login.models

data class SubjectModel(
    val name:String?=null,
    var totalAttended:Int?=null,
    var correctSolved:Int?=null,
    var missed:Int?=null,
)
