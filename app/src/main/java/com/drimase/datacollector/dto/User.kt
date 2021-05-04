package com.drimase.datacollector.dto

data class User(
    private val id : Int,
    private val loginId :String,
    private val password: String,
) {
}