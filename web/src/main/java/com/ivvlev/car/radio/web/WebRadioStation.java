package com.ivvlev.car.radio.web;


public class WebRadioStation {
    public final String url;
    public final String caption;

    public WebRadioStation(String url, String caption) {
        this.url = url;
        this.caption = caption;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj)
                || (caption.equals(((WebRadioStation) obj).caption) && url.equals(((WebRadioStation) obj).url));
    }
}
