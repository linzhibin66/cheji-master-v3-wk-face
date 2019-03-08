package com.dgcheshang.cheji.netty.po;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/9/19.
 */

public class Line implements Serializable {

    private static final long serialVersionUID = 1L;
    private int id;
    private String mc;//名称
    private String xlzb;//线路坐标

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMc() {
        return mc;
    }

    public void setMc(String mc) {
        this.mc = mc;
    }

    public String getXlzb() {
        return xlzb;
    }

    public void setXlzb(String xlzb) {
        this.xlzb = xlzb;
    }

    @Override
    public String toString() {
        return "Line{" +
                "id=" + id +
                ", mc='" + mc + '\'' +
                ", xlzb='" + xlzb + '\'' +
                '}';
    }
}
