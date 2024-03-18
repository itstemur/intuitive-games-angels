package com.visafm.roombook.app;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.visafm.roombook.BuildConfig;

import timber.log.Timber;

public class OrdersApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.plant(new NoLogTree());
        }
    }

    static class NoLogTree extends Timber.Tree {
        @Override
        protected void log(int i, @Nullable String s, @NonNull String s1, @Nullable Throwable throwable) {
            // DO NOT LOG
        }
    }
}