package com.ivvlev.car.radio.mst768;

import java.util.ArrayList;
import java.util.Iterator;

public class Stations extends ArrayList<Station> {

    public Station findByFreq(int freq){
        for (Station station : this) {
            if (station.freq == freq){
                return station;
            }
        }
        return null;
    }

    public int findIdByFreq(int freq){
        int id = 0;
        for (Station station : this) {
            if (station.freq == freq){
                return id;
            }
            id ++;
        }
        return -1;
    }

    public void removeByUUID(String uuid){
        Iterator<Station> it = this.iterator();
        while (it.hasNext()) {
            Station station = it.next();
            if (station.uuid.equals(uuid)) {
                it.remove();
            }
        }
    }

    public void setByUUID(String uuid, Station aStation){
        int id = 0;
        for (Station station : this) {
            if (station.uuid.equals(uuid)){
                this.set(id, aStation);
            }
            id ++;
        }
    }

    public Station findByUUID(String uuid){
        Station result = null;
        for (Station station : this) {
            if (station.uuid.equals(uuid)){
                return station;
            }
        }
        return result;
    }

    public int findIdByUUID(String uuid){
        int id = 0;
        for (Station station : this) {
            if (station.uuid.equals(uuid)){
                return id;
            }
            id ++;
        }
        return -1;
    }

}