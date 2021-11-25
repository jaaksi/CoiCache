package org.jaaksi.coicache.demo

import android.app.Application
import org.jaaksi.coicache.CoiCache
import org.jaaksi.coicache.util.CacheLog

class TheApplication : Application() {
    companion object {
        lateinit var instance: TheApplication
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        CacheLog.debug = BuildConfig.DEBUG
        CoiCache.initialize(this)
    }
}