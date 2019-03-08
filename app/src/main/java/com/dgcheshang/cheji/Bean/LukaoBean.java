package com.dgcheshang.cheji.Bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/9/14 0014.
 */

public class LukaoBean implements Serializable{
    private String name;
    private String content;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "LukaoBean{" +
                "name='" + name + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
