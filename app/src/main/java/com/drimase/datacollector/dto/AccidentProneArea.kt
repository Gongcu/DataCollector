package com.drimase.datacollector.dto

import com.google.gson.annotations.SerializedName

data class AccidentProneArea(
        private val id :Int,
        private val spotName: String,
        private val longitude: Double,
        private val latitude: Double,
)