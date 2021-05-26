package com.drimase.datacollector.dto

data class VideoRecord(
    val userId : Int,
    val userName:String,
    val videoId:Int,
    val longitude:Double,
    val latitude:Double,
    val altitude:Double,
    val file : ProgressRequestBody)
{
}