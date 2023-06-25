package com.ivvlev.car.radio.mst768;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;

import com.google.gson.Gson;

public class RadioService extends Service implements Radio.NoticeListener {

    private Gson mGson = null;
    private Radio mRadio = null;

    private BroadcastReceiver mIntentReceiver = null;
    private ServiceBinder mServiceBinder;

    //    private FreqRange mFreqRange = null;
//    private Station mStation = null; // Current frequency range
//    private String mListName = null; // Current station list FM1, FM2, AM,
//    private int mFreq = -1; // Current freq
    private boolean audioFocus = false;

    // Service vars and params
    private boolean started = false;

    public RadioService() {

        mServiceBinder = new ServiceBinder();

        mIntentReceiver = new BroadcastReceiver() {
            public void onReceive(final Context context, final Intent intent) {

                final String action = intent.getAction();

                if (Constants.BROADCAST_ACTION_SET_FREQ.equals(action)) {

                } else if (Constants.BROADCAST_ACTION_PREV_STATION.equals(action)) {
                    prevStation();
                } else if (Constants.BROADCAST_ACTION_NEXT_STATION.equals(action)) {
                    nextStation();
                } else if (Constants.BROADCAST_ACTION_SET_BAND.equals(action)) {

                } else if (Constants.BROADCAST_ACTION_RADIO_QUERY_AUDIO_FOCUS.equals(action)) {
                    queryAudioFocus();
                } else if (Constants.BROADCAST_ACTION_RADIO_RELEASE_AUDIO_FOCUS.equals(action)) {
                    releaseAudioFocus();
                } else if (Constants.BROADCAST_ACTION_REFRESH_PREFERENCES.equals(action)) {
                    initPreferenceValues();
                } else if (Constants.BROADCAST_ACTION_REFRESH_STATION_LIST.equals(action)) {
                    refreshStationList();
                } else if (Constants.BROADCAST_ACTION_REFRESH_STATION.equals(action)) {
                    refreshStation();
                }
            }
        };

    }

    @Override
    public IBinder onBind(Intent intent) {
        return mServiceBinder;
    }

    @Override
    public boolean onUnbind(final Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {

        // Gson serializer/deserializer //
        mGson = new Gson();

        // Preferences //
        //mPreferences = new RadioSharedPreferences(this);

        // IntentFilter //
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.BROADCAST_ACTION_SET_FREQ);
        intentFilter.addAction(Constants.BROADCAST_ACTION_PREV_STATION);
        intentFilter.addAction(Constants.BROADCAST_ACTION_NEXT_STATION);
        intentFilter.addAction(Constants.BROADCAST_ACTION_SET_BAND);
        intentFilter.addAction(Constants.BROADCAST_ACTION_REFRESH_PREFERENCES);
        intentFilter.addAction(Constants.BROADCAST_ACTION_REFRESH_STATION);
        intentFilter.addAction(Constants.BROADCAST_ACTION_REFRESH_STATION_LIST);
        intentFilter.addAction(Constants.BROADCAST_ACTION_RADIO_QUERY_AUDIO_FOCUS);
        intentFilter.addAction(Constants.BROADCAST_ACTION_RADIO_RELEASE_AUDIO_FOCUS);
        this.registerReceiver(mIntentReceiver, intentFilter);

        // Initial values //
        initPreferenceValues();

        // Service started
        started = true;
        sendBroadcast(Constants.BROADCAST_INFO_STARTED);

    }

    @Override
    public void onDestroy() {
        started = false;
        stop();
        unregisterReceiver(mIntentReceiver);
        stopForeground(true);
        System.out.println("Destroy");
    }

