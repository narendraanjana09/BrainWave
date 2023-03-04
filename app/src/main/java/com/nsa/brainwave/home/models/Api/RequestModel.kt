package com.nsa.brainwave.home.models.Api

data class RequestModel(
    val max_tokens: Int,
    val model: String,
    val prompt: String,
    val temperature: Double
)