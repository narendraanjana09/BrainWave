package com.nsa.brainwave.Login.models.exams

data class Exam(
    val name: String? = null,
    var isSelected: Boolean? = null,
    val subjects: List<String>? = null
)