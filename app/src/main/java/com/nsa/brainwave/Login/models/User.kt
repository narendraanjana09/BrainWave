package com.nsa.brainwave.Login.models


data class User(
    val name:String?=null,
    val email:String?=null,
    val uid:String?=null,
    val photoUrl:String?=null,
    val exam:String?=null,
    val dateJoined:String?=null,
)