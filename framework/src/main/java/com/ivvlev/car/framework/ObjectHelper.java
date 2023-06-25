package com.ivvlev.car.framework;

public class ObjectHelper {


    public static boolean equals(Object o1, Object o2) {
        return equals(o1, o2, true);
    }

    public static boolean equals(Object o1, Object o2, boolean nullsAreEquals) {
        if (o1 == null && o2 == null)
            return nullsAreEquals;
        if (o1 == null || o2 == null)
            return false;
        return o1.equals(o2);
    }

}
