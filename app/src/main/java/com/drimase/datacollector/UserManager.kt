package com.drimase.datacollector

import com.drimase.datacollector.dto.User
import javax.inject.Inject


class UserManager @Inject constructor(){
    lateinit var user: User
}