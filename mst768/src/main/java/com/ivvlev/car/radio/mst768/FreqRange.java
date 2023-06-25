package com.ivvlev.car.radio.mst768;

import java.util.Locale;

public class FreqRange {
    public int minFreq;
    public int maxFreq;
    public int step;
    public Band band;
    public String units;

    public FreqRange(Band band, int minFreq, int maxFreq, int step, String units) {
        this.band = band;
        this.minFreq = minFreq;
        this.maxFreq = maxFreq;
        this.step = step;
        this.units = units;
    }

    public int testFrequency(int freq) {
        freq = (freq > maxFreq) ? maxFreq : freq;
        freq = (freq < minFreq) ? minFreq : freq;
        return freq;
    }

    public String formatFrequencyValue(int freq, boolean isShort) {
        double dFreq;
        String sFreq;
        switch (band) {
            case FM1:
            case FM2:
                dFreq = freq / 100.0;
                sFreq = String.format(Locale.getDefault(), "%.2f", dFreq);
                if (isShort && sFreq.endsWith("0"))
                    sFreq = sFreq.substring(0, sFreq.length() - 1);
                if (isShort && sFreq.endsWith(".0"))
                    sFreq = sFreq.substring(0, sFreq.length() - 2);
                break;
            default:
                dFreq = freq / 1.0;
                sFreq = String.format(Locale.getDefault(), "%.0f", dFreq);
        }
        sFreq = sFreq + " " + units;
        return sFreq;
    }

}
