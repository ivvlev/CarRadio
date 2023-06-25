package com.ivvlev.car.radio.web;

import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ivvlev.car.framework.ActionLayout;
import com.ivvlev.car.framework.PageableListView;
import com.ivvlev.car.framework.PagerFragment;
import com.ivvlev.car.framework.Tools;
import com.ivvlev.car.framework.json.GsonHelper;

public class WebRadioFragment extends PagerFragment {
    private static final boolean DEBUG = true;
    private WebRadioViewModel mViewModel;
    //private WebRadioService mWebRadioService;
    private WebRadioServiceProxy mWebRadioServiceProxy;
    private TextView displayTextView;
    private TextView statusTextView;
    private PageableListView<WebRadioStation> mPageableListView;
    private WebRadioStation mSelectedWebRadioStation;
    private boolean mStartEQ = false;

    public static WebRadioFragment newInstance() {
        return new WebRadioFragment();
    }

//    public void onServiceConnected(ComponentName componentName, IBinder service) {
//        System.out.println("");
//        if (DEBUG) Log.d(LOG_TAG, "WEB RADIO SERVICE CONNECTED");
//        //mWebRadioService = ((WebRadioService.ServiceBinder) service).getService();
//    }
//
//    public void onServiceDisconnected(final ComponentName componentName) {
//        if (DEBUG) Log.d(LOG_TAG, "WEB RADIO SERVICE DISCONNECTED");
//        //mWebRadioService = null;
//    }

