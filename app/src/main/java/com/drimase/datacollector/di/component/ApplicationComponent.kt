package com.drimase.datacollector.di.component

import com.drimase.datacollector.BaseApplication
import com.drimase.datacollector.di.module.ActivityBuildModule
import com.drimase.datacollector.di.module.AppModule
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [
    AndroidSupportInjectionModule::class,
    AppModule::class,
    ActivityBuildModule::class
])
interface ApplicationComponent : AndroidInjector<BaseApplication> {

    @Component.Factory
    abstract class Factory : AndroidInjector.Factory<BaseApplication>{
    }

}