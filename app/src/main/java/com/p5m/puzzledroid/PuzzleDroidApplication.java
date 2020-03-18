package com.p5m.puzzledroid;

import android.app.Application;

import timber.log.Timber;

/**
 * Override Application to set up some initial configuration
 */
public class PuzzleDroidApplication extends Application {

    /**
     * Called before the first screen is shown to the user
     */
    @Override
    public void onCreate() {
        super.onCreate();
        Timber.plant(new Timber.DebugTree());
    }
}
