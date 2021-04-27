package com.drimase.datacollector.di.util

import java.lang.annotation.RetentionPolicy
import javax.inject.Qualifier

@Qualifier
@Retention(value=AnnotationRetention.RUNTIME)
annotation class ApplicationContext
