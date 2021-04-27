package com.drimase.datacollector.di.component

import android.app.Application
import android.content.Context
import com.drimase.datacollector.BaseApplication
import com.drimase.datacollector.di.module.ActivityModule
import com.drimase.datacollector.di.module.AppModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [
    AndroidSupportInjectionModule::class,
    AppModule::class,
    ActivityModule::class
])
interface ApplicationComponent : AndroidInjector<BaseApplication> {

    @Component.Factory
    abstract class Factory : AndroidInjector.Factory<BaseApplication>{
    }

}