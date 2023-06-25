package com.ivvlev.car.radio.web;

import android.arch.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class WebRadioViewModel extends ViewModel {
    private final String LOG_TAG = getClass().getCanonicalName();

    private List<WebRadioStation> webRadioStationList = new ArrayList<>();

    public WebRadioViewModel() {
        //http://top-radio.ru/web
        //webRadioStationList.add(new WebRadioStation("https://maximum.gcdn.co/max_m.aac", "Maximum"));
        //webRadioStationList.add(new WebRadioStation("https://bfm.hostingradio.ru:9075/fm", "BusinessFM"));
        //webRadioStationList.add(new WebRadioStation("http://icecast.radiomaximum.cdnvideo.ru:8000/max_m.mp3", "Maximum"));
        webRadioStationList.add(new WebRadioStation("https://maximum.hostingradio.ru/maximum96.aacp", "Maximum"));
        webRadioStationList.add(new WebRadioStation("https://metallica.hostingradio.ru/metallica96.aacp?0.013595991198548152", "Maximum Met"));
        webRadioStationList.add(new WebRadioStation("https://depechemode.hostingradio.ru/depechemode96.aacp?0.33078725013418575", "Maximum DM"));
        webRadioStationList.add(new WebRadioStation("https://rockhits.hostingradio.ru/rockhits96.aacp?0.296185149485227", "Rock Hits"));
        webRadioStationList.add(new WebRadioStation("https://maximum90.hostingradio.ru/maximum9096.aacp?0.624868234218477", "Maximum 90e"));
        webRadioStationList.add(new WebRadioStation("http://streams.ffh.de/ffhchannels/mp3/hqrock.mp3?amsparams=playerid:RTFFHVtuner", "Rock FFH"));
        webRadioStationList.add(new WebRadioStation("http://bfm.hostingradio.ru:8004/fm", "Business FM"));
        webRadioStationList.add(new WebRadioStation("http://air.radiorecord.ru:805/rock_320", "Radio Record Rock"));
        webRadioStationList.add(new WebRadioStation("http://nashe2.hostingradio.ru/rock-128.mp3", "Rock FM 95.2"));
        webRadioStationList.add(new WebRadioStation("http://online-radioroks2.tavrmedia.ua/RadioROKS_HardnHeavy", "ROKS UA Hard'n'Heavy"));
        webRadioStationList.add(new WebRadioStation("http://us4.internet-radio.com:8258", "Classic Rock Florida HD"));
        webRadioStationList.add(new WebRadioStation("http://ep128.hostingradio.ru:8030/ep128", "Europa Plus"));
        webRadioStationList.add(new WebRadioStation("http://air2.radiorecord.ru:805/rr_320", "Radio Record"));
        webRadioStationList.add(new WebRadioStation("http://icecast.vgtrk.cdnvideo.ru/mayakfm_mp3_128kbps", "МАЯК"));
        webRadioStationList.add(new WebRadioStation("http://184.154.58.146:29378/ch12_64s.mp3", "Эхо Москвы"));
        webRadioStationList.add(new WebRadioStation("http://icecast.vgtrk.cdnvideo.ru/vestifm_aac_64kbps", "ВЕСТИ FM"));

    }

    public List<WebRadioStation> getWebRadioStationList() {
        return webRadioStationList;
    }
}
