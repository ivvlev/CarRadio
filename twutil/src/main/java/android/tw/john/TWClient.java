//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package android.tw.john;

import android.os.Handler;

public class TWClient {
    public static final int CLEAR_PAIR_INFO = 65285;
    private static final int C_TW = 4;
    public static final int GET_HFP_INFO = 65288;
    public static final int GET_PAIR_INFO = 65283;
    private static final char[] HEX_DIGITS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    public static final int REQUEST_A2DP_SWITCH = 65326;
    public static final int REQUEST_AV_PLAY = 65300;
    public static final int REQUEST_CALL = 65290;
    public static final int REQUEST_HFP = 65286;
    public static final int REQUEST_NAME = 65310;
    public static final int REQUEST_PHONE_BOOK = 65302;
    public static final int REQUEST_PIN = 65312;
    public static final int REQUEST_SEARCH = 65308;
    public static final int REQUEST_SPP = 65318;
    public static final int REQUEST_VOICE_SWITCH = 65292;
    public static final int RETURN_CALL = 65291;
    public static final int RETURN_HFP = 65287;
    public static final int RETURN_HFP_INFO = 65289;
    public static final int RETURN_NAME = 65311;
    public static final int RETURN_PAIRED = 65328;
    public static final int RETURN_PAIR_INFO = 65284;
    public static final int RETURN_PHONE_BOOK = 65303;
    public static final int RETURN_PHONE_BOOK_DATA = 65304;
    public static final int RETURN_PIN = 65313;
    public static final int RETURN_RING = 65327;
    public static final int RETURN_SEARCH = 65309;
    public static final int RETURN_VOICE_SWITCH = 65293;
    public static final int R_CAN = 65280;
    public static final int R_CAN_L = 65281;
    public static final int R_RADIO_BAND = 1;
    public static final int R_RADIO_FLAG = 0;
    public static final int R_RADIO_FREQ = 2;
    public static final int R_RADIO_NORMAL = 65280;
    public static final int R_RADIO_NUMS = 65281;
    public static final int T_BT = 3;
    public static final int T_CAN = 0;
    public static final int T_RADIO = 1;
    public static final int T_SETTINGS = 2;
    public static final int W_BT_CALL = 65281;
    public static final int W_BT_SOURCE = 65535;
    public static final int W_CAN_L = 65281;
    public static final int W_RADIO_AS = 0;
    public static final int W_RADIO_BAND = 4;
    public static final int W_RADIO_DX = 6;
    public static final int W_RADIO_FREQ = 65281;
    public static final int W_RADIO_NEXT = 5;
    public static final int W_RADIO_NORMAL = 65280;
    public static final int W_RADIO_NUM = 65282;
    public static final int W_RADIO_PREV = 3;
    public static final int W_RADIO_SAVE = 7;
    public static final int W_RADIO_SL = 1;
    public static final int W_RADIO_SOURCE = 65535;
    public static final int W_RADIO_SR = 2;
    public static final int W_RADIO_X = 255;
    public static final int W_SETTINGS_UP = 65281;
    private static TWClient[] mTW = new TWClient[4];
    private int mCount = 0;
    private TWUtil mTWUtil = null;
    private int mType = -1;

    private TWClient(int var1) {
        this.mType = var1;
    }

    public static TWClient open(int param0, String param1, String param2, Handler param3) {
        return new TWClient(0);
    }

    public void close(String param1) {
        // $FF: Couldn't be decompiled
    }

    public int write(int param1, int param2, int param3, Object param4) {
        // $FF: Couldn't be decompiled
        return 0;
    }
}
