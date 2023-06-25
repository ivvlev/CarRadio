package com.ivvlev.car.radio.mst768;

import java.util.UUID;

public class Station {

    public final String uuid;
    public final int freqRangeId;
    public String name;
    public int freq;
    public int pty = 0;

    public Station(String aName, int aFreq) {
        this(aName, aFreq, 0, 0, null);
    }

    public Station(String aName, int aFreq, int aFreqRangeId, int aPty) {
        this(aName, aFreq, aFreqRangeId, aPty, UUID.randomUUID().toString());
    }


    public Station(String aName, int aFreq, int aFreqRangeId, int aPty, String aUUID) {
        name = aName;
        freq = aFreq;
        freqRangeId = aFreqRangeId;
        pty = aPty;
        uuid = aUUID;
    }
}