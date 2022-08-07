package intalio.cts.mobile.android.di

import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import intalio.cts.mobile.android.util.Constants

@Module
class StorageModule (var context: Context){


    @Provides
    fun provideSharedPreference() : SharedPreferences =

      context.getSharedPreferences(Constants.SHARED_NAME,Context.MODE_PRIVATE)


}