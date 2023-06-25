package com.ivvlev.car.radio.mst768;

public enum Band {
    FM1(0), FM2(1), AM(2);

    public final int id;

    Band(int id) {
        this.id = id;
    }

    public static Band fromId(int id) {
        switch (id) {
            case 0:
                return FM1;
            case 1:
                return FM2;
            case 2:
                return AM;
            default:
                throw new RuntimeException(String.format("Incorrect band index: %s", id));
        }
    }
}
