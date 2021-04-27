package com.drimase.datacollector.di.module

import com.drimase.datacollector.MainActivity
import com.drimase.datacollector.di.util.ActivityScope
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface ActivityModule {

    /**
     *  MainActivity를 위한 서브 컴포넌트
     *  ContributesAndroidInjector --> 서브 컴포넌트
     *  (기존 방식에 비해 보일러 플레이트가 줄어듦)
     * */
    @ActivityScope
    @ContributesAndroidInjector(modules = [MainModule::class])
    fun mainActivity() : MainActivity
}