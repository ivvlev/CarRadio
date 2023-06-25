package com.ivvlev.car.radio.mst768;

import java.util.HashMap;

public class FreqRanges {

    private HashMap<Band, FreqRange> ranges = new HashMap<Band, FreqRange>();

    public HashMap<Band, FreqRange> get() {
        return ranges;
    }

    public FreqRange get(Band key) {
        return ranges.get(key);
    }

    public FreqRange getById(int id) {
        return get(Band.fromId(id));
    }

    public void add(FreqRange freqRange) {
        ranges.put(freqRange.band, freqRange);
    }


}
