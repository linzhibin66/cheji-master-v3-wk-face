package com.dgcheshang.cheji.netty.po;

import java.util.ArrayList;
import java.util.List;

/***********************************************
 * @项目名称：cheji-master-3.5
 * @文件名称：ClearJlcd
 * @文件描述：
 * @文件作者：joxhome
 * @创建时间：2017/11/27 10:25
 ***********************************************/
public class ClearJlcd {
    private int num;//区域数
    private List<String> slist=new ArrayList<String>();//区域ID集合

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public List<String> getSlist() {
        return slist;
    }

    public void setSlist(List<String> slist) {
        this.slist = slist;
    }
}
