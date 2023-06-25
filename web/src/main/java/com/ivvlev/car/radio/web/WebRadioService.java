package com.ivvlev.car.radio.web;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.ivvlev.car.framework.ObjectHelper;

public final class WebRadioService extends Service {
    private final String LOG_TAG = getClass().getCanonicalName();
    private final boolean DEBUG = true;
    private SimpleExoPlayer simpleExoPlayer;
    private DefaultDataSourceFactory dataSourceFactory;
    private volatile ServiceBinder mServiceBinder;
    private State mState = State.Stopped;
    private String mUrl = null;
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
            if (DEBUG) Log.d(LOG_TAG, "onReceive: " + action);
            if (Constants.BROADCAST_ACTION_SERVICE_PLAY.equals(action)) {
                play(vString);
            } else if (Constants.BROADCAST_ACTION_SERVICE_STOP.equals(action)) {
                stop();
            }
        }
    };

    public WebRadioService() {
        mServiceBinder = new ServiceBinder();
    }

    public void onCreate() {
        super.onCreate();
        if (DEBUG) Log.d(LOG_TAG, "onCreate");
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.BROADCAST_ACTION_SERVICE_PLAY);
        intentFilter.addAction(Constants.BROADCAST_ACTION_SERVICE_STOP);
        this.registerReceiver(mBroadcastReceiver, intentFilter);
        dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "CarRadio"));
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(C.USAGE_MEDIA)
                .setContentType(C.CONTENT_TYPE_MOVIE)
                .build();
        simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(this);
        simpleExoPlayer.addListener(new ExoPlayerEventListener());
        simpleExoPlayer.setAudioAttributes(audioAttributes, /* handleAudioFocus= */ true);
    }

    public void onDestroy() {
        stop();
        simpleExoPlayer.release();
        simpleExoPlayer = null;
        this.unregisterReceiver(mBroadcastReceiver);
        super.onDestroy();
        if (DEBUG) Log.d(LOG_TAG, "onDestroy");
    }

    public IBinder onBind(Intent intent) {
        if (DEBUG) Log.d(LOG_TAG, "onBind");
        return mServiceBinder;
    }

    public void play(String url) {
        switch (mState) {
            case Playing:
                if (ObjectHelper.equals(mUrl, url, true)) {
                    break;
                }
                stop();
                //break;
            case Stopped:
                mUrl = url;

                MediaSource mediaSource = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(url));
                simpleExoPlayer.setPlayWhenReady(true); //run file/link when ready to play.
                simpleExoPlayer.prepare(mediaSource);
                break;
        }
    }

    public void stop() {
        try {
            if (simpleExoPlayer != null) simpleExoPlayer.stop();
        } finally {
            mState = State.Stopped;
            sendDisplayMessage("Stopped");
        }
    }

    private void sendDisplayMessage(String msg) {
        Intent intent = new Intent(Constants.BROADCAST_ACTION_SERVICE_ONMESSAGE);
        intent.putExtra("type", "string");
        intent.putExtra("value", msg);
        sendBroadcast(intent);
    }

    public class ServiceBinder extends Binder {
        WebRadioService getService() {
            return WebRadioService.this;
        }
    }

    private class ExoPlayerEventListener implements ExoPlayer.EventListener {


        @Override
        public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {
            //sendDisplayMessage("onTimelineChanged");
        }

        @Override
        public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
            //sendDisplayMessage("onTracksChanged");
        }

        @Override
        public void onLoadingChanged(boolean isLoading) {
            sendDisplayMessage("Loading");
        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            switch (playbackState) {
                case Player.STATE_IDLE:
                    sendDisplayMessage("Idle");
                    break;
                case Player.STATE_BUFFERING:
                    sendDisplayMessage("Buffering");
                    break;
                case Player.STATE_READY:
                    sendDisplayMessage("Playing");
                    break;
                case Player.STATE_ENDED:
                    sendDisplayMessage("Stopped");
                    break;
            }
        }

        @Override
        public void onRepeatModeChanged(int repeatMode) {
            //sendDisplayMessage("onRepeatModeChanged");
        }

        @Override
        public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
            // sendDisplayMessage("onShuffleModeEnabledChanged");
        }

        @Override
        public void onPlayerError(ExoPlaybackException error) {
            sendDisplayMessage("onPlayerError");
            stop();
        }

        @Override
        public void onPositionDiscontinuity(int reason) {
            //sendDisplayMessage("onPositionDiscontinuity");
        }

        @Override
        public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
            //sendDisplayMessage("onPlaybackParametersChanged");
        }

        @Override
        public void onSeekProcessed() {
            // sendDisplayMessage("onSeekProcessed");
        }
    }

    private enum State {
        Stopped,
        Playing
    }
}