    @Override
    public void onAttach(Context context) {
        if (DEBUG) Log.d(LOG_TAG, "onAttach");
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        if (DEBUG) Log.d(LOG_TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mViewModel = ViewModelProviders.of(this).get(WebRadioViewModel.class);
        if (savedInstanceState == null) {
            //Запускаем сервис только при первом создании фрагмента.
            getContext().startService(new Intent(getContext(), WebRadioService.class));
        }
        mWebRadioServiceProxy = new WebRadioServiceProxy(getContext());
        //getContext().bindService(new Intent(getContext(), WebRadioService.class), this, Context.BIND_AUTO_CREATE);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        if (DEBUG) Log.d(LOG_TAG, "onCreateView");
        View view = inflater.inflate(R.layout.web_radio_fragment, container, false);
        displayTextView = view.findViewById(R.id.text_display_main);
        statusTextView = view.findViewById(R.id.text_display_status);
        mPageableListView = view.findViewById(R.id.web_radio_station_pager);
        super.onCreateView(inflater, container, savedInstanceState);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        if (DEBUG) Log.d(LOG_TAG, "onActivityCreated");
        super.onActivityCreated(savedInstanceState);
        mPageableListView.setEventListener(new StationListEventListener());
        mPageableListView.setListSource(mViewModel.getWebRadioStationList());
    }


    private class StationListEventListener implements PageableListView.EventListener<WebRadioStation> {

        @Override
        public void onClick(WebRadioStation item, int index) {
            if (mWebRadioServiceProxy != null) {
                selectWebRadioStation(item);
            }
        }

        @Override
        public boolean onLongClick(WebRadioStation item, int index) {
//            if (mWebRadioServiceProxy != null) {
//                mWebRadioServiceProxy.updateStation(index, item);
//                return true;
//            }
            return false;
        }

        @Override
        public String onFormatItem(WebRadioStation item, int index) {
            String caption = (item.caption == null || item.caption.isEmpty())
                    ? "[Не задано]"
                    : item.caption;
            return caption;
        }
    }

    private void selectWebRadioStation(WebRadioStation station) {
        mSelectedWebRadioStation = station;
        mWebRadioServiceProxy.play(station.url);
        mPageableListView.setSelectedItem(station);
        setDisplayText(station.caption);
    }


    @Override
    public void onDestroy() {
        if (DEBUG) Log.d(LOG_TAG, "onDestroy");
        mWebRadioServiceProxy.dispose();
        mWebRadioServiceProxy = null;
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    protected void onSaveState(Bundle bundle) {
        super.onSaveState(bundle);
        bundle.putCharSequence("Display.Text", displayTextView.getText());
        bundle.putCharSequence("Display.Status", statusTextView.getText());
        bundle.putInt("StationPageView.PageIndex", mPageableListView.getCurrentPage());
        if (mSelectedWebRadioStation != null)
            bundle.putString("StationPageView.SelectedStation", GsonHelper.toJson(mSelectedWebRadioStation, WebRadioStation.class));
    }

    @Override
    protected void onRestoreState(Bundle bundle) {
        super.onRestoreState(bundle);
        if (bundle.containsKey("Display.Text"))
            displayTextView.setText(bundle.getCharSequence("Display.Text"));
        if (bundle.containsKey("Display.Status"))
            statusTextView.setText(bundle.getCharSequence("Display.Status"));
        if (bundle.containsKey("StationPageView.PageIndex")) {
            int pageIndex = bundle.getInt("StationPageView.PageIndex");
            //if (mPageableListView.getChildCount() > pageIndex)
            mPageableListView.setCurrentPage(pageIndex, true);
        }
        if (bundle.containsKey("StationPageView.ItemIndex")) {
            int itemIndex = bundle.getInt("StationPageView.ItemIndex");
            //if (mPageableListView.getChildCount() > pageIndex)
            mPageableListView.setSelectedItemByIndex(itemIndex);
        }
        if (bundle.containsKey("StationPageView.SelectedStation")) {
            mSelectedWebRadioStation = GsonHelper.fromJson(bundle.getString("StationPageView.SelectedStation"), WebRadioStation.class);
        }
    }

    @Override
    public void onDetach() {
        if (DEBUG) Log.d(LOG_TAG, "onDetach");
        //getContext().unbindService(this);
        super.onDetach();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_web_radio, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private void setDisplayText(String msg) {
        this.displayTextView.setText(msg);
    }

    private void setStatusText(String msg) {
        this.statusTextView.setText(msg);
    }

    @Override
    public void onPageActivated(Context context) {
        super.onPageActivated(context);
        if (mSelectedWebRadioStation != null) {
            selectWebRadioStation(mSelectedWebRadioStation);
        } else {
            selectWebRadioStation(mViewModel.getWebRadioStationList().get(0));
        }
        ((ActionLayout) context).clearRight();
        ((ActionLayout) context).addActionToRight("EQ", this::onEqualizerBtnClicked);
    }


    public void onEqualizerBtnClicked(View view) {
        if (Tools.switchToEqualizer(getActivity())) {
            mStartEQ = true;
        }
    }

    @Override
    public void onPageDeactivated(Context context) {
        super.onPageDeactivated(context);
        if (mWebRadioServiceProxy != null) {
            mWebRadioServiceProxy.stop();
        }
    }

    private static class StationHolder {
        public final int index;
        public final WebRadioStation station;

        public StationHolder(int index, WebRadioStation station) {
            this.index = index;
            this.station = station;
        }
    }


    private class WebRadioServiceProxy {

        private Context mContext;
        private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
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

                if (Constants.BROADCAST_ACTION_SERVICE_ONMESSAGE.equals(action)) {
                    setStatusText(vString);
                }
            }
        };

        public WebRadioServiceProxy(Context mContext) {
            this.mContext = mContext;
            // IntentFilter //
            final IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(Constants.BROADCAST_ACTION_SERVICE_ONMESSAGE);
            getContext().registerReceiver(mBroadcastReceiver, intentFilter);
        }

        public void play(String url) {
            mContext.sendBroadcast(new Intent(Constants.BROADCAST_ACTION_SERVICE_PLAY)
                    .putExtra("type", "string")
                    .putExtra("value", url));
        }

        public void stop() {
            mContext.sendBroadcast(new Intent(Constants.BROADCAST_ACTION_SERVICE_STOP));
        }

        public void dispose() {
            mContext.unregisterReceiver(mBroadcastReceiver);
            mContext = null;
        }
    }


}
