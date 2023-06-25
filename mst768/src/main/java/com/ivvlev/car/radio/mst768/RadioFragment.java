package com.ivvlev.car.radio.mst768;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.ivvlev.car.framework.ActionLayout;
import com.ivvlev.car.framework.PageableListView;
import com.ivvlev.car.framework.PagerFragment;
import com.ivvlev.car.framework.ToggleColorButton;
import com.ivvlev.car.framework.Tools;
import com.ivvlev.car.framework.json.GsonHelper;

public class RadioFragment extends PagerFragment {
    private static final boolean DEBUG = true;
    //private RadioViewModel mViewModel;

    //private RadioSharedPreferences sharedPreferences = null;
    private BroadcastReceiver mBroadcastReceiver = null;
    private RadioService mRadioService = null;
    private PageableListView<Station> mPageableListView;

    private boolean mStartEQ;
    private int mService;

    public static RadioFragment newInstance() {
        return new RadioFragment();
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName componentName, IBinder service) {
            System.out.println("");
            if (DEBUG) Log.d(LOG_TAG, "RADIO SERVICE CONNECTED");
            mRadioService = ((RadioService.ServiceBinder) service).getService();
            mRadioService.init();
            requestService(1);
        }

        public void onServiceDisconnected(final ComponentName componentName) {
            if (DEBUG) Log.d(LOG_TAG, "RADIO SERVICE DISCONNECTED");
            mRadioService = null;
        }

    };

    private void bindRadioService() {
        boolean radioServiceBind = getContext().bindService(new Intent(getContext(), RadioService.class), mServiceConnection, Context.BIND_AUTO_CREATE);
        if (DEBUG) Log.d(LOG_TAG, "bindRadioService(): " + radioServiceBind);
    }

    public void requestService(int activity) {
        this.mService = activity;
        if (this.mRadioService != null) {
            this.mRadioService.requestService(activity);
        }
    }

    private void unbindRadioService() {
        if (mRadioService != null) {
            mRadioService.stop();
            getContext().unbindService(mServiceConnection);
            mRadioService = null;
        }
        if (DEBUG) Log.d(LOG_TAG, "unbindRadioService()");
    }


    public RadioFragment() {
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(final Context context, final Intent intent) {
                final String action = intent.getAction();

                String type = intent.getStringExtra("type");
                String vString = null;
                String vObject = null;
                int vInteger = -1;
                boolean vBoolean = false;

                if (type != null) {
                    switch (type) {
                        case "integer":
                            vInteger = intent.getIntExtra("value", -1);
                            break;
                        case "string":
                            vString = intent.getStringExtra("value");
                            break;
                        case "boolean":
                            vBoolean = intent.getBooleanExtra("value", false);
                            break;
                        case "object":
                            vObject = intent.getStringExtra("value");
                            break;
                    }
                }


                if (Constants.BROADCAST_ACTION_REFRESH_PREFERENCES.equals(action)) {
                    initUIPrefs();
                } else if (Constants.BROADCAST_INFO_STARTED.equals(action)) {
                    // Nothing
                } else if (Constants.BROADCAST_INFO_FREQUENCY.equals(action)) {
                    UIRenderFrequency(vInteger);
                } else if (Constants.BROADCAST_INFO_STATION.equals(action)) {
                    UIRenderStation(GsonHelper.fromJson(vObject, Station.class));
                } else if (Constants.BROADCAST_INFO_STATION_LIST_SELECTION.equals(action)) {
                    UIRenderStationListSelection(vInteger);
                } else if (Constants.BROADCAST_INFO_STATION_LIST.equals(action)) {
                    UIRenderStationList(GsonHelper.fromJson(vObject, Stations.class));
                } else if (Constants.BROADCAST_INFO_FREQUENCY_RANGE.equals(action)) {
                    UIRenderFreqBar(GsonHelper.fromJson(vObject, FreqRange.class));
                } else if (Constants.BROADCAST_INFO_STATION_LIST_NAME.equals(action)) {
                    UIRenderListNameValue(vString);
                } else if (Constants.BROADCAST_INFO_FLAG_TA.equals(action)) {
                    UIRenderFlagTAValue(vBoolean);
                } else if (Constants.BROADCAST_INFO_FLAG_REG.equals(action)) {
                    UIRenderFlagREGValue(vBoolean);
                } else if (Constants.BROADCAST_INFO_FLAG_AF.equals(action)) {
                    UIRenderFlagAFValue(vBoolean);
                } else if (Constants.BROADCAST_INFO_FLAG_LOC.equals(action)) {
                    UIRenderFlagLOCValue(vBoolean);
                } else if (Constants.BROADCAST_INFO_FLAG_RDS_ST.equals(action)) {
                    UIRenderFlagRDSSTValue(vBoolean);
                } else if (Constants.BROADCAST_INFO_FLAG_RDS_TP.equals(action)) {
                    UIRenderFlagRDSTPValue(vBoolean);
                } else if (Constants.BROADCAST_INFO_FLAG_RDS_TA.equals(action)) {
                    UIRenderFlagRDSTAValue(vBoolean);
                } else if (Constants.BROADCAST_INFO_RDS_PTY_ID.equals(action)) {
                    // Nothing
                } else if (Constants.BROADCAST_INFO_RDS_PTY_NAME.equals(action)) {
                    UIRenderFlagRDSPTYNameValue(vString);
                } else if (Constants.BROADCAST_INFO_RDS_PS.equals(action)) {
                    UIRenderFlagRDSPSValue(vString);
                } else if (Constants.BROADCAST_INFO_RDS_TEXT.equals(action)) {
                    UIRenderFlagRDSTextValue(vString);
                } else if (Constants.BROADCAST_INFO_FLAG_SCANNING.equals(action)) {
                    UIStationFlag(vBoolean);
                } else if (Constants.BROADCAST_INFO_SCANNING_FOUND_STATION.equals(action)) {
                    UIStationFound(GsonHelper.fromJson(vObject, Station.class));
                } else if (Constants.BROADCAST_INFO_REGION_ID.equals(action)) {
                    // Nothing
                } else if (com.ivvlev.car.framework.Constants.BROADCAST_ACTION_WHEEL_NEXT.equals(action)) {
                    mRadioService.nextStation();
                } else if (com.ivvlev.car.framework.Constants.BROADCAST_ACTION_WHEEL_PREV.equals(action)) {
                    mRadioService.prevStation();
                } else if (com.ivvlev.car.framework.Constants.BROADCAST_ACTION_EQUALIZER_OPEN.equals(action)) {
                    mStartEQ = vBoolean;
                }
            }
        };
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        //Settings.System.getInt(getBaseContext(), "test", true);

        // Start service
        if (savedInstanceState == null) {
            //Запусукаем сервис один раз
            getContext().startService(new Intent(getContext(), RadioService.class));
        }
        // Init prefs
        //sharedPreferences = new RadioSharedPreferences(getContext());

        // Wallpaper background
