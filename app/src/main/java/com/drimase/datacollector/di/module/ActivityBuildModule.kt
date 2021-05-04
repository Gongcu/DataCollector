package com.drimase.datacollector.di.module

import com.drimase.datacollector.ui.main.MainActivity
import com.drimase.datacollector.di.util.ActivityScope
import com.drimase.datacollector.ui.login.LoginActivity
import com.drimase.datacollector.ui.registration.RegistrationActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface ActivityBuildModule {

    /**
     *  MainActivity를 위한 서브 컴포넌트
     *  ContributesAndroidInjector --> 서브 컴포넌트
     *  (기존 방식에 비해 보일러 플레이트가 줄어듦)
     * */
    @ActivityScope
    @ContributesAndroidInjector(modules = [MainModule::class])
    fun mainActivity() : MainActivity

    @ActivityScope
    @ContributesAndroidInjector
    fun registrationActivity() : RegistrationActivity

    @ActivityScope
    @ContributesAndroidInjector
    fun loginActivity() : LoginActivity
}