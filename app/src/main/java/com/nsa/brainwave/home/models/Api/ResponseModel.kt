package com.nsa.brainwave.home.models.Api

data class ResponseModel(
    val choices: List<Choice>,
    val created: Int,
    val id: String,
    val model: String,
    val `object`: String,
    val usage: Usage
)