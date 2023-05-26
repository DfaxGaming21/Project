/*
* шаблон для объекта района, тут есть его координаты, имя и приод, в течение которого устройство находилось в этом районе
* */
package ru.dfax214.geolocation;

import org.json.JSONObject;

public class Region extends JSONObject {
    String name = "";
    double latitude1, latitude2, longitude1, longitude2;
    long period;

    public Region(String name, double latitude1, double latitude2, double longitude1, double longitude2) {
        this.name = name;
        this.latitude1 = latitude1;
        this.latitude2 = latitude2;
        this.longitude1 = longitude1;
        this.longitude2 = longitude2;
    }

    public Region() {
        this.period = 0;
    };

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setLatitude1(double latitude1) {
        this.latitude1 = latitude1;
    }

    public double getLatitude1() {
        return latitude1;
    }

    public void setLatitude2(double latitude2) {
        this.latitude2 = latitude2;
    }

    public double getLatitude2() {
        return latitude2;
    }

    public void setLongitude1(double longitude1) {
        this.longitude1 = longitude1;
    }

    public double getLongitude1() {
        return longitude1;
    }

    public void setLongitude2(double longitude2) {
        this.longitude2 = longitude2;
    }

    public double getLongitude2() {
        return longitude2;
    }

    public void setPeriod(long period) {
        this.period = period;
    }

    public long getPeriod() {
        return period;
    }
}
