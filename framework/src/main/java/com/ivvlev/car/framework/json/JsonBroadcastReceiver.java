package com.ivvlev.car.framework.json;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public abstract class JsonBroadcastReceiver extends BroadcastReceiver {
    @Override
    final public void onReceive(Context context, Intent intent) {

    }

    protected abstract void onReceive(Context context, Intent intent, String action, Argument[] args);

    protected static class Argument {
        public final String name;
        public final Object value;

        public Argument(String name, Object value) {
            this.name = name;
            this.value = value;
        }
    }
}
