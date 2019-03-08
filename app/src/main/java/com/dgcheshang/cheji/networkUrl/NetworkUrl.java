package com.dgcheshang.cheji.networkUrl;

/**
 * Created by Administrator on 2017/6/21 0021.
 */

public class NetworkUrl  {
    //public static String IP="http://192.168.0.102:8042/TcpConsole/";//外网
    public static String IP="http://14.17.70.172:8889/TcpConsole/";//外网
//    public static String IP="http://119.147.149.60:8889/TcpConsole/";//汕头外网

    public static String UpdateCodeUrl = IP+"tcpconsole/zdzc!androidversion.action";//版本更新
    public static String uploadExceptionUrl=IP+"tcpconsole/zdzc!uploadException.action";//异常收集路径
    public static String Chejimusic=IP+"apkdownload/chejimusic.zip";//语音文件下载

}
