package com.dgcheshang.cheji.Bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/9/19 0019.
 */

public class LineBean implements Serializable {
    private String name;
    private int type;//类型
    private String lat;//维度
    private String lon;//经度
    private String direction;//方向

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    @Override
    public String toString() {
        return "LineBean{" +
                "name='" + name + '\'' +
                ", type=" + type +
                ", lat='" + lat + '\'' +
                ", lon='" + lon + '\'' +
                ", direction='" + direction + '\'' +
                '}';
    }
}
