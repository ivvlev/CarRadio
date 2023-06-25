package com.ivvlev.car.radio.mst768;

import android.os.Handler;
import android.tw.john.TWUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Radio {

    /**
     * Номер страницы в списке радиостанций.
     *
     * @return
     */
    public int getCurrentStationGroup() {
        return 1;
    }

    /**
     * Interface
     */
    public interface NoticeListener {
        void onFrequencyChanged(int freq);

        void onFreqRangeChanged(FreqRange freqRange);

        void onStationSelected(int index);

        void onFreqRangeParamsReceived();

        void onFlagChangedTA(boolean flag);

        void onFlagChangedREG(boolean flag);

        void onFlagChangedAF(boolean flag);

        void onFlagChangedLOC(boolean flag);

        void onFlagChangedRDSTP(boolean flag);

        void onFlagChangedRDSTA(boolean flag);

        void onFlagChangedRDSST(boolean flag);

        void onFlagChangedScanning(boolean flag);

        void onStationFound(int number, int freq, String name, int pty);

        void onFoundPTY(int Id, int requestedId);

        void onFoundRDSPSText(String text);

        void onFoundRDSText(String text);

        void onSetRegionId(int id);

        void onNativeCommand(int what, int arg1, int arg2);

        void onNativeEvent(int what, int arg1, int arg2, Object obj);

        void onPtyChanged(int ptyIndex);
    }

    /**
     * Variables
     */

    public String[] PTYNames = {"None", "News", "Affairs", "Info", "Sport", "Educate", "Drama", "Culture", "Science", "Varied", "Pop Music", "Rock Music", "Easy Music", "Light Music", "Classics", "Other Music", "Weather", "Finance", "Children", "Social", "Religion", "Phone In", "Trave ", "Leisure", "Jazz", "Country", "Nation M", "Oldies", "Folk Music", "Document", "Test", "Alarm"};
    public FreqRanges freqRanges;
    public HashMap<Band, Stations> stationsMap = new HashMap<Band, Stations>(); // Band -> Stations
    /**
     * Активные станции в диапазонах
     */
    //public HashMap<Band, Integer> bandStationIndex = new HashMap<Band, Integer>();

    public FreqRange freqRange;
    public Station station;
    public int freq;
    public int region = 0;
    public int ptyNumber = 0;
    public boolean scanning = true;//После init(); сбросится в false
    public boolean audioFocus = false;
    public boolean scanningFlag = false;
    public boolean flag_PTY = false;
    public boolean flag_REG = false;
    public boolean flag_TA = false;
    public boolean flag_AF = false;
    public boolean flag_LOC = false;
    public boolean flag_RDS_TP = false;
    public boolean flag_RDS_TA = false;
    public boolean flag_RDS_ST = false;

    public int mActivity;

    private int freqAfterFreqRangeChanged = -1; // TODO Use promises
    private int indexAfterFreqRangeChanged = -1; // TODO Use promises

    private Handler handler = new RadioHandler(this);

    private List<NoticeListener> listeners = new ArrayList<>();

    public TWUtil mTWUtil;

    public Radio() {
        // Ranges
        freqRanges = new FreqRanges();
        freqRanges.add(new FreqRange(Band.FM1, 8750, 10800, 10, "MHz"));
        freqRanges.add(new FreqRange(Band.FM2, 6500, 7500, 10, "MHz"));
        freqRanges.add(new FreqRange(Band.AM, 531, 1629, 9, "KHz"));

        freqRange = freqRanges.get(Band.FM1);
        freq = freqRange.minFreq;

        for (Band band : Band.values()) {
            Stations stations = new Stations();
            clearStationList(band, stations);
            stationsMap.put(band, stations);
        }
    }

    public void clearStationList(Band band, Stations stations) {
        stations.clear();
        for (int index = 0; index < 18; index++) {
            FreqRange freqRange = freqRanges.get(band);
            stations.add(new Station(
                    freqRange.formatFrequencyValue(freqRange.minFreq, true),
                    freqRange.minFreq,
                    band.id,
                    0));
        }
    }

    /**
     * Init radio
     */
    public void init() {

        if (mTWUtil != null)
            return;

        mTWUtil = new TWUtil(1); // Radio
        short[] initialSequence = new short[]{(short) 513, (short) 1025, (short) 1026, (short) 1028, (short) 1029, (short) 1030, (short) -25088};
        if (mTWUtil.open(initialSequence) == 0) {
            mTWUtil.start();
            mTWUtil.addHandler("radio", handler);
            resume();
        } else {
            // finish();
        }
    }

    /**
     * Stop radio
     */
    public void stop() {
        if (mTWUtil == null)
            return;
        releaseAudioFocus();
        this.mTWUtil.stop();
        this.mTWUtil.close();
        this.mTWUtil = null;
    }

    /**
     * Pause
     */
    public void pause() {
        requestService(129);
        //mTWUtil.removeHandler("radio");
        //releaseAudioFocus();
    }

    /**
     * Resume
     */
    public void resume() {
        requestService(1);
        queryAudioFocus();
        this.twWrite(1030, 0); //upadate region
        this.twWrite(1025, 255);
        this.twWrite(1028, 255);
        this.twWrite(1029, 255);
        //queryAudioFocus();
    }

    public void requestService(int activity) {
        this.mActivity = activity;
        this.twWrite(40448, activity);
    }

    public void setActivity(int activity) {
        mActivity = activity;
    }

    /**
     * Set radio region
     *
     * @param id
     */
    public void setRegion(int id) {
        twWrite(265, 0, id);
    }

    /**
     * Set frequency range by its name
     *
     * @param aBand (String)
     * @return FreqRange
     */
    public void setFreqRangeByName(Band aBand) {
        twWrite(1025, 5, freqRanges.get(aBand).band.id);
    }

    /**
     * Set frequency range by its id
     *
     * @param aId (String)
     * @return FreqRange
     */
    public void setFreqRangeById(int aId) {
        twWrite(1025, 5, freqRanges.getById(aId).band.id);
    }

    /**
     * Запоминает текущую частоту станции в ячейке памяти под индексом index.
     *
     * @param index Индекс станции
     */
    public void storeCurrentFreq(int index) {
        twWrite(1025, 8, index);
    }


    /**
     * Request current frequency range parameters (min, max, step etc)
     */
    public void getFreqRangeParams() {
        twWrite(1030, 1);
    }

    /**
     * Set frequency
     *
     * @param aFreq (int)
     */
    public void setFreq(int aFreq) {
        if (freq != aFreq) {
            if (aFreq > freqRange.maxFreq) {
                aFreq = freqRange.maxFreq;
            }
            if (aFreq < freqRange.minFreq) {
                aFreq = freqRange.minFreq;
            }
            freq = aFreq;
            twWrite(1026, 255, aFreq);
        } else {
            frequencyChanged(freq);
        }
    }

    /**
     * Set station
     * Used to set range and frequency
     *
     * @param aFreq
     */
    public void setStation(int aFreqRangeId, int aFreq) {
        if (freqRange == null || aFreqRangeId != freqRange.band.id) {
            setFreqRangeById(aFreqRangeId);
            freqAfterFreqRangeChanged = aFreq;
        } else {
            setFreq(aFreq);
            freqAfterFreqRangeChanged = -1;
        }
    }

    public void setStationByIndex(int aFreqRangeId, int aIndex) {
        if (freqRange == null || aFreqRangeId != freqRange.band.id) {
            setFreqRangeById(aFreqRangeId);
            indexAfterFreqRangeChanged = aIndex;
        } else {
            freqAfterFreqRangeChanged = -1;
            this.twWrite(1026, aIndex, aFreqRangeId);
        }
    }


    public void autoScanLongClick() {
        twWrite(1025, 0, 0);
    }

    /**
     * Start auto-scan
     */
    public void autoScan() {
        twWrite(1025, 3, 0);
    }

    public void toggleFlag_REG() {
        flag_REG = !flag_REG;
        setFlag_REG(flag_REG);
    }

    public void toggleFlag_TA() {
        flag_TA = !flag_TA;
        setFlag_TA(flag_TA);
    }

    public void toggleFlag_AF() {
        flag_AF = !flag_AF;
        setFlag_AF(flag_AF);
    }

    public void toggleFlag_LOC() {
        flag_LOC = !flag_LOC;
        setFlag_LOC(flag_LOC);
    }

    public void toggleFlag_PTY() {
        flag_PTY = !flag_PTY;
    }

    public void setFlag_LOC(boolean flag) {
        twWrite(1025, 4, (flag ? 1 : 0));
    }

    public void setFlag_PTY(boolean flag) {
        twWrite(1025, 4, (flag ? 1 : 0));
    }

    public void setFlag_AF(boolean flag) {
        twWrite(1028, 0, (flag ? 1 : 0));
    }

    public void setFlag_TA(boolean flag) {
        twWrite(1028, 1, (flag ? 1 : 0));
    }

    public void setFlag_REG(boolean flag) {
        twWrite(1028, 3, (flag ? 1 : 0));
    }

    /**
     * Query audio focus for radio
     */
    public void queryAudioFocus() {
        audioFocus = true;
        //twWrite(769, 192, 1);
        twWrite(40465, 192, 1);
    }

    /**
     * Release audio focus for radio
     */
    public void releaseAudioFocus() {
        audioFocus = false;
        //twWrite(769, 192, 0);
        twWrite(40465, 192, 129);
    }

    /**
     * Get audio focus flag
     *
     * @return
     */
    public boolean isAudioFocus() {
        return audioFocus;
    }

    /**
     * Seek next station
     */
    public void seekNextStation() {
        twWrite(1025, 1, 0);
    }

    /**
     * Seek prev station
     */
    public void seekPrevStation() {
        twWrite(1025, 1, 1);
    }

    /**
     * Do one freq-step forward
     */
    public void seekNextFreq() {
        twWrite(1025, 2, 0);
    }

    /**
     * Do one freq-step backward
     */
    public void seekPrevFreq() {
        twWrite(1025, 2, 1);
    }

    public void nextFreqRange() {

    }

    public void nextStation() {
        Stations stations = stationsMap.get(freqRange.band);
        int size = stations.size();
        if (size > 0) {
            int index = (station != null) ? stations.findIdByUUID(station.uuid) + 1 : 0;
            index = index >= size ? 0 : index;
            setStationByIndex(freqRange.band.id, index);
        }
    }

    public void prevStation() {
        Stations stations = stationsMap.get(freqRange.band);
        int size = stations.size();
        if (size > 0) {
            int index = (station != null) ? stations.findIdByUUID(station.uuid) - 1 : 0;
            index = index < 0 ? size - 1 : index;
            setStationByIndex(freqRange.band.id, index);
        }
    }


    /**
     * Notice Listener methods (callbacks)
     */

    public void twWrite(int var1, int var2) {
        nativeCommand(var1, var2, -1);
        mTWUtil.write(var1, var2);
    }

    public void twWrite(int var1, int var2, int var3) {
        nativeCommand(var1, var2, var3);
        mTWUtil.write(var1, var2, var3);
    }

    /**
     * Add listener
     *
     * @param listener
     */
    public void addListener(NoticeListener listener) {
        listeners.add(listener);
    }

    /**
     * Frequency changed (RadioHandler)
     *
     * @param aFreq
     */
    public void frequencyChanged(int aFreq) {
        freq = aFreq;
        station = null;
        for (NoticeListener listener : listeners) {
            listener.onFrequencyChanged(aFreq);
        }
    }

    public void selectedIndexChanged(int index) {
        Stations stations = stationsMap.get(freqRange.band);
        if (index == 31) {
            int i = stations.findIdByFreq(freq);
            if (i >= 0){
                index = i;
                station = stations.get(index);
            }
        } else {
            station = stations.get(index);
        }
        for (NoticeListener listener : listeners) {
            listener.onStationSelected(index);
        }
    }

    public void ptyIndexChanged(int ptyIndex) {
        ptyNumber = ptyIndex;
        for (NoticeListener listener : listeners) {
            listener.onPtyChanged(ptyIndex);
        }
    }

    /**
     * Frequency Range Changed (RadioHandler)
     *
     * @param aRangeId
     */
    public void freqRangeChanged(int aRangeId) {
        if (freqAfterFreqRangeChanged > 0) {
            setFreq(freqAfterFreqRangeChanged);
            freqAfterFreqRangeChanged = -1;
        } else if (indexAfterFreqRangeChanged >= 0) {
            setStationByIndex(aRangeId, indexAfterFreqRangeChanged);
            indexAfterFreqRangeChanged = -1;
        }

        freqRange = freqRanges.getById(aRangeId);
        for (NoticeListener listener : listeners) {
            listener.onFreqRangeChanged(freqRange);
        }
    }

    /**
     * Frequency Range Changed (RadioHandler)
     */
    public void freqRangesPramsReceived() {
        if (freqRange != null)
            for (NoticeListener listener : listeners) {
                listener.onFreqRangeParamsReceived();
            }
    }

    /**
     * TA changed
     *
     * @param flag
     */
    public void flagChangedTA(boolean flag) {
        flag_TA = flag;
        for (NoticeListener listener : listeners) {
            listener.onFlagChangedTA(flag);
        }
    }

    /**
     * REG changed
     *
     * @param flag
     */
    public void flagChangedREG(boolean flag) {
        flag_REG = flag;
        for (NoticeListener listener : listeners) {
            listener.onFlagChangedREG(flag);
        }
    }

    /**
     * AF changed
     *
     * @param flag
     */
    public void flagChangedAF(boolean flag) {
        flag_AF = flag;
        for (NoticeListener listener : listeners) {
            listener.onFlagChangedAF(flag);
        }
    }

    /**
     * DX changed (DX/LOC)
     *
     * @param flag
     */
    public void flagChangedDX(boolean flag) {
        flag_LOC = flag;
        for (NoticeListener listener : listeners) {
            listener.onFlagChangedLOC(flag);
        }
    }

    /**
     * RDS_TP changed
     *
     * @param flag
     */
    public void flagChangedRDSTP(boolean flag) {
        flag_RDS_TP = flag;
        for (NoticeListener listener : listeners) {
            listener.onFlagChangedRDSTP(flag);
        }
    }

    /**
     * RDS_TA changed
     *
     * @param flag
     */
    public void flagChangedRDSTA(boolean flag) {
        flag_RDS_TA = flag;
        for (NoticeListener listener : listeners) {
            listener.onFlagChangedRDSTA(flag);
        }
    }

    /**
     * PTY Id found
     * PTY scanning use: mTWUtil(1028, 4, PTY_Id_To_find);
     *
     * @param PTYId
     * @param requestedPTYId
     */
    public void foundPTY(int PTYId, int requestedPTYId) {
        for (NoticeListener listener : listeners) {
            listener.onFoundPTY(PTYId, requestedPTYId);
        }
    }

    /**
     * RDS_PS text found
     * a part of the previous one
     *
     * @param text
     */
    public void foundRDSPSText(String text) {
        for (NoticeListener listener : listeners) {
            listener.onFoundRDSPSText(text);
        }
    }

    /**
     * RDS long message
     *
     * @param text
     */
    public void foundRDSText(String text) {
        for (NoticeListener listener : listeners) {
            listener.onFoundRDSText(text);
        }
    }

    /**
     * RDS_ST changed
     *
     * @param flag
     */
    public void flagChangedRDSST(boolean flag) {
        flag_RDS_ST = flag;
        for (NoticeListener listener : listeners) {
            listener.onFlagChangedRDSST(flag);
        }
    }

    /**
     * Scanning flag changed
     *
     * @param flag
     */
    public void flagChangedScanning(boolean flag) {
        scanningFlag = flag;
        for (NoticeListener listener : listeners) {
            listener.onFlagChangedScanning(flag);
        }
    }

    /**
     * Station found (scanning)
     *
     * @param aNumber
     * @param aFreq
     * @param aName
     */
    public void stationFound(int aNumber, int aFreq, String aName, int aPty) {
        Stations stations = stationsMap.get(freqRange.band);
        Station station = stations.get(aNumber);
        station.freq = aFreq;
        station.name = aName;
        station.pty = aPty;

        for (NoticeListener listener : listeners) {
            listener.onStationFound(aNumber, aFreq, aName, aPty);
        }
    }

    public void nativeCommand(int what, int arg1, int arg2) {
        for (NoticeListener listener : listeners) {
            listener.onNativeCommand(what, arg1, arg2);
        }
    }

    public void nativeEvent(int what, int arg1, int arg2, Object obj) {
        for (NoticeListener listener : listeners) {
            listener.onNativeEvent(what, arg1, arg2, obj);
        }
    }

    /**
     * Received region id
     *
     * @param id
     */
    public void setRegionId(int id) {
        region = id;
        for (NoticeListener listener : listeners) {
            listener.onSetRegionId(id);
        }
    }

}
