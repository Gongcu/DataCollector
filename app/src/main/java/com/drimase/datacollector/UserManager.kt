package com.drimase.datacollector

import android.util.Log
import com.drimase.datacollector.dto.User
import javax.inject.Inject

private const val TAG = "UserManager"

class UserManager @Inject constructor(){
    private lateinit var user: User

    @JvmName("setUser1")
    fun setUser(user:User){
        Log.d(TAG, "setUser: ${user.toString()}")
        this.user= user
    }

    fun getUserId() : Int{
        return user.id
    }
}