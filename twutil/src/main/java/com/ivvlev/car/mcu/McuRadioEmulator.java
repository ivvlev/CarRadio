package com.ivvlev.car.mcu;

import android.tw.john.TWUtil;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class McuRadioEmulator {

    private TWUtil twUtil;
    private int mActivity;
    private int mStatusRegisterA = 0;
    private int mRdsFlag = 0;
    private int mCurrentFreq = 8750;
    private int mCurrentStationIndex = 0;
    private List<StationInfo> mStationList = new ArrayList<>();

    public McuRadioEmulator(TWUtil twUtil) {
        this.twUtil = twUtil;
        mStationList.add(new StationInfo(0, 8750, "Дорожное радио"));
        mStationList.add(new StationInfo(1, 9060, "90.6 МГц"));
        mStationList.add(new StationInfo(2, 10200, "102 МГц"));
        mStationList.add(new StationInfo(3, 10240, "102.4 МГц"));
        mStationList.add(new StationInfo(4, 10280, "102.8 МГц"));
        mStationList.add(new StationInfo(5, 10400, "104 МГц"));
        mStationList.add(new StationInfo(6, 10630, "106.3 МГц"));
        mStationList.add(new StationInfo(7, 10640, "106.4 МГц"));
        mStationList.add(new StationInfo(8, 10650, "106.5 МГц"));
        mStationList.add(new StationInfo(9, 10660, "106.6 МГц"));
        mStationList.add(new StationInfo(10, 10710, "107.1 МГц"));
        mStationList.add(new StationInfo(11, 10720, "107.2 МГц"));
        mStationList.add(new StationInfo(12, 10730, "107.3 МГц"));
        mStationList.add(new StationInfo(13, 10740, "107.4 МГц"));
        mStationList.add(new StationInfo(14, 10750, "107.5 МГц"));
        mStationList.add(new StationInfo(15, 10760, "107.6 МГц"));
        mStationList.add(new StationInfo(16, 10770, "107.7 МГц"));
        mStationList.add(new StationInfo(17, 10780, "107.8 МГц"));
    }

    private final String LOG_TAG = getClass().getSimpleName();

    public void queryAudioFocus() {
        Log.d(LOG_TAG, "queryAudioFocus()");
    }

    public void releaseAudioFocus() {
        Log.d(LOG_TAG, "releaseAudioFocus()");
    }

    public void native_start() {
        Log.d(LOG_TAG, "native_start()");
    }

    public int native_open(short[] var1, int var2) {
        Log.d(LOG_TAG, "native_open()");
        return 0;
    }

    public void native_stop() {
        Log.d(LOG_TAG, "native_stop()");
    }

    public void native_close() {
        Log.d(LOG_TAG, "native_close()");
        twUtil = null;
    }

    public void autoscan() {
        broadcastScanFlagChanged(true);
        broadcastStationList();
        broadcastScanFlagChanged(false);
    }

    public void broadcastStationList() {
        for (StationInfo stationInfo : mStationList) {
            broadcastStation(stationInfo);
        }
    }

    public void broadcastStation(StationInfo stationInfo) {
        twUtil.sendHandler(1026, stationInfo.index, stationInfo.freq, stationInfo.caption);
    }

    public void setFreq(int freq) {
        mCurrentFreq = freq;
        broadcastRdsText();
        broadcastStatusRegisterA();
        broadcastFreq(freq);
        broadcastStationUnselected();
    }

    public void storeCurrentFreqAsIndex(int index) {
        mStationList.get(index).freq = mCurrentFreq;
        mStationList.get(index).caption = String.format("%.2f MHz", mCurrentFreq / 100d );
        broadcastStation(mStationList.get(index));
    }

    private int mFlags = 0x0;

    public void setFlagREG(int var3) {
        mFlags = (var3 == 1 ? mFlags | 0x4 : mFlags ^ 0x4);
        twUtil.sendHandler(1028, mFlags, 0, "");
    }

    public void setFlagLOC(int var3) {
        mFlags = (var3 == 1 ? mFlags | 0x8 : mFlags ^ 0x8);
        twUtil.sendHandler(1025, 0, mFlags, "");
    }

    public void setFlagTA(int var3) {
        mFlags = (var3 == 1 ? mFlags | 0x20 : mFlags ^ 0x20);
        twUtil.sendHandler(1028, mFlags, 0, "");
    }

    public void setFlagAF(int var3) {
        mFlags = (var3 == 1 ? mFlags | 0x40 : mFlags ^ 0x40);
        twUtil.sendHandler(1028, mFlags, 0, "");
    }


    public void broadcastFreq(int freq) {
        mCurrentFreq = freq;
        twUtil.sendHandler(1025, 2, freq, "");
    }

    public void broadcastRdsFlags() {
        twUtil.sendHandler(1028, mRdsFlag, 0, null);
    }


    public void broadcastStatusRegisterA(String rdsText) {
        twUtil.sendHandler(1028, mStatusRegisterA, 0, rdsText);
    }

    public void broadcastRdsMsg() {
        twUtil.sendHandler(1029, 0, 0, "");
    }

    public void broadcastStatusRegisterA() {
        broadcastStatusRegisterA(null);
    }

    /**
     * 1029
     */
    public void broadcastRdsText() {
        twUtil.sendHandler(1029, 0, 0, "RDS Text");
    }

    public void broadcastFreqRange(int rangeId) {
        twUtil.sendHandler(1025, 1, rangeId, null);
    }

    public void broadcastScanFlagChanged(boolean flag) {
        twUtil.sendHandler(1025, 0, flag ? 0x0 : 0x80);
    }

    public void setStationByIndex(int index, int freqRange) {
        mCurrentStationIndex = index;
        broadcastRdsMsg();
        broadcastStatusRegisterA(mStationList.get(index).caption);
        twUtil.sendHandler(1025, 3, 10, null);//PTY Number
        broadcastFreq(mStationList.get(index).freq);
        broadcastStationSelected(index);
    }

    private void broadcastStationUnselected() {
        broadcastStationSelected(31);
    }

    private void broadcastStationSelected(int index) {
        twUtil.sendHandler(1025, 4, index, null);
    }

    public void setActivity(int activity) {
        mActivity = activity;
    }

    public void broadcastFreqRanges() {
        twUtil.sendHandler(1030, 0, 5, null);
        twUtil.sendHandler(1030, 1, 10800, null);
        twUtil.sendHandler(1030, 2, 8750, null);
        twUtil.sendHandler(1030, 3, 11337, null);
        twUtil.sendHandler(1030, 4, 8175, null);
        twUtil.sendHandler(1030, 5, 10, null);
        twUtil.sendHandler(1030, 6, 5, null);
        twUtil.sendHandler(1030, 7, 0, null); //RDS
        twUtil.sendHandler(1030, 8, 7500, null);
        twUtil.sendHandler(1030, 9, 6500, null);
        twUtil.sendHandler(1030, 10, 5, null);
    }

    public void seekNextFreq() {
        mCurrentFreq = mCurrentFreq + 5;
        broadcastFreq(mCurrentFreq);
        broadcastStationUnselected();
    }

    public void seekPrevFreq() {
        mCurrentFreq = mCurrentFreq - 5;
        broadcastFreq(mCurrentFreq);
        broadcastStationUnselected();
    }

    public void seekNextStation() {
        mCurrentStationIndex++;
        if (mCurrentStationIndex >= 18) {
            mCurrentStationIndex = 0;
        }
        broadcastFreq(mStationList.get(mCurrentStationIndex).freq);
    }

    public void seekPrevStation() {
        mCurrentStationIndex--;
        if (mCurrentStationIndex < 0) {
            mCurrentStationIndex = 17;
        }
        broadcastFreq(mStationList.get(mCurrentStationIndex).freq);
    }

    public void init() {
        broadcastRdsFlags();
        broadcastFreqRanges();
        broadcastStatusRegisterA();
        broadcastFreqRange(0);
        broadcastStationList();
        broadcastScanFlagChanged(false);
        broadcastFreq(mCurrentFreq);
        broadcastStationUnselected();
    }

    private static class StationInfo {
        public int index;
        public int freq;
        public String caption;

        public StationInfo(int index, int freq, String caption) {
            this.index = index;
            this.freq = freq;
            this.caption = caption;
        }
    }
}
