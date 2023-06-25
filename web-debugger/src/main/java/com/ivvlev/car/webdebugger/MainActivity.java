package com.ivvlev.car.webdebugger;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.tw.john.TWUtil;
import android.view.Surface;
import android.view.View;
import android.widget.EditText;

import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.video.VideoRendererEventListener;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements VideoRendererEventListener {

    private TWUtil mTWUtil = new TWUtil();
    private EditText mEditText;
    private volatile AudioManager audioManager;
    private AFListener afListenerMusic;
    private MediaPlayer mMediaPlayer;
    private String url = "https://maximum.hostingradio.ru/maximum96.aacp";

    private SimpleExoPlayer simpleExoPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mEditText = findViewById(R.id.editTextLog);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
    }

    public void btnInitOnClick(View view) {
        log("Init TW");
        if (mTWUtil.open(new short[]{(short) -25085, (short) -25057}) == 0) {
            mTWUtil.start();
        }
    }

    public void btnCloseOnClick(View view) {
        log("Close TW");
        mTWUtil.close();
    }

    public void btnAcquireRadioFocusTWOnClick(View view) {
        log("AcquireRadioFocusTW");
        mTWUtil.write(40465, 192, 1);
    }

    public void btnReleaseRadioTWFocusOnClick(View view) {
        log("ReleaseRadioTW");
        mTWUtil.write(40465, 192, 129);
    }

    public void btnAcquireDefaultRadioFocusOnClick(View view) {
        afListenerMusic = new AFListener(mMediaPlayer, "Music");
        int requestResult = audioManager.requestAudioFocus(afListenerMusic,
                AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        log("Music request focus, result: " + requestResult);
    }

    public void btnReleaseDefaultRadioFocusOnClick(View view) {
        int requestResult = audioManager.abandonAudioFocus(afListenerMusic);
        log("Music: abandon focus result: " + requestResult);
    }

    public void btnMediaPlayerPlayOnClick(View view) {
        try {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setDataSource(url);
            mMediaPlayer.setOnPreparedListener(mp -> {
                log(mp.toString() + ".onPrepared");
                if (mMediaPlayer == mp) {
                    mp.start();
                    log("Playing");
                    //startUpdatingSeekbarWithPlaybackProgress();
                }
            });
            mMediaPlayer.setOnCompletionListener(mp -> {
                log("Music: abandon focus");
                audioManager.abandonAudioFocus(afListenerMusic);
                log("onCompletion");
            });
            mMediaPlayer.setLooping(true);
            log("Connecting to: " + url);
            mMediaPlayer.prepareAsync();
        } catch (IOException e) {
            mMediaPlayer.stop();
            log("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void btnMediaPlayerStopOnClick(View view) {
        log("mMediaPlayer.stop():");
        mMediaPlayer.stop();
    }


    public void btnExoPlayerPlayOnClick(View view) {

        RenderersFactory renderersFactory = new DefaultRenderersFactory(this, null, DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF);
        TrackSelector trackSelector = new DefaultTrackSelector();
        ExtractorsFactory extractorFactory = new DefaultExtractorsFactory();
        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(this,Util.getUserAgent(this, "ExoPlayerIntro"));
        MediaSource[] mediaSources = new MediaSource[]{new ExtractorMediaSource(Uri.parse(url), dataSourceFactory, extractorFactory, null, Throwable::printStackTrace)};
        MediaSource mediaSource = mediaSources.length == 1 ? mediaSources[0]: new ConcatenatingMediaSource(mediaSources);
        simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(renderersFactory, trackSelector);
        simpleExoPlayer.prepare(mediaSource);
        simpleExoPlayer.setPlayWhenReady(true);


        simpleExoPlayer.addListener(new ExoPlayer.EventListener() {


            @Override
            public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
                log("Listener-onTracksChanged... ");
            }

            @Override
            public void onLoadingChanged(boolean isLoading) {

            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                log("Listener-onPlayerStateChanged..." + playbackState);
            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {

            }

            @Override
            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
                log("Listener-onPlayerError...");
                simpleExoPlayer.stop();
                simpleExoPlayer = null;
            }

            @Override
            public void onPositionDiscontinuity(int reason) {

            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

            }

            @Override
            public void onSeekProcessed() {

            }
        });
        simpleExoPlayer.setPlayWhenReady(true); //run file/link when ready to play.
        simpleExoPlayer.setVideoDebugListener(this);
    }

    public void btnExoPlayerStopOnClick(View view) {
        simpleExoPlayer.stop();
    }


    @Override
    public void onVideoEnabled(DecoderCounters counters) {

    }

    @Override
    public void onVideoDecoderInitialized(String decoderName, long initializedTimestampMs, long initializationDurationMs) {

    }

    @Override
    public void onVideoInputFormatChanged(Format format) {

    }

    @Override
    public void onDroppedFrames(int count, long elapsedMs) {

    }

    @Override
    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
        log("onVideoSizeChanged [" + " width: " + width + " height: " + height + "]");
    }

    @Override
    public void onRenderedFirstFrame(Surface surface) {

    }

    @Override
    public void onVideoDisabled(DecoderCounters counters) {

    }


    public void log(String message) {
        mEditText.append(message);
        mEditText.append("\r\n");
    }


    class AFListener implements AudioManager.OnAudioFocusChangeListener {

        String label = "";
        int originalVolume = 100;
        MediaPlayer mp;

        public AFListener(MediaPlayer mp, String label) {
            this.label = label;
            this.mp = mp;
            originalVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        }

        @Override
        public void onAudioFocusChange(int focusChange) {
            String event = "";
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_LOSS:
                    event = "AUDIOFOCUS_LOSS";
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    event = "AUDIOFOCUS_LOSS_TRANSIENT";
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    event = "AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK";
                    originalVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 25, 0);
                    break;
                case AudioManager.AUDIOFOCUS_GAIN:
                    event = "AUDIOFOCUS_GAIN";
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, originalVolume, 0);
                    break;
            }
            log(label + " onAudioFocusChange: " + event);
        }
    }

}
