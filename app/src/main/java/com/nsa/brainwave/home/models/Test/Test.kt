package com.nsa.brainwave.home.models.Test


data class Test(
    val exam: String?=null,
    val level: Int?=null,
    val questions: List<Question>?=null,
    val subject: String?=null
):java.io.Serializable