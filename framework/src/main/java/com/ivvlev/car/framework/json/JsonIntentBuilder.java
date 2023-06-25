package com.ivvlev.car.framework.json;

import android.content.Intent;

public class JsonIntentBuilder {

    private String mAction;
    private JsonArgsBuilder mJsonArgsBuilder = new JsonArgsBuilder();

    public JsonIntentBuilder setAction(String name) {
        mAction = name;
        return this;
    }

    public JsonIntentBuilder put(String name, String value) {
        mJsonArgsBuilder.put(name, value);
        return this;
    }

    public JsonIntentBuilder put(String name, boolean value) {
        mJsonArgsBuilder.put(name, value);
        return this;
    }

    public JsonIntentBuilder put(String name, double value) {
        mJsonArgsBuilder.put(name, value);
        return this;
    }

    public JsonIntentBuilder put(String name, long value) {
        mJsonArgsBuilder.put(name, value);
        return this;
    }

    public JsonIntentBuilder put(String name, Number value) {
        mJsonArgsBuilder.put(name, value);
        return this;
    }

    public JsonIntentBuilder putNull(String name) {
        mJsonArgsBuilder.putNull(name);
        return this;
    }

    public Intent toIntent() {
        return new Intent()
                .setAction(mAction)
                .putExtra("args", mJsonArgsBuilder.toJsonString());
    }

}