//        String background = sharedPreferences.getBackground();
//        if (background.equals("$$wallpaper")) {
//            setTheme(R.style.wallpaper_bg);
//        }
//
//        // Create view
//        setContentView(R.layout.activity_radio);
//
//        // Init views
//        initViews();
//        initUIPrefs();
        // Attach to service
        //bindRadioService();
        // Intent filters
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.BROADCAST_INFO_STARTED);
        intentFilter.addAction(Constants.BROADCAST_INFO_FREQUENCY);
        intentFilter.addAction(Constants.BROADCAST_INFO_STATION);
        intentFilter.addAction(Constants.BROADCAST_INFO_STATION_LIST_NAME);
        intentFilter.addAction(Constants.BROADCAST_INFO_STATION_LIST);
        intentFilter.addAction(Constants.BROADCAST_INFO_STATION_LIST_SELECTION);
        intentFilter.addAction(Constants.BROADCAST_INFO_FREQUENCY_RANGE);
        intentFilter.addAction(Constants.BROADCAST_INFO_FLAG_TA);
        intentFilter.addAction(Constants.BROADCAST_INFO_FLAG_REG);
        intentFilter.addAction(Constants.BROADCAST_INFO_FLAG_AF);
        intentFilter.addAction(Constants.BROADCAST_INFO_FLAG_LOC);
        intentFilter.addAction(Constants.BROADCAST_INFO_FLAG_RDS_ST);
        intentFilter.addAction(Constants.BROADCAST_INFO_FLAG_RDS_TP);
        intentFilter.addAction(Constants.BROADCAST_INFO_FLAG_RDS_TA);
        intentFilter.addAction(Constants.BROADCAST_INFO_RDS_PTY_ID);
        intentFilter.addAction(Constants.BROADCAST_INFO_RDS_PTY_NAME);
        intentFilter.addAction(Constants.BROADCAST_INFO_RDS_PS);
        intentFilter.addAction(Constants.BROADCAST_INFO_RDS_TEXT);
        intentFilter.addAction(Constants.BROADCAST_INFO_FLAG_SCANNING);
        intentFilter.addAction(Constants.BROADCAST_INFO_SCANNING_FOUND_STATION);
        intentFilter.addAction(Constants.BROADCAST_INFO_REGION_ID);
        intentFilter.addAction(com.ivvlev.car.framework.Constants.BROADCAST_ACTION_WHEEL_NEXT);
        intentFilter.addAction(com.ivvlev.car.framework.Constants.BROADCAST_ACTION_WHEEL_PREV);
        intentFilter.addAction(com.ivvlev.car.framework.Constants.BROADCAST_ACTION_EQUALIZER_OPEN);
        getContext().registerReceiver(mBroadcastReceiver, intentFilter);

        //mViewModel = ViewModelProviders.of(this).get(RadioViewModel.class);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.radio_fragment, container, false);

        // Buttons
        Button nextButton = view.findViewById(R.id.nextButton);
        nextButton.setOnClickListener(this::onNextStationBtnClick);
        nextButton.setOnLongClickListener(this::onNextStationBtnLongClick);

        Button prevButton = view.findViewById(R.id.prevButton);
        prevButton.setOnClickListener(this::onPrevStationBtnClick);
        prevButton.setOnLongClickListener(this::onPrevStationBtnLongClick);

        Button fm1Button = view.findViewById(R.id.button_band_FM1);
        fm1Button.setOnClickListener(this::onBandRadioBtnClicked);

        Button fm2Button = view.findViewById(R.id.button_band_FM2);
        fm2Button.setOnClickListener(this::onBandRadioBtnClicked);

        Button amButton = view.findViewById(R.id.button_band_AM);
        amButton.setOnClickListener(this::onBandRadioBtnClicked);

        ToggleColorButton regCheckBox = view.findViewById(R.id.checkBox_REG);
        regCheckBox.setOnClickListener(this::onREGBtnClick);

        ToggleColorButton taCheckBox = view.findViewById(R.id.checkBox_TA);
        taCheckBox.setOnClickListener(this::onTABtnClick);

        ToggleColorButton afCheckBox = view.findViewById(R.id.checkBox_AF);
        afCheckBox.setOnClickListener(this::onAFBtnClick);

