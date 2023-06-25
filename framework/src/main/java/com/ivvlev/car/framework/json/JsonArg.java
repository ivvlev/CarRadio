package com.ivvlev.car.framework.json;

public class JsonArg {
    public final String name;
    public final String dataType;
    public final Object value;

//    public JsonArg(String name, long value) {
//        this(name, "long", value);
//    }
//
//    public JsonArg(String name, double value) {
//        this(name, "long", value);
//    }
//
//    public JsonArg(String name, boolean value) {
//        this(name, "long", value);
//    }
//
//    public JsonArg(String name, Number value) {
//        this(name, "long", value);
//    }
//
    public JsonArg(String name, String dataType, Object value) {
        this.name = name;
        this.dataType = dataType;
        this.value = value;
    }
}
