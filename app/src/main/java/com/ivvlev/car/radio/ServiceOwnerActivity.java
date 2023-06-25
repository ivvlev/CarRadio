package com.ivvlev.car.radio;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public abstract class ServiceOwnerActivity extends AppCompatActivity {
    protected final String LOG_TAG = getClass().getCanonicalName();
    protected final boolean DEBUG = true;
    private Map<String, ComponentName> mServiceMap = new HashMap<>();

    @Override
    public ComponentName startService(Intent service) {
        if (DEBUG) Log.d(LOG_TAG, "startService: " + service.getComponent().getClassName());
        ComponentName componentName = super.startService(service);
        if (componentName != null) {
            if (DEBUG) Log.d(LOG_TAG, "startService: " + service.getComponent().getClassName() + " success");
            mServiceMap.put(componentName.getClassName(), componentName);
        }
        return componentName;
    }

    @Override
    public boolean bindService(Intent service, ServiceConnection conn, int flags) {
//        if (mServiceMap.isEmpty()){
//            return false;
//        }
        return super.bindService(service, conn, flags);
    }

    @Override
    public void unbindService(ServiceConnection conn) {
//        if (mServiceMap.isEmpty()){
//            return;
//        }
        super.unbindService(conn);
    }

    @Override
    protected void onDestroy() {
        if (DEBUG) Log.d(LOG_TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        for (ComponentName componentName : mServiceMap.values()) {
            try {
                if (DEBUG) Log.d(LOG_TAG, "stopService: " + componentName.getClassName());
                stopService(new Intent(this, getClass().getClassLoader().loadClass(componentName.getClassName())));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        mServiceMap.clear();
        super.onBackPressed();
    }


}
