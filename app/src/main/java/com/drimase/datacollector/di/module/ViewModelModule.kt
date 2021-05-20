package com.drimase.datacollector.di.module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.drimase.datacollector.di.ViewModelFactory
import com.drimase.datacollector.di.util.ActivityScope
import com.drimase.datacollector.di.util.ViewModelKey
import com.drimase.datacollector.ui.login.LoginViewModel
import com.drimase.datacollector.ui.main.MainViewModel
import com.drimase.datacollector.ui.registration.RegistrationViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {
    @Binds
    abstract fun  bindViewModelFactory(factory: ViewModelFactory) : ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    abstract fun bindsMainViewModel(viewModel: MainViewModel):ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(RegistrationViewModel::class)
    abstract fun bindsRegistrationViewModel(viewModel: RegistrationViewModel):ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(LoginViewModel::class)
    abstract fun bindsLoginViewModel(viewModel: LoginViewModel):ViewModel
}