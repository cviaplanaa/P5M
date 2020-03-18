package com.p5m.puzzledroid

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Override Application to set up some initial configuration
 */
class PuzzledroidApplication: Application() {
    private val applicationScope = CoroutineScope(Dispatchers.Default)

    /**
     * Called before the first screen is shown to the user
     */
    override fun onCreate() {
        super.onCreate()
        delayedInit()
    }

    /**
     * Used to set up background task using a couroutine to not block the UI thread
     */
    private fun delayedInit() {
        applicationScope.launch {
            // Use this instead of the built-in Log
            Timber.plant(Timber.DebugTree())
        }
    }
}