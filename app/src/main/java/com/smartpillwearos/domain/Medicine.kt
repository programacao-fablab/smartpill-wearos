package com.smartpillwearos.domain

data class Medicine(
    val id: String,
    val name: String,
    val time: String, // HH:mm format
    val isDone: Boolean
)