//        CheckBox ptyCheckBox = view.findViewById(R.id.checkBox_PTY);
//        ptyCheckBox.setOnClickListener(this::onPTYBtnClick);

        ToggleColorButton locCheckBox = view.findViewById(R.id.checkBox_LOC);
        locCheckBox.setOnClickListener(this::onLOC_DXBtnClick);

        mPageableListView = view.findViewById(R.id.radio_station_pager);
        mPageableListView.setEventListener(new StationListEventListener());
        //mPageableListView.setListSource(mRadioService.getStationList());

        return view;
    }

    private class StationListEventListener implements PageableListView.EventListener<Station> {

        @Override
        public void onClick(Station item, int index) {
            if (mRadioService != null) {
                mRadioService.setStation(index);
            }
        }

        @Override
        public boolean onLongClick(Station item, int index) {
            if (mRadioService != null) {
                mRadioService.updateStation(index, item);
                return true;
            }
            return false;
        }

        @Override
        public String onFormatItem(Station item, int index) {
//            String caption = (item.name == null || item.name.isEmpty())
//                    ? mRadioService.getFreqRange().formatFrequencyValue(item.freq, true)
//                    : item.name;
            return mRadioService.getFreqRange().formatFrequencyValue(item.freq, true);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_radio, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //return super.onOptionsItemSelected(item);
        int i = item.getItemId();
//        if (i == R.id.action_add_station) {
//            //displayStationAddDialog();
//            return true;
//        } else if (i == R.id.action_auto_scan) {
//            mRadioService.autoScan();
//            return true;
//        } else if (i == R.id.action_clear_list) {
//            mRadioService.clearStationList();
//            return true;
//        } else if (i == R.id.action_save_station_list) {
//            //displaySaveStationListDialog();
//            return true;
//        } else if (i == R.id.action_restore_station_list) {
//            //displayRestoreStationListDialog();
//            return true;
//        } else if (i == R.id.action_settings) {
//            //Tools.switchToSettings(this);
//            return true;
//        }
        return false;

    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        if (DEBUG) Log.d(LOG_TAG, "onActivityCreated");
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onStart() {
        if (DEBUG) Log.d(LOG_TAG, "onStart");
        super.onStart();
    }

    @Override
    public void onResume() {
        if (DEBUG) Log.d(LOG_TAG, "onResume");
        this.mStartEQ = false;
        requestService(1);
        super.onResume();
    }

    @Override
    public void onPause() {
        if (DEBUG) Log.d(LOG_TAG, "onPause");
        if (!this.mStartEQ) {
            requestService(129);
        }
        super.onPause();
    }

    @Override
    public void onStop() {
        if (DEBUG) Log.d(LOG_TAG, "onStop");
        super.onStop();
    }

    @Override
    public void onDetach() {
        if (DEBUG) Log.d(LOG_TAG, "onDetach");
        getContext().unregisterReceiver(mBroadcastReceiver);
        unbindRadioService();
        super.onDetach();
    }

    private void queryAudioFocus() {
        getContext().sendBroadcast(new Intent(Constants.BROADCAST_ACTION_RADIO_QUERY_AUDIO_FOCUS));
    }

    private void releaseAudioFocus() {
        getContext().sendBroadcast(new Intent(Constants.BROADCAST_ACTION_RADIO_RELEASE_AUDIO_FOCUS));
    }


    @Override
    public void onDestroy() {
        if (DEBUG) Log.d(LOG_TAG, "onDestroy");
        //getContext().unbindService(mServiceConnection);
        //Сервис останавливается при уничтожении MainActivity
        super.onDestroy();
    }

    private void UIRenderFrequency(int freq) {

        String text = mRadioService.getFreqRange().formatFrequencyValue(freq, false);
        ((TextView) getView().findViewById(R.id.display_freq_text)).setText(text);
        // freqBarView.setFreq(freq);
    }

    private void UIRenderFreqBar(FreqRange freqRange) {
        //freqBarView.setFreqRange(freqRange);
    }

    private void UIRenderStationList(Stations stations) {
        mPageableListView.setListSource(stations);
    }

    private void UIRenderStationListSelection(int index) {
        mPageableListView.setSelectedItemByIndex(index);
        // stationListView.setSelection(index);
    }

    private void UIRenderStation(Station station) {
        //showStationInfoToast(station);
        // stationInfoView.showStation(station);
    }

    private void UIRenderListNameValue(String listName) {
//        switch(listName){
//            case "FM1":
//                modeRadioGroup.check(R.id.fmMode);
//                break;
//            case "AM":
//                modeRadioGroup.check(R.id.amMode);
//                break;
//            case "FAV":
//                modeRadioGroup.check(R.id.favMode);
//                break;
//        }
    }

    private void UIRenderFlagTAValue(boolean flag) {
        //taCheckBox.setChecked(flag);
        ((Checkable) getView().findViewById(R.id.checkBox_TA)).setChecked(flag);
    }

    private void UIRenderFlagREGValue(boolean flag) {
        ///regCheckBox.setChecked(flag);
        ((Checkable) getView().findViewById(R.id.checkBox_REG)).setChecked(flag);
    }

    private void UIRenderFlagAFValue(boolean flag) {
        //afCheckBox.setChecked(flag);
        ((Checkable) getView().findViewById(R.id.checkBox_AF)).setChecked(flag);
    }

    private void UIRenderFlagLOCValue(boolean flag) {
        //locCheckBox.setText(flag ? R.string.loc_btn : R.string.dx_btn);
        // locCheckBox.setChecked(flag);
        ((Checkable) getView().findViewById(R.id.checkBox_LOC)).setChecked(flag);
    }

    private void UIRenderFlagRDSSTValue(boolean flag) {
        //stationInfoView.setRDSSTFlag(flag);
        ((Checkable) getView().findViewById(R.id.flag_ST)).setChecked(flag);
    }

    private void UIRenderFlagRDSTPValue(boolean flag) {
        // stationInfoView.setRDSTPFlag(flag);
        ((Checkable) getView().findViewById(R.id.flag_TP)).setChecked(flag);
    }

    private void UIRenderFlagRDSTAValue(boolean flag) {
        //stationInfoView.setRDSTAFlag(flag);
        ((Checkable) getView().findViewById(R.id.flag_TA)).setChecked(flag);
    }

    private void UIRenderFlagRDSPTYNameValue(String text) {
//        if (isRdsEnabled)
//            stationInfoView.setRDSPTYText(text);
        ((TextView) getView().findViewById(R.id.display_RDSPTY_text)).setText(text);
    }

    private void UIRenderFlagRDSPSValue(String text) {
//        if (isRdsEnabled)
//            stationInfoView.setRDSPSText(text);
        ((TextView) getView().findViewById(R.id.display_RDSPS_text)).setText(text);
    }

    private void UIRenderFlagRDSTextValue(String text) {
        //((TextView) getView().findViewById(R.id.display_RDS_Text)).setText("RDS: " + text);
        //rdsMessageTextView.setText("one\ntwo\nthree");
        //rdsMessageTextView.setMovementMethod(new ScrollingMovementMethod());
        //rdsMessageTextView.setText(text);
    }

    private void UIStationFound(Station station) {
        //stationListView.add(station);
        //mStationArrayAdapter.add(station);
    }

    private void UIStationFlag(boolean flag) {
        //if (flag)
        //mStationArrayAdapter.clear();
        //stationListView.clear();
    }


    private void initUIPrefs() {

//        // Set background
//        String background = sharedPreferences.getBackground();
//        if (background.equals("$$solid")) { // Solid color
//            int backgroundColor = sharedPreferences.getBackgroundColor();
//            mainView.setBackgroundColor(backgroundColor);
//        } else if (background.equals("$$custom")) { // Custom image
//            String uri = sharedPreferences.getBackgroundImageURI();
//            mainView.setBackgroundDrawable(Drawable.createFromPath(uri));
//        } else { // Image resource from preset
//            mainView.setBackgroundResource(getResources().getIdentifier(background, "drawable", this.getPackageName()));
//        }
//
//        // Grid view
//        String gridSize = sharedPreferences.getGridSize();
//        String[] parts = gridSize.split("x");
//        int rowNum = Integer.valueOf(parts[0]);
//        int colNum = Integer.valueOf(parts[1]);
//        stationListView.setGridSize(rowNum, colNum);
//        stationListView.setFontSize(sharedPreferences.getStationInfoFontSize());
//
//        // Toasts
//        isToastNotificationsEnabled = sharedPreferences.isToastNotificationsEnabled();
//        toastNotificationPosition = sharedPreferences.getToastNotificationPosition();
//        toastNotificationDuration = sharedPreferences.getToastNotificationDuration();
//        toastNotificationTransparency = sharedPreferences.getStationInfoToastBgTransparency();
//
//        // AM band
//        isAmBandEnabled = sharedPreferences.isAmBandEnabled();
//        if (isAmBandEnabled)
//            findViewById(R.id.amMode).setVisibility(View.VISIBLE);
//        else
//            findViewById(R.id.amMode).setVisibility(View.GONE);
//
//        // Freq bar
//        isFreqBarEnabled = sharedPreferences.isFreqBarEnabled();
//        if (isFreqBarEnabled)
//            findViewById(R.id.freqBarLayout).setVisibility(View.VISIBLE);
//        else
//            findViewById(R.id.freqBarLayout).setVisibility(View.GONE);
//
//        // RDS
//        isRdsEnabled = sharedPreferences.isRDSEnabled();
//
//        // Toggle full view
//        isToggleFullViewEnabled = sharedPreferences.isToggleFullViewEnabled();
//        toggleFullView();
    }

    public void onPrevStationBtnClick(View view) {
        //mRadioService.prevStation();
        mRadioService.seekPrevFreq();
    }

    public void onNextStationBtnClick(View view) {
        //mRadioService.nextStation();
        mRadioService.seekNextFreq();
    }

    public boolean onPrevStationBtnLongClick(View view) {
        mRadioService.seekPrevStation();
        return true;
    }

    public boolean onNextStationBtnLongClick(View view) {
        mRadioService.seekNextStation();
        return true;
    }

    public void onREGBtnClick(View view) {
        mRadioService.toggleREGFlag();
    }

    public void onTABtnClick(View view) {
        mRadioService.toggleTAFlag();
    }

    public void onAFBtnClick(View view) {
        mRadioService.toggleAFFlag();
    }

    public void onPTYBtnClick(View view) {
        //mRadioService.togglePTYFlag();
    }

    public void onLOC_DXBtnClick(View view) {
        mRadioService.toggleLOCFlag();
    }

    public void onREGBtnToggle(CompoundButton buttonView, boolean isChecked) {
        mRadioService.toggleREGFlag();
    }

    public void onTABtnToggle(CompoundButton buttonView, boolean isChecked) {
        mRadioService.toggleTAFlag();
    }

    public void onAFBtnToggle(CompoundButton buttonView, boolean isChecked) {
        mRadioService.toggleAFFlag();
    }

    public void onPTYBtnToggle(CompoundButton buttonView, boolean isChecked) {
        //mRadioService.togglePTYFlag();
    }

    public void onLOCBtnToggle(CompoundButton buttonView, boolean isChecked) {
        mRadioService.toggleLOCFlag();
    }

    public void onFMRadioBtnClicked(View view) {
        mRadioService.setStationListBand(Band.FM1);
    }

    public void onAMRadioBtnClicked(View view) {
        mRadioService.setStationListBand(Band.AM);
    }

    public void onBandRadioBtnClicked(View view) {
        int id = view.getId();
        if (id == R.id.button_band_FM1) {
            mRadioService.setStationListBand(Band.FM1);
        } else if (id == R.id.button_band_FM2) {
            mRadioService.setStationListBand(Band.FM2);
        } else if (id == R.id.button_band_AM) {
            mRadioService.setStationListBand(Band.AM);
        }
        ViewGroup viewGroup = (ViewGroup) view.getParent();
        for (int index = 0; index < viewGroup.getChildCount(); index++) {
            ToggleColorButton btn = ((ToggleColorButton) viewGroup.getChildAt(index));
            btn.setChecked(btn.getId() == view.getId());
        }
    }

    public void onHomeBtnClicked(View view) {
        Tools.switchToHome(getActivity());
    }

    @Override
    public void onPageActivated(Context context) {
        super.onPageActivated(context);
        if (context instanceof ActionLayout) {
            ((ActionLayout) context).clearLeft();
//            ((ActionLayout) context).addActionToLeft("FM1", this::onFMRadioBtnClicked);
//            ((ActionLayout) context).addActionToLeft("AM", this::onAMRadioBtnClicked);
            ((ActionLayout) context).clearRight();
            ((ActionLayout) context).addActionToRight("EQ", this::onEqualizerBtnClicked);
//            ((ActionLayout) context).addActionToRight("REG", this::onREGBtnClick);
//            ((ActionLayout) context).addActionToRight("TA", this::onTABtnClick);
//            ((ActionLayout) context).addActionToRight("AF", this::onAFBtnClick);
//            ((ActionLayout) context).addActionToRight("PTY", this::onPTYBtnClick);
//            ((ActionLayout) context).addActionToRight("DX", this::onLOC_DXBtnClick);
        }
        // Включить радио
        bindRadioService();
    }

    public void onEqualizerBtnClicked(View view) {
        if (Tools.switchToEqualizer(getActivity())) {
            mStartEQ = true;
        }
    }


    @Override
    public void onPageDeactivated(Context context) {
        // Выключить радио
        if (context instanceof ActionLayout) {
            ((ActionLayout) context).clearLeft();
            ((ActionLayout) context).clearRight();
        }
        unbindRadioService();
        super.onPageDeactivated(context);
    }

    private class StationHolder {
        public final int index;
        public final Station station;

        public StationHolder(int index, Station station) {
            this.index = index;
            this.station = station;
        }
    }

}