    @Override
    public int onStartCommand(final Intent intent, final int n, final int n2) {

        // Notify & start foreground
//        mNotifyData = new  NotifyData( getApplicationContext() );
//        startForeground( NotifyData.NOTIFY_ID, mNotifyData.show());

        // Kill stock radio and service
        ActivityManager activityManager = (ActivityManager) getBaseContext().getSystemService(Context.ACTIVITY_SERVICE);
        activityManager.killBackgroundProcesses("com.tw.radio");
        activityManager.killBackgroundProcesses("com.tw.radio:RadioService");

        if (intent != null && started) {

            if (mRadio == null) {
                init();
            }

            final String action = intent.getAction();
            if (Constants.BROADCAST_ACTION_SET_FREQ.equals(action)) {

            } else if (Constants.BROADCAST_ACTION_PREV_STATION.equals(action)) {
                if (!audioFocus)
                    queryAudioFocus();
                prevStation();
            } else if (Constants.BROADCAST_ACTION_NEXT_STATION.equals(action)) {
                if (!audioFocus)
                    queryAudioFocus();
                nextStation();
            } else if (Constants.BROADCAST_ACTION_SET_BAND.equals(action)) {

            } else if (Constants.BROADCAST_ACTION_RADIO_QUERY_AUDIO_FOCUS.equals(action)) {
                queryAudioFocus();
            } else if (Constants.BROADCAST_ACTION_REFRESH_PREFERENCES.equals(action)) {
                initPreferenceValues();
            } else if (Constants.BROADCAST_ACTION_REFRESH_STATION_LIST.equals(action)) {
                refreshStationList();
            } else if (Constants.BROADCAST_ACTION_REFRESH_STATION.equals(action)) {
                refreshStation();
            } else if (Constants.BROADCAST_ACTION_SWITCH_STATION_LIST_TO_FM1.equals(action)) {
                setStationListBand(Band.FM1);
            } else if (Constants.BROADCAST_ACTION_SWITCH_STATION_LIST_TO_FM2.equals(action)) {
                setStationListBand(Band.FM2);
            } else if (Constants.BROADCAST_ACTION_SWITCH_STATION_LIST_TO_AM.equals(action)) {
                setStationListBand(Band.AM);
            }
        }
        return Service.START_STICKY;
    }


    /**
     * Private
     */

    public void queryAudioFocus() {
        audioFocus = true;
        mRadio.queryAudioFocus();
    }

    public void releaseAudioFocus() {
        audioFocus = false;
        mRadio.releaseAudioFocus();
    }

    private void initPreferenceValues() {

    }

    /**
     * Send broadcast messages
     *
     * @param action
     * @param o,     i, s or b
     */
    private void sendBroadcast(String action, Object o) {
        if (started)
            sendBroadcast(new Intent(action).putExtra("value", mGson.toJson(o)).putExtra("type", "object"));
    }

    private void sendBroadcast(String action, Integer i) {
        sendBroadcast(action, (int) i);
    }

    private void sendBroadcast(String action, int i) {
        if (started)
            sendBroadcast(new Intent(action).putExtra("value", i).putExtra("type", "integer"));
    }

    private void sendBroadcast(String action, String s) {
        if (started)
            sendBroadcast(new Intent(action).putExtra("value", s).putExtra("type", "string"));
    }

    private void sendBroadcast(String action, boolean b) {
        if (started)
            sendBroadcast(new Intent(action).putExtra("value", b).putExtra("type", "boolean"));
    }

    private void sendBroadcast(String action) {
        if (started)
            sendBroadcast(new Intent(action));
    }


    /**
     *
     * Radio Events
     *
     */

    /**
     * Frequency changed event
     *
     * @param freq
     */
    @Override
    public void onFrequencyChanged(int freq) {

        sendBroadcast(Constants.BROADCAST_INFO_FREQUENCY, freq);

        Stations stations = getStationList();
        int index = stations.findIdByFreq(freq);
        if (index > -1) {
            sendBroadcast(Constants.BROADCAST_INFO_STATION_LIST_SELECTION, index);
        }

        // Notify
        //onStationSelected(index);

        // Save
//            mBandIndexes.put(mListName, index);
//            mPreferences.putIndexes(mBandIndexes);
//            mPreferences.putFrequency(mFreq);
    }


    /**
     * Frequency range changed event
     *
     * @param freqRange
     */
    @Override
    public void onFreqRangeChanged(FreqRange freqRange) {
        sendBroadcast(Constants.BROADCAST_INFO_FREQUENCY_RANGE, freqRange);
    }


    @Override
    public void onStationSelected(int index) {
        sendBroadcast(Constants.BROADCAST_INFO_STATION_LIST_SELECTION, index);
    }

    /**
     * Frequency range received
     */
    public void onFreqRangeParamsReceived() {
        FreqRange freqRange = mRadio.freqRanges.get(mRadio.freqRange.band);
        sendBroadcast(Constants.BROADCAST_INFO_FREQUENCY_RANGE, freqRange);
    }

    /**
     * TA flag was changed event
     *
     * @param flag
     */
    @Override
    public void onFlagChangedTA(boolean flag) {
        sendBroadcast(Constants.BROADCAST_INFO_FLAG_TA, flag);
    }

    /**
     * REG flag was changed event
     *
     * @param flag
     */
    @Override
    public void onFlagChangedREG(boolean flag) {
        sendBroadcast(Constants.BROADCAST_INFO_FLAG_REG, flag);
    }

    /**
     * AF flag changed
     *
     * @param flag
     */
    @Override
    public void onFlagChangedAF(boolean flag) {
        sendBroadcast(Constants.BROADCAST_INFO_FLAG_AF, flag);
    }

    /**
     * DX flag changed
     *
     * @param flag
     */
    @Override
    public void onFlagChangedLOC(boolean flag) {
        sendBroadcast(Constants.BROADCAST_INFO_FLAG_LOC, flag);
    }

