package com.haoxueche.cameralib.alivedect;//package com.haoxueche.camerademo.alivedect;
//
//import vcolco.hxc.terminal.BuildConfig;
//import vcolco.hxc.terminal.utils.SharePreferUtil;
//
///**
// * Created by Lyc(987424501@qq.com) on 2018/12/3.
// */
//public class AliveDectManager {
//    private boolean aliveDectAvailable;
//
//    private AliveDectManager(){
//        aliveDectAvailable = BuildConfig.ALIVE_DECT_ENABLED;
//    }
//
//    public boolean isAliveDectAvailable() {
//        return aliveDectAvailable;
//    }
//
//    public void setAliveDectAvailable(boolean aliveDectAvailable) {
//        this.aliveDectAvailable = aliveDectAvailable;
//    }
//
//    public boolean isStuSignInAliveDectEnabled() {
//        return aliveDectAvailable && SharePreferUtil.isStuSignInAliveDectEnabled();
//    }
//
//    public boolean isCoachSignInAliveDectEnabled() {
//        return aliveDectAvailable && SharePreferUtil.isCoachSignInAliveDectEnabled();
//    }
//
//    public boolean isStuSignOutAliveDectEnabled() {
//        return aliveDectAvailable && SharePreferUtil.isStuSignOutAliveDectEnabled();
//    }
//
//    public boolean isCoachSignOutAliveDectEnabled() {
//        return aliveDectAvailable && SharePreferUtil.isCoachSignOutAliveDectEnabled();
//    }
//
//
//    public static AliveDectManager getInstance() {
//        return InstanceHolder.instance;
//    }
//
//    private static class InstanceHolder {
//        public static final AliveDectManager instance = new AliveDectManager();
//    }
//
//}
