package com.drimase.datacollector.service

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.drimase.datacollector.base.BaseApplication
import javax.inject.Inject

class SharedPreferencesManager @Inject constructor(
    private val application: BaseApplication
) {

    fun getUserId():Int{
        return getInstance(application).getInt(userIdKey,defaultUserId)
    }

    fun setUserId(value:Int){
        val editor = getInstance(application).edit()
        editor.putInt(userIdKey,value)
        editor.apply()
    }

    fun setUserName(value:String){
        val editor = getInstance(application).edit()
        editor.putString(userNameKey,value)
        editor.apply()
    }

    fun getUserName():String{
        return getInstance(application).getString(userNameKey,defaultUserName)!!
    }

    fun setLogout(){
        val editor = getInstance(application).edit()
        editor.putString(userNameKey,defaultUserName)
        editor.putInt(userIdKey,defaultUserId)
        editor.apply()
    }

    companion object{
        private const val sharedPreFile = "preferences"
        private const val userIdKey = "userId"
        private const val userNameKey = "userName"
        private const val defaultUserId = -1
        private const val defaultUserName = ""
        private var instance : SharedPreferences? = null


        fun getInstance(context: Context):SharedPreferences{
            if(instance==null){
                instance = context.getSharedPreferences(sharedPreFile,MODE_PRIVATE)
            }
            return instance!!
        }
    }
}