    /**
     * RDS_ST flag changed
     *
     * @param flag
     */
    @Override
    public void onFlagChangedRDSST(boolean flag) {
        sendBroadcast(Constants.BROADCAST_INFO_FLAG_RDS_ST, flag);
    }

    /**
     * RDS_TP changed
     *
     * @param flag
     */
    @Override
    public void onFlagChangedRDSTP(boolean flag) {
        sendBroadcast(Constants.BROADCAST_INFO_FLAG_RDS_TP, flag);
    }

    /**
     * RDS_TA changed
     *
     * @param flag
     */
    @Override
    public void onFlagChangedRDSTA(boolean flag) {
        sendBroadcast(Constants.BROADCAST_INFO_FLAG_RDS_TA, flag);
    }

    /**
     * PTY info found
     *
     * @param id
     * @param requestedId
     */
    @Override
    public void onFoundPTY(int id, int requestedId) {
        sendBroadcast(Constants.BROADCAST_INFO_RDS_PTY_ID, id);
        sendBroadcast(Constants.BROADCAST_INFO_RDS_PTY_NAME, id == 0 ? "" : mRadio.PTYNames[id]);
    }

    /**
     * RDS_ST text found
     *
     * @param text
     */
    @Override
    public void onFoundRDSPSText(String text) {
        sendBroadcast(Constants.BROADCAST_INFO_RDS_PS, text);
    }

    /**
     * RDS message, a long one
     *
     * @param text
     */
    @Override
    public void onFoundRDSText(String text) {
        sendBroadcast(Constants.BROADCAST_INFO_RDS_TEXT, text);
    }

    /**
     * Scanning flag changed
     *
     * @param flag
     */
    @Override
    public void onFlagChangedScanning(boolean flag) {

        // On radio init mTWUtil returns flag - false
        // We need to ignore it

        // Scanning begin
//        if (flag) {
//            mScanning = true;
//            mStationList.get(mListName).clear();
//        }
//
//        // Scanning end
//        if (mScanning && !flag) {
//            mScanning = false;
//            onStationListChanged();
//            if (getStationList().size() > 0) {
//                setStation(0, getStationList().get(0));
//            }
//            // Save
//            mPreferences.putStationList(mListName, getStationList());
//        }

        sendBroadcast(Constants.BROADCAST_INFO_FLAG_SCANNING, flag);
        onStationListChanged(getStationList());
    }

    /**
     * Station found (scanning)
     *
     * @param number
     * @param freq
     * @param name
     */
    @Override
    public void onStationFound(int number, int freq, String name, int pty) {

//        // Station
//        Station station = new Station(name, freq, mFreqRange.band.id, pty);
        //Station station = mRadio.stationsMap.get(mRadio.freqRange.band).get(number);
        // Send broadcast
        //sendBroadcast(Constants.BROADCAST_INFO_SCANNING_FOUND_STATION, station);

        onStationListChanged(getStationList());
//        // Add station to the station list
//        mStationList.get(mListName).add(station);
    }

    /**
     * Region Id
     *
     * @param id
     */
    public void onSetRegionId(int id) {
//        mRegionId = id;
        sendBroadcast(Constants.BROADCAST_INFO_REGION_ID, id);
        mRadio.getFreqRangeParams();
//        mPreferences.putRegionId(id);
    }

    @Override
    public void onNativeCommand(int what, int arg1, int arg2) {

    }

    @Override
    public void onNativeEvent(int what, int arg1, int arg2, Object obj) {

    }

    @Override
    public void onPtyChanged(int ptyIndex) {

    }

    /**
     * Service binder
     */

    public class ServiceBinder extends Binder {
        public RadioService getService() {
            return RadioService.this;
        }
    }

    public void requestService(int activity) {
        mRadio.requestService(activity);
    }

    /**
     * Public methods
     */


    /**
     * Init
     */

    public void init() {

        // Radio //
        if (mRadio == null) {
            mRadio = new Radio();
            mRadio.addListener(this);
        }

        mRadio.init();

        // Set current station list
        setStationListBand(getBand());
    }

    public void stop() {
        mRadio.stop();
    }

    /**
     * Get freq-range
     *
     * @return
     */
    public FreqRange getFreqRange() {
        return mRadio.freqRange;
    }

    /**
     * Get station list band
     *
     * @return
     */
    public Band getBand() {
        return mRadio.freqRange.band;
    }

    /**
     * Current station list
     *
     * @return
     */
    public Stations getStationList() {
        return mRadio.stationsMap.get(getBand());
    }

    /**
     * Ger current freq
     *
     * @return
     */
    public int getFreq() {
        return mRadio.freq;
    }

