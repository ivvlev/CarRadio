package com.ivvlev.car.radio.mst768;

import android.os.Handler;
import android.os.Message;
import android.util.Log;


class RadioHandler extends Handler {

    private final Radio radio;
    private int mRdsFlag = 0;
    private int statusRegisterA = 0;


    RadioHandler(Radio aRadio) {
        radio = aRadio;
    }

    private boolean enabled = false;

    public void handleMessage(Message msg) {

        try {
            //System.out.println("WHAT: " + message.what);
            //System.out.println("ARG1: " + message.arg1);
            //System.out.println("ARG2: " + message.arg2);
            //if (message.what != 265)
            //    System.out.println("OBJ: " + message.obj);
        } catch (Error e) {

        }

        radio.nativeEvent(msg.what, msg.arg1, msg.arg2, msg.obj);

        if (msg.what == 769) {
            // arg1, Audio focus: 0 - released, 1- focused, 3 - pause
            enabled = msg.arg1 == 1;
            switch (msg.arg1) {
                case 0:
                    //radio.queryAudioFocus();
                    break;
                case 1:
                    //radio.queryAudioFocus();
                    break;
                case 3:
                    //radio.queryAudioFocus();
                    break;
            }

        }

        try {
            switch (msg.what) {
                case 513:
                    switch (msg.arg2) {
                        case 19:
                            // Кнопка руля, Стрелка вверх
                            if (msg.arg1 == 1) {
                                radio.nextStation();
                            } else {
                                radio.seekNextStation();
                            }
                            return;
                        case 21:
                            // Кнопка руля, Стрелка вверх
                            if (msg.arg1 == 1) {
                                radio.prevStation();
                            } else {
                                radio.seekPrevStation();
                            }
                            return;
                        case 22:
                            radio.seekNextStation();
                            return;
                        case 23:
                            radio.seekPrevStation();
                            return;
                        case 33:
                        case 37:
                            // Кнопка руля, следущий медиа-источник
                            return;
                        case 76:
                            radio.nextFreqRange();
                            return;
                        case 49:
                        case 50:
                        case 51:
                        case 52:
                        case 53:
                        case 54:
                            if (radio.mActivity == 1 || radio.mActivity == 129) {
                                radio.setStationByIndex((msg.arg2 - 49) + (radio.getCurrentStationGroup() * 6), radio.freqRange.band.id);
                                return;
                            }
                            return;
                        case 84:
                            radio.flag_PTY = false;
//                            showRegTaAfPty(mTPty, radio.flag_PTY);
//                            for (i = 0; i < mFreqPs.length; i++) {
//                                mFreqPs[i].mXPs = null;
//                                showViewHolder(getDropTarget(i));
//                            }
//                            saveName();
                            radio.autoScanLongClick();
                            return;
                        case 96:
                            if (radio.freqRange.band != Band.AM) {
                                radio.toggleFlag_TA();
                            }
                            return;
                        case 97:
                            if (radio.freqRange.band != Band.AM) {
                                radio.toggleFlag_AF();
                            }
                            return;
                        default:
                            return;
                    }
                case 1025:
                    switch (msg.arg1) {
                        case 0:
                            if (statusRegisterA != msg.arg2) {
                                final int updatedBits = statusRegisterA ^ msg.arg2;
                                statusRegisterA = msg.arg2;
                                if ((updatedBits & 0x8) == 0x8) {
                                    boolean flag = ((statusRegisterA & 0x8) == 0x8);
                                    radio.flagChangedDX(flag);
                                }
                                if ((updatedBits & 0x10) == 0x10) {
                                    boolean flag = ((statusRegisterA & 0x10) == 0x10);
                                    radio.flagChangedRDSST(flag);
                                }
                                if ((updatedBits & 0x80) == 0x80 /*&& (statusRegisterA & 0x80) == 0*/) {
                                    boolean flag = ((statusRegisterA & 0x80) == 0x80); // Scanning started - 1, Scanning ended - 0
                                    radio.flagChangedScanning(flag);
//                                    int band = mBand;
//                                    if (!(mBand == 2 || mRegion == 5)) {
//                                        band = 0;
//                                    }
//                                    mTWUtil.write(1026, 0, band);
//                                    mWorkspace.snapToScreen(0);
                                    return;
                                }
                                return;
                            }
                            return;
                        case 1:
                            if (radio.freqRange.band.id != msg.arg2) {
                                int rangeId = msg.arg2; // Band id: 0 - fm1, 1 - fm2, 2 - am
                                radio.freqRangeChanged(rangeId);
                                //mTWUtil.write(1028, 255);
                            }
                            return;
                        case 2:
//                            RadioActivity.this.mFreq = msg.arg2;
//                            RadioActivity.this.mPs = (String) msg.obj;
//                            RadioActivity.this.showRdsDigitPs();
//                            RadioActivity.this.mFreqBar.setFreq(RadioActivity.this.mFreq);
                            int freq = msg.arg2; // Frequency
                            String stationName = String.valueOf(msg.obj); // Station name, from TWUtil
                            radio.frequencyChanged(freq);
                            return;
                        case 3:
                            radio.ptyIndexChanged(msg.arg2); // PTY group number
                            //showSPty
                            return;
                        case 4:
                            radio.selectedIndexChanged(msg.arg2); // Station number
                            //RadioActivity.this.showSFreq(msg.arg2);
                            return;
                        default:
                            return;
                    }
                case 1026:
                    radio.scanning = ((statusRegisterA & 0x80) == 0x80);
//                    if (radio.scanning) {
//                        switch (msg.arg1) {
//                            case 0:
//                                mWorkspace.snapToScreen(0);
//                                break;
//                            case 6:
//                                mWorkspace.snapToScreen(1);
//                                break;
//                            case 12:
//                                mWorkspace.snapToScreen(2);
//                                break;
//                        }
//                    }

                    int index = msg.arg1 & 255;
                    int freq = msg.arg2;
                    int pty = (msg.arg1 >> 8) & 255;
                    String name = (String) msg.obj;
                    radio.stationFound(index, freq, name, pty);
                    return;
                case 1028:
                    if (mRdsFlag != msg.arg1) {

                        final int n6 = mRdsFlag ^ msg.arg1; // XOR
                        mRdsFlag = msg.arg1;

                        if ((n6 & 0x2) == 0x2) { // RDS_TA
                            boolean flag = ((mRdsFlag & 0x2) == 0x2);
                            radio.flagChangedRDSTA(flag);
                        }
                        if ((n6 & 0x4) == 0x4) { // REG
                            boolean flag = ((mRdsFlag & 0x4) == 0x4);
                            radio.flagChangedREG(flag);
                        }
                        if ((n6 & 0x20) == 0x20) { // TA
                            boolean flag = ((mRdsFlag & 0x20) == 0x20);
                            radio.flagChangedTA(flag);
                        }
                        if ((n6 & 0x40) == 0x40) { // AF
                            boolean flag = ((mRdsFlag & 0x40) == 0x40);
                            radio.flagChangedAF(flag);
                        }
                        if ((n6 & 0x80) == 0x80) { // RDS_TP
                            boolean flag = ((mRdsFlag & 0x80) == 0x80);
                            radio.flagChangedRDSTP(flag);
                        }
                    }

                    // PTY id
                    int ptyPosition = (msg.arg2 >> 8) & 0xFF;
                    int currentPtyId = msg.arg2 & 0xFF;
                    radio.foundPTY(ptyPosition, currentPtyId);

                    // Short RDS text
                    String RDSPSText = (msg.obj != null) ? ((String) msg.obj).trim() : "";
                    radio.foundRDSPSText(RDSPSText);


                    return;
                case 1029:
                    String text = (String) msg.obj;
                    radio.foundRDSText(text);
                    return;
                case 1030:
                    switch (msg.arg1) {
                        case 0:
                            radio.region = msg.arg2; // 0 - CHINA, 1 - EUROPE, 2 - USA, 3 - SOUTHEAST ASIA, 4 - SOUTH AMERICA, 5 - EASTERN EUROPE, 6 - JAPAN
                            //showRds();
                            break;
                        case 1: // FM max
                            radio.freqRanges.get(Band.FM1).maxFreq = msg.arg2;
                            radio.freqRanges.get(Band.FM2).maxFreq = msg.arg2;
                            break;
                        case 2: // FM min
                            radio.freqRanges.get(Band.FM1).minFreq = msg.arg2;
                            radio.freqRanges.get(Band.FM2).minFreq = msg.arg2;
                            break;
                        case 3: // AM max
                            radio.freqRanges.get(Band.AM).maxFreq = msg.arg2;
                            break;
                        case 4: // AM min
                            radio.freqRanges.get(Band.AM).minFreq = msg.arg2;
                            break;
                        case 5:
                            radio.freqRanges.get(Band.FM1).step = msg.arg2;
                            radio.freqRanges.get(Band.FM2).step = msg.arg2;
                            int min = 8475;
                            int max = 11085;
                            switch (radio.region) {
                                case 6:
                                    min = 7305;
                                    max = 9202;
                                    break;
                            }
                            //mFreqBar.setConf(RadioActivity.this.mFM1Min, RadioActivity.this.mFM1Max, RadioActivity.this.mFM1Config, true, min, max);
                            return;
                        case 6:
                            radio.freqRanges.get(Band.AM).step = msg.arg2;
//                            if (mBand == 2) {
//                                mFreqBar.setConf(mAMMin, mAMMax, mAMConfig, false, 364, 1882);
//                                return;
//                            }
                            return;
                        case 7:
//                            if (mRds != (msg.arg2 != 0)) {
//                                mRds = msg.arg2 != 0;
//                                showRds();
//                                showRdsPty();
//                                return;
//                            }
                            radio.freqRangesPramsReceived();
                            return;
                        case 8:
                            radio.freqRanges.get(Band.FM2).maxFreq = msg.arg2;
                            return;
                        case 9:
                            radio.freqRanges.get(Band.FM2).minFreq = msg.arg2;
                            return;
                        case 10:
                            radio.freqRanges.get(Band.FM2).step = msg.arg2;
//                            if (mBand == 1 && mRegion == 5) {
//                                mFreqBar.setConf(mFM2Min, mFM2Max, mFM2Config, true, 6370, 7635);
//                                return;
//                            }
                            return;
                        default:
                            return;
                    }
                case 40448:
                    radio.setActivity(msg.arg1);
                    return;
                default:
                    return;
            }
        } catch (Exception e) {
            Log.e("RadioActivity", Log.getStackTraceString(e));
        }
    }
}
