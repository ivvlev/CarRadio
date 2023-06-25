//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package android.tw.john;

import android.os.Handler;
import android.os.Message;

import com.ivvlev.car.mcu.McuRadioEmulator;

import java.util.ArrayList;
import java.util.Iterator;

public class TWUtil {
    private int mNativeContext = 0;
    private ArrayList<TWUtil.THandler> mTHandler = new ArrayList();
    private McuRadioEmulator mcuRadioEmulator;

    public TWUtil() {
        this.native_init(0);
    }

    public TWUtil(int var1) {
        this.native_init(var1);
    }

    private final void native_init(int var1) {
        mcuRadioEmulator = new McuRadioEmulator(this);
    }


    private final void native_finalize() {
        mcuRadioEmulator = null;
    }

    private int native_open(short[] var1, int var2) {
        return mcuRadioEmulator.native_open(var1, var2);
    }

    private void native_start() {
        mcuRadioEmulator.native_start();
    }

    private void native_stop() {
        mcuRadioEmulator.native_stop();
    }

    private void native_close() {
        mcuRadioEmulator.native_close();
    }


    private int native_write(int what, int arg1, int arg2, Object arg3, Object arg4) {
        if (what == 1025 && arg1 == 0 && arg2 == 0) {
            mcuRadioEmulator.autoscan();
        } else if (what == 1026 && arg1 == 255) {
            mcuRadioEmulator.setFreq(arg2);
        } else if (what == 1026) {
            mcuRadioEmulator.setStationByIndex(arg1, arg2);
        } else if (what == 1025 && arg1 == 1) {
            if (arg2 == 0)
                mcuRadioEmulator.seekNextStation();
            else
                mcuRadioEmulator.seekPrevStation();
        } else if (what == 1025 && arg1 == 2) {
            if (arg2 == 0)
                mcuRadioEmulator.seekNextFreq();
            else
                mcuRadioEmulator.seekPrevFreq();
        } else if (what == 1025 && arg1 == 4) {
            mcuRadioEmulator.setFlagLOC(arg2);
        } else if (what == 1025 && arg1 == 5) {
            mcuRadioEmulator.broadcastFreqRange(arg2);
        } else if (what == 1025 && arg1 == 255) {
            mcuRadioEmulator.broadcastStatusRegisterA();
        } else if (what == 1025 && arg1 == 8) {
            mcuRadioEmulator.storeCurrentFreqAsIndex(arg2);
        } else if (what == 1028 && arg1 == 255) {
            mcuRadioEmulator.init();
        } else if (what == 1029 && arg1 == 255) {
            mcuRadioEmulator.broadcastRdsText();
        } else if (what == 1028 && arg1 == 0) {
            mcuRadioEmulator.setFlagAF(arg2);
        } else if (what == 1028 && arg1 == 1) {
            mcuRadioEmulator.setFlagTA(arg2);
        } else if (what == 1028 && arg1 == 3) {
            mcuRadioEmulator.setFlagREG(arg2);
        } else if (what == 1030 && arg1 == 0) {
            mcuRadioEmulator.broadcastFreqRanges();
        } else if (what == 40465 && arg1 == 192 && arg2 == 1) {
            mcuRadioEmulator.queryAudioFocus();
        } else if (what == 40465 && arg1 == 192 && arg2 == 129) {
            mcuRadioEmulator.releaseAudioFocus();
        } else if (what == 40448) {
            mcuRadioEmulator.setActivity(arg1);
        }
        return 0;
    }

    public void addHandler(String param1, Handler param2) {
        mTHandler.add(new THandler(param1, param2));
    }

    public void close() {
        this.native_close();
    }

    protected void finalize() {
        this.native_finalize();
    }

    public Handler getHandler(String var1) {
        Iterator var2 = this.mTHandler.iterator();

        TWUtil.THandler var3;
        do {
            if (!var2.hasNext()) {
                return null;
            }

            var3 = (TWUtil.THandler) var2.next();
        } while (var3.mTag == null || !var3.mTag.equals(var1));

        return var3.mHandler;
    }

    public int open(short[] var1) {
        return this.open(var1, 0);
    }

    public int open(short[] var1, int var2) {
        return this.native_open(var1, var2);
    }

    public void pollEventFromNative(int param1, int param2, int param3, Object param4, Object param5) {
        // $FF: Couldn't be decompiled
    }

    public void removeHandler(String param1) {
        mTHandler.remove(getHandler(param1));
    }

    public void sendHandler(int var2) {
        this.sendHandler(var2, 0, 0, (Object) null);
    }

    public void sendHandler(int var2, int var3) {
        this.sendHandler(var2, var3, 0, (Object) null);
    }

    public void sendHandler(int var2, int var3, int var4) {
        this.sendHandler(var2, var3, var4, (Object) null);
    }

    public void sendHandler(int what, int var1, int var2, Object param5) {
        Handler handler = getHandler("radio");
        handler.sendMessage(Message.obtain(handler, what, var1, var2, param5));
    }

    public void start() {
        this.native_start();
    }

    public void stop() {
        this.native_stop();
    }

    public int write(int var1) {
        return this.write(var1, 0, 0, (Object) null, (Object) null);
    }

    public int write(int var1, int var2) {
        return this.write(var1, var2, 0, (Object) null, (Object) null);
    }

    public int write(int var1, int var2, int var3) {
        return this.write(var1, var2, var3, (Object) null, (Object) null);
    }

    public int write(int var1, int var2, int var3, Object var4) {
        return this.write(var1, var2, var3, var4, (Object) null);
    }

    public int write(int var1, int var2, int var3, Object var4, Object var5) {
        return this.native_write(var1, var2, var3, var4, var5);
    }

    public class THandler {
        Handler mHandler;
        String mTag;

        public THandler(String var2, Handler var3) {
            this.mTag = var2;
            this.mHandler = var3;
        }
    }

    public class TWObject {
        public Object obj3;
        public Object obj4;

        public TWObject(Object var2, Object var3) {
            this.obj3 = var2;
            this.obj4 = var3;
        }
    }
}
