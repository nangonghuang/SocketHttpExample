package com.xckoohttp.net;

import java.util.concurrent.Executor;

public abstract class PlatForm {
    public abstract boolean isMainthread();

    public abstract Executor getMainThreadExecutor();
}
