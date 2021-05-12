package com.drimase.datacollector

import android.util.Log
import com.drimase.datacollector.dto.User
import javax.inject.Inject

private const val TAG = "UserManager"

class UserManager @Inject constructor(){
    //현재 기록 시 유저 정보 필요 없음
    private var user: User = User(0,"admin","test")
    private var recordingVideoID:Int = -1

    @JvmName("setUser1")
    fun setUser(user:User){
        Log.d(TAG, "setUser: ${user.toString()}")
        this.user= user
    }


    fun getUserId() : Int{
        return user.id
    }

    fun setRecordingVideoID(videoId:Int){
        this.recordingVideoID= videoId
    }


    fun getRecordingVideoID() : Int{
        return recordingVideoID
    }

}