package com.dgcheshang.cheji.Bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/9/3 0003.
 * 灯光bean
 */

public class LightingBean implements Serializable {
    private String name;
    private String doing;
    private String icon;
    private String voice;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDoing() {
        return doing;
    }

    public void setDoing(String doing) {
        this.doing = doing;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getVoice() {
        return voice;
    }

    public void setVoice(String voice) {
        this.voice = voice;
    }

    @Override
    public String toString() {
        return "LightingBean{" +
                "name='" + name + '\'' +
                ", doing='" + doing + '\'' +
                ", icon='" + icon + '\'' +
                ", voice='" + voice + '\'' +
                '}';
    }
}
