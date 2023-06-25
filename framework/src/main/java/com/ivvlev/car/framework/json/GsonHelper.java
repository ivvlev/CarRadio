package com.ivvlev.car.framework.json;

import com.google.gson.Gson;

public class GsonHelper {

    private final static Gson GSON = new Gson();

    public static String toJson(Object obj, Class clazz) {
        return GSON.toJson(obj, clazz);
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        return GSON.fromJson(json, clazz);
    }

}