    /**
     * Get current station
     *
     * @return
     */
    public Station getStation() {
        return mRadio.station;
    }

    /**
     * Refresh station list
     */
    public void refreshStationList() {
        initPreferenceValues();
        setStationListBand(getBand());
    }

    /**
     * Refresh station
     */
    public void refreshStation() {
        setStation(getStationList().findIdByUUID(getStation().uuid));
    }

    public void clearStationList() {
        mRadio.clearStationList(getBand(), getStationList());
    }

    /**
     * Switch station list by name
     *
     * @param band
     */
    public void setStationListBand(Band band) {

        mRadio.setFreqRangeById(band.id);
        //sendBroadcast(Constants.BROADCAST_INFO_STATION_LIST_NAME, mListName);
//        onStationListChanged(getStationList());
//
//        // Active station
//        int index = mBandIndexes.get(mListName);
//        int size = getStationList().size();
//
//        if (index > -1 && size > 0) {
//            index = (index >= size) ? size - 1 : index;
//            index = (index < 0) ? 0 : index;
//            mStation = getStationList().get(index);
//        } else {
//            mFreq = mPreferences.getFrequency();
//            int freqRangeId = Tools.idByListName(mListName);
//            mStation = new Station(null, mFreq, freqRangeId, 0);
//        }
//
//        mBandIndexes.put(mListName, index);
//        onStationSelected(index);
//
//        // Set station
//        if (mRadio.freqRange == null || mStation.freqRangeId != mRadio.freqRange.band.id) {
//            // Change freq-range and frequency
//            mRadio.setStation(mStation.freqRangeId, mStation.freq);
//        } else {
//            mRadio.setFreq(mStation.freq);
//            // No need to change freq range, just notify about it
//            sendBroadcast(Constants.BROADCAST_INFO_FREQUENCY_RANGE, mFreqRange);
//        }
//
//        // Save
//        mPreferences.putListName(mListName);
//        mPreferences.putIndexes(mBandIndexes);

    }

    /**
     * Set frequency
     *
     * @param freq
     */
    public void setFrequency(int freq) {
        FreqRange mFreqRange = getFreqRange();
        freq = freq > mFreqRange.maxFreq ? mFreqRange.maxFreq : freq;
        freq = freq < mFreqRange.minFreq ? mFreqRange.minFreq : freq;
        mRadio.setFreq(freq);
    }

    /**
     * Set station
     */
    public void setStation(int index) {
        Station station = getStationList().get(index);
        mRadio.setStationByIndex(station.freqRangeId, index);
        //onStationSelected(index);
    }

//    public void setStation(Station station) {
//        if (station.freqRangeId == mRadio.freqRange.band.id) {
//            int index = getStationList().findIdByUUID(station.uuid);
//            mRadio.setFreq(station.freq);
//        } else {
//            int index = getStationList().findIdByUUID(station.uuid);
//            mRadio.setStation(station.freqRangeId, station.freq);
//            mRadio.setStationByIndex(station.freqRangeId, index);
//        }
//        onStationSelected(index);
//    }

    /**
     * Update положение станции в списке
     *
     * @param index   Новый индекс станции
     * @param station Станция
     */
    public void updateStation(int index, Station station) {

        // Update station list (based on station FM1 or AM)
        //String origListName = Tools.listNameById(station.freqRangeId);
        //mStationList.get(origListName).setByUUID(station.uuid, station);
        //getStationList().set(index, station);
        mRadio.storeCurrentFreq(index);
        // Notify
        //onStationListChanged(getStationList());
        //setStation(index);

        // Save
        //mPreferences.putStationList(mListName, getStationList());
    }

    private void onStationListChanged(Stations stations) {
        sendBroadcast(Constants.BROADCAST_INFO_STATION_LIST, stations);
    }


    public void toggleREGFlag() {
        mRadio.toggleFlag_REG();
    }

    public void toggleTAFlag() {
        mRadio.toggleFlag_TA();
    }

    public void toggleAFFlag() {
        mRadio.toggleFlag_AF();
    }

    public void toggleLOCFlag() {
        mRadio.toggleFlag_LOC();
    }

    public void autoScan() {
        mRadio.autoScan();
    }

    /**
     * Switch to prev station
     */
    public void prevStation() {
        mRadio.prevStation();
    }

    /**
     * Switch to next station
     */
    public void nextStation() {
        mRadio.nextStation();
    }

    public void seekNextStation() {
        mRadio.seekNextStation();
    }

    public void seekPrevStation() {
        mRadio.seekPrevStation();
    }

    public void seekPrevFreq() {
        mRadio.seekPrevFreq();
    }

    public void seekNextFreq() {
        mRadio.seekNextFreq();
    }

}
