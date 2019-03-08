package com.dgcheshang.cheji.netty.conf;

import android.location.Location;

import com.dgcheshang.cheji.netty.po.Line;

import java.security.PrivateKey;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

/**
 * Created by Administrator on 2017/5/8 0008.
 */
public class NettyConf {
    public static boolean debug=true;//调试模式
    public static Boolean netstate;//网络状态
    public static boolean camerastate;//摄像头状态
    public static int background=0;//是否停止后台监听 0不停止，1停止
    public static boolean ispz=false;//是否在拍照，false没有，true在拍照
    public static boolean isback=false;//是否可以返回，false没有，true可以

    public static String sfjm1="0";//培训过程消息头里是否加密标志0不加密 1加密
    public static String sfjm2="0";//培训过程消息体内是否加密标志0不加密 2加密
    //启动初始化
    public static Map<String,Object> handlersmap=new HashMap<String,Object>();
    public static String mobile="";
    public static String imei="";
    public static String xbh="";
    public static String jbh="";
    public static String jzjhm="";//证件号码
    public static String cx="";
    public static String version="1.0";//app版本号
    public static String model="V600ID";//终端型号
    public static String SIMxlh="";//SIM卡序列号
    public static String uid ="65DF6FEB,75A56FEB,05DD6FEB,85E574EB,15846FEB,052F73EB,F55E73EB,254173EB";//管理员卡uid
    //下载存放路径
    public static final String fileurl = "/sdcard/APPdown";
    //教练员学员原始图保存路径
    public static final String jlyxy_picurl = "/sdcard/jlyxypic/";

    //本地存储
    public static String host="";
    public static int port;
    public static String zzsid="70218";//制造商id
    public static String zdxlh="0JTG40A";//终端序列号

    public static String ptbh="";//平台编号
    public static String pxjgbh="";//培训机构编号
    public static String jszdbh="";//终端统一编号
    public static String zs="";//证书
    public static String zskl="";//证书口令
    public static PrivateKey key=null;
    public static String pxkc="1212110000";//培训课程

    public static String ktid="0";//课堂ID默认值为0

    public static String cp="";
    public static String cpys="";
    public static String shengID="44";//省域id
    public static String shiID="1900";//市域id

    public static int zcstate=0;//注册状态存入内存0代表初始化，1代表注册功能
    public static int jlstate=0;//教练状态 0代表无教练 1代表教练登陆成功，登出返回0
    public static int xystate=0;//学员状态 0代表无学员登陆，1代表有学员登陆中，登出返回0

    public static String xydltime;

    //参数
    public static int xtjg=30;//心跳间隔
    public static int cfjg=30;//重发间隔
    public static int cfcs=3;//重传次数
    public static int dwfsjg=20;//定位信息发送间隔
    public static int dwfsjg2=600;//定位信息发送间隔
    public static int dwfsjg3=3600;//熄火后GNss发送间隔
    public static int dwfsjg4=15;//切换的时间间隔
    public static int makephotojg=900;//拍照发送间隔时间
    public static int xsjljg=60;//学时记录上报间隔时间
    public static int cxyzsj=30;//验证身份间隔
    public static String dzwlcl="0";//电子围栏策略0不启用1启用
    public static int dzwllxsj=5;//电子围栏轮询时间
    public static Timer dzwlTimer=null;//电子围栏定时器

    //缓存存储
    public static Timer cardtimer=null;
    public static Timer fringerTimer=null;

    //接收分包缓存数据
    public static Map<String,Object> fbdata=new HashMap<String,Object>();

    //发送分包缓存数据
    public static Map<String,Object> senddata=new HashMap<String,Object>();
    public static int fbzj=732;


    public static Map<String,Object> zpdataStr=new HashMap<String,Object>();

    public static int constate=0;//连接状态0为断开1为连接中存入缓存
    public static int jqstate=0;//鉴权状态0为初始化1为成功

    public static Location location;

    public static String xsgnss;
    public static float fx=0;//当前行驶方向
    public static Line line;//当前行驶线路
    public static int bdjl=15;//报读距离
    public static long controltime=30000l;//控件监听时间

    //定时器
    public static Map<String,Object> timermap=new HashMap<String,Object>();

    //服务
    public static Map<String,Object> servicemap=new HashMap<String,Object>();

    //1分钟内最大速度
    public static float zdsdM=0;


    //缓存数据保存时间
    public static long hcsj=30;//天数

    //缓存数据延时
    public static int level1=500;//毫秒  教练员登陆1级别（包含无需缓存）
    public static int level2=500;//毫秒  学员登陆2级别
    public static int level3=150;//毫秒   位置汇报3级别
    public static int level4=3000;//毫秒  照片数据4级别
    public static int level5=200;//毫秒   学时记录5级别
    public static int level6=5000;//毫秒    学员登出6级别
    public static int level7=50;//毫秒    教练登出7级别

    public static int level8=40;//发送照片间隔

    public static boolean sendState=true;

    public static int jrxxsc=0;

    public static Timer xltimer=null;

    public static int maxTime=235500;//最晚登陆时间
    public static int minTime=50000;//最早登陆时间

    public static int dtlxlong=240;//今日累计时长最大提醒值

    public static int jxzt=1;

    public static int autoroot=2;

}
