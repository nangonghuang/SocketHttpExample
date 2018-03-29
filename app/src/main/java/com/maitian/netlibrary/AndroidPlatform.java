package com.maitian.netlibrary;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import com.xckoohttp.net.PlatForm;

import java.util.concurrent.Executor;


public class AndroidPlatform extends PlatForm {

    private AndroidExecutor androidExecutor = new AndroidExecutor();

    @Override
    public boolean isMainthread() {
        return Looper.getMainLooper() == Looper.myLooper();
    }

    @Override
    public Executor getMainThreadExecutor() {
        return androidExecutor;
    }

    static class AndroidExecutor implements Executor {

        private final Handler mainHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable command) {
            mainHandler.post(command);
        }
    }
}
