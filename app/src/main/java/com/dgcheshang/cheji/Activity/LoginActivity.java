package com.dgcheshang.cheji.Activity;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.dgcheshang.cheji.Activity.Lukao.LukaoActivity;
import com.dgcheshang.cheji.Bean.VersionBean;
import com.dgcheshang.cheji.Camera.SharePreferUtil;
import com.dgcheshang.cheji.Camera.Test;
import com.dgcheshang.cheji.CjApplication;
import com.dgcheshang.cheji.R;
import com.dgcheshang.cheji.Tools.LoadingDialogUtils;
import com.dgcheshang.cheji.Tools.Speaking;
import com.dgcheshang.cheji.Tools.Speakout;
import com.dgcheshang.cheji.broadcastReceiver.TrainReceiver;
import com.dgcheshang.cheji.netty.conf.NettyConf;
import com.dgcheshang.cheji.netty.thread.SpeakThread;
import com.dgcheshang.cheji.netty.timer.CacheTimer;
import com.dgcheshang.cheji.netty.timer.DzwlTimerTask;
import com.dgcheshang.cheji.netty.timer.LoginoutTimer;
import com.dgcheshang.cheji.netty.util.InitUtil;
import com.dgcheshang.cheji.netty.util.LocationUtil;
import com.dgcheshang.cheji.netty.util.ZdUtil;
import com.dgcheshang.cheji.networkUrl.NetworkUrl;
import com.google.gson.Gson;
import com.haoxueche.cameralib.manager.ICameraManager;
import com.haoxueche.cameralib.managerProxy.NativeCameraManagerProxy;
import com.haoxueche.cameralib.util.CameraInfo;
import com.haoxueche.cameralib.util.CameraWindowSize;
import com.haoxueche.mz200alib.util.InstallUtil;
import com.haoxueche.winterlog.L;
import com.rscja.customservices.ICustomServices;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

/**
 * 主菜单
 * */
public class LoginActivity extends BaseInitActivity implements View.OnClickListener {
    public static LoginActivity instance = null;

    Context context = LoginActivity.this;

    private static final String TAG = "LoginActivity";
    public static final int REQUEST_COACH = 1;//跳转教练登录页面
    public static final int REQUEST_STUDENT = 2;//跳转学员登录页面
    public static final int REQUEST_SETTING = 3;//跳转设置ip,端口页面
    Timer qzOutTimer;
    Dialog loading;
    TextView tv_coach_state,tv_student_state;
    SharedPreferences coachsp;
    SharedPreferences stusp;

    String fileurl="/sdcard/APPdown";//下载文件夹路径
    BroadcastReceiver receiver;//下载广播
    NetworkReceiver networkReceiver;//网络监听广播
    View layout_showphoto;
    SoundPool soundPool;
    boolean isFirstStartApp=false;//是否第一次启动app
    TrainReceiver trainReceiver;

    ICustomServices mCustomServices;

    public static boolean hyconstate=false;//华盈连接标志
    private ICameraManager cameraManager;

    Handler handler=new  Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.arg1==1) {//注销
                handleCancel(msg);
            }else if(msg.arg1==2){
                Bundle data = msg.getData();
                final String scms = data.getString("scms");
                final String tdh = data.getString("tdh");
                final String lx = data.getString("lx");
                final String gnss=data.getString("gnss");
                //拍照提前发出滴滴声
                soundPool.play(1,1, 1, 0, 0, 1);

                postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                        ZdUtil.sendZpsc2(scms,tdh,lx,gnss,"");
                        //  关闭摄像头
                        Test.floatTakePicture4(getWindow().getDecorView(),scms,tdh,lx,gnss);
                    }
                },2000);

            }else if(msg.arg1==3){

            }else if(msg.arg1==5){
                handleshow();
            }else if(msg.arg1==6){
                Bundle data = msg.getData();
                String url= (String) data.getSerializable("url");
                String version= (String) data.getSerializable("version");
                downFile(url, version);
            }else if(msg.arg1==11){
                //重启位置汇报广播
                Intent intent=new Intent();
                intent.setAction("wzhb");
                sendBroadcast(intent);
            }else if(msg.arg1==12){
                //重启系统或关机
                seReboot();
            }else if(msg.arg1==13){
                //强制拍照
                Bundle data = msg.getData();
                final String scms = data.getString("scms");
                final String tdh = data.getString("tdh");
                final String lx = data.getString("lx");
                final String gnss=data.getString("gnss");
                //拍照提前发出滴滴声
                soundPool.play(1,1, 1, 0, 0, 1);

                postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                        ZdUtil.sendZpsc2(scms,tdh,lx,gnss,"");
                        //  关闭摄像头
                        Test.floatTakePicture3(getWindow().getDecorView(),scms,tdh,lx,gnss);
                    }
                },2000);

            }else if(msg.arg1==22){
                //自动登出触发(可能)
                Intent xydcIntent=new Intent();
                xydcIntent.setAction("xydc");
                sendBroadcast(xydcIntent);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        com.rscja.deviceapi.OTG.getInstance().on(); //打开OTG
        //启动语音播放器
        new Thread(new SpeakThread()).start();
        trainReceiver=new TrainReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("wzhb");
        filter.addAction("xydl");
        filter.addAction("xydc");
        filter.addAction("autoLoginout");
        filter.addAction("autoLoginwarn");


        filter.addAction("reboot");
        this.registerReceiver(trainReceiver,filter);

        NettyConf.handlersmap.put("login",handler);

        //强制打开GPS
        try {
            if (!LocationUtil.isOPen()) {
                openGPS(this);
            }
        }catch(Exception e){}

        //初始化
        InitUtil.initSystem();
        initView();
        //广播
        registerReceiver();
        instance = this;

        //拍照秒提示嘀嘀声初始化
        soundPool= new SoundPool(10, AudioManager.STREAM_SYSTEM,5);
        soundPool.load(CjApplication.getInstance(), R.raw.didi4,1);

        //清除缓存数据
        CacheTimer cacheTimer=new CacheTimer();
        new Timer().scheduleAtFixedRate(cacheTimer,0,24*60*60*1000);

        //看是否学员登陆成功了学时汇报和拍照是否启动
        if(NettyConf.xystate==1){
            //发送学员登陆广播
            Intent xydlIntent=new Intent();
            xydlIntent.setAction("xydl");
            sendBroadcast(xydlIntent);

            try {
                SimpleDateFormat dff = new SimpleDateFormat("yyMMdd");
                String ee = dff.format(new Date());

                String ee2 = NettyConf.xydltime.substring(0, 6);
                if (!ee.equals(ee2)) {
                    Toast.makeText(context,"培训已跨天,学时无效,请登出后重新登陆！",Toast.LENGTH_SHORT).show();
                }
            }catch(Exception e){}
        }

        //定时验证身份
        //new Timer().scheduleAtFixedRate(new ValidateTimerTask(),NettyConf.cxyzsj*60*1000,NettyConf.cxyzsj*60*1000);

        //启动强制登出定时器
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date=new Date();//取时间
        Calendar calendar = Calendar.getInstance();
        StringBuffer sb=new StringBuffer(sdf.format(date).substring(0,11));
        sb.append("23:55:00");
        try {
            if(NettyConf.debug){
                Log.e("TAG","登出计时器触发时间:"+sb.toString());
            }
            Date d=sdf.parse(sb.toString());
            Intent intent=new Intent();
            intent.setAction("autoLoginout");
            PendingIntent pi=PendingIntent.getBroadcast(this, 0, intent,0);
            //设置一个PendingIntent对象，发送广播
            AlarmManager am=(AlarmManager)getSystemService(ALARM_SERVICE);
            //获取AlarmManager对象
            am.set(AlarmManager.RTC_WAKEUP, d.getTime(), pi);

            //在加一次自动登出
            calendar.setTime(d);
            calendar.add(Calendar.MINUTE, 5);
            Date dd=calendar.getTime();
            new Timer().schedule(new LoginoutTimer(), dd);

            calendar.setTime(d);
            calendar.add(Calendar.MINUTE, -5);

            Date d2=calendar.getTime();
            if(NettyConf.debug){
                Log.e("TAG","登出报警计时器触发时间:"+sdf.format(d2));
            }

            Intent i2=new Intent();
            i2.setAction("autoLoginwarn");
            PendingIntent pi2=PendingIntent.getBroadcast(this, 0, i2,0);
            AlarmManager am3=(AlarmManager)getSystemService(ALARM_SERVICE);
            am3.set(AlarmManager.RTC_WAKEUP, d2.getTime(), pi2);

            calendar.setTime(d);
            calendar.add(Calendar.MINUTE, 5);
            calendar.add(Calendar.SECOND,(int)(Math.random()*(59)));
            Date d3=calendar.getTime();

            Intent i3=new Intent();
            i3.setAction("reboot");
            PendingIntent pi3=PendingIntent.getBroadcast(this, 0, i3,0);
            AlarmManager am4=(AlarmManager)getSystemService(ALARM_SERVICE);
            am4.set(AlarmManager.RTC_WAKEUP, d3.getTime(), pi3);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        /**
         * 启动电子围栏
         */
        if("1".equals(NettyConf.dzwlcl)) {
            NettyConf.dzwlTimer=new Timer();
            NettyConf.dzwlTimer.schedule(new DzwlTimerTask(), 0, NettyConf.dzwllxsj*1000);
        }
        //测试摄像头
        if(NettyConf.ispz==false){
            Test.floatTakePicture5(getWindow().getDecorView());
        }
    }

    /**
     * 初始化布局
     * */
    private void initView() {
         coachsp = getSharedPreferences("coach", Context.MODE_PRIVATE);
        int jlstate = coachsp.getInt("jlstate", 0);
        stusp = getSharedPreferences("student", Context.MODE_PRIVATE);
        int xystate = stusp.getInt("xystate", 0);

        View layout_back = findViewById(R.id.layout_back);//返回
        View layout_coach = findViewById(R.id.layout_coach);//教练
        View layout_student = findViewById(R.id.layout_student);//学员
        tv_coach_state = (TextView) findViewById(R.id.tv_coach_state);//教练显示状态
        tv_student_state = (TextView) findViewById(R.id.tv_student_state);//学员显示状态
        View layout_cardetail = findViewById(R.id.layout_cardetail);//车辆信息
        View layout_about = findViewById(R.id.layout_about);//关于我们
        View layout_lukao = findViewById(R.id.layout_lukao);//模拟路考
        View layout_setting = findViewById(R.id.layout_setting);//参数设置
        View layout_basic_set = findViewById(R.id.layout_basic_set);//基本设置
        layout_showphoto = findViewById(R.id.layout_showphoto);//显示拍照框
        layout_showphoto.setVisibility(View.INVISIBLE);
        layout_basic_set.setOnClickListener(this);
        layout_back.setOnClickListener(this);
        layout_coach.setOnClickListener(this);
        layout_student.setOnClickListener(this);
        layout_cardetail.setOnClickListener(this);
        layout_about.setOnClickListener(this);
        layout_setting.setOnClickListener(this);
        layout_lukao.setOnClickListener(this);
        layout_showphoto.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //点击其他地方关闭摄像头页面
                layout_showphoto.setVisibility(View.INVISIBLE);
                //关闭摄像头
                releaseCamera();
                return true;
            }
        });
    }

    /**
     * 点击监听
     * */
    @Override
    public void onClick(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.layout_back://摄像头
                layout_showphoto.setVisibility(View.VISIBLE);
                preview(getWindow().getDecorView());
                break;

            case R.id.layout_coach://教练员登录
                if(ZdUtil.canLogin()){
                    intent.setClass(context, LoginCoachActivity.class);
                    startActivityForResult(intent, REQUEST_COACH);
                }else {
                    Toast.makeText(context,"当前正在拍照，请稍后",Toast.LENGTH_SHORT).show();
                }

                break;

            case R.id.layout_student://学员登录

                if(ZdUtil.canLogin()){
                    if(NettyConf.jlstate==1) {
                        //教练员已登录
                        intent.setClass(context, LoginStudentActivity.class);
                        startActivityForResult(intent, REQUEST_COACH);
                    }else {
                        //教练员未登录
                        Toast.makeText(context,"教练员请先登录",Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(context,"当前正在拍照，请稍后",Toast.LENGTH_SHORT).show();
                }

                break;

            case R.id.layout_setting://参数设置
                intent.setClass(context,MainActivity.class);
                startActivityForResult(intent, REQUEST_SETTING);
                break;

            case R.id.layout_basic_set://基本设置
                intent.setClass(context,SystemSetActivity.class);
                startActivityForResult(intent, REQUEST_SETTING);
                break;

            case R.id.layout_cardetail://车辆信息
                intent.setClass(context,CarDetailActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;

            case R.id.layout_about://关于我们
                intent.setClass(context,AboutActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            case R.id.layout_lukao://模拟路考
                intent.setClass(context, LukaoActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;



        }
    }
    /**
     * 跳转页面返回回来结果处理
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode){
            case REQUEST_COACH://教练页面返回来
                switch (resultCode){
                    case LoginCoachActivity.LOGIN_COA_SUCCESS://教练返回回来

                        break;
                }
                break;

            case REQUEST_STUDENT://学员页面返回来
                switch (resultCode){
                    case LoginStudentActivity.LOGIN_STU_SUCCESS://学员返回回来

                        break;
                }
                break;

        }
    }


    /**
     * 控制返回键无效
     * */
    public boolean onKeyDown(int keyCode,KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0){
//            //这里重写返回键
//            return true;
        }
        return false;
    }


    /**
     * 注销返回处理
     *
     * @param msg*/
    public void handleCancel(Message msg){

        Bundle data = msg.getData();
        int zxjg = (int) data.get("zxjg");
        if(zxjg==1){//注销成功
            //清除定位计时器
            Object o=NettyConf.timermap.get("wzhb");
            if(o!=null){
                Timer timer= (Timer) o;
                timer.cancel();
            }
            //清除定位服务
            o=NettyConf.servicemap.get("wzhb");
            if(o!=null){
                Intent intent= (Intent) o;
                stopService(intent);
            }
            NettyConf.zcstate=0;//改变注册状态
            NettyConf.jqstate=0;//改变鉴权状态
            //清除保存状态
            SharedPreferences jianquan = getSharedPreferences("jianquan", Context.MODE_PRIVATE);
            Intent intent = new Intent();
            intent.setClass(context,MainActivity.class);
            jianquan.edit().clear();
            startActivity(intent);
            finish();
        }else {
            //注销失败
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        handleshow();
    }

    public void handleshow(){
        if(NettyConf.jlstate==1){
            tv_coach_state.setText("教练员管理(已登录)");
        }else {
            tv_coach_state.setText("教练员管理(未登录)");
        }
        if(NettyConf.xystate==1){
            tv_student_state.setText("学员管理(已登录)");
        }else {
            tv_student_state.setText("学员管理(未登录)");
            //息屏
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }




    /**
     * app获取版本,是否需要更新
     * */
    public void getVersion( ) {
        StringRequest request = new StringRequest(Request.Method.POST, NetworkUrl.UpdateCodeUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Gson gson = new Gson();
                    VersionBean versionbean = gson.fromJson(response, VersionBean.class);

                    //管理员卡uid
                    if(!versionbean.getManageruid().equals("")){
                        //保存管理员卡号
                        SharedPreferences uidsp = getSharedPreferences("uid", Context.MODE_PRIVATE);
                        SharedPreferences.Editor edit = uidsp.edit();
                        if(NettyConf.debug){
                            Log.e("TAG",versionbean.getManageruid());
                        }
                        edit.putString("uid",versionbean.getManageruid());
                        edit.commit();
                    }
                    String version = versionbean.getVersion();//版本号
                    String url = versionbean.getUrl();//下载路径
                    //判断是否版本一致
                    if (Double.valueOf(versionbean.getVersion())>Double.valueOf(NettyConf.version)) {
                        //进行版本更新
//                        updateDialog(versionbean.getUrl(), versionbean.getMsg());
                        if(versionbean.getImei().equals("")){
                            //全部更新
                            updateDialog(url,version);
                        }else {
                            //个别更新
                            String imei = versionbean.getImei();
                            String[] split = imei.split(",");
                            for (int i=0; i<split.length;i++){
                                if(split[i].equals(NettyConf.imei)){
                                    updateDialog(url,version);
                                    return;
                                }

                            }
                        }
                    }else {
                        //没有新版本更新，删除创建的文件夹里的文件
                        File file = new File(fileurl);
                        deleteAllFiles(file);

                    }
                }catch (Exception e){

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("TGA","volleyError="+volleyError);
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                return map;
            }
        };
        CjApplication.getHttpQueue().add(request);
    }

    /**
     * 删除文件夹底下所有文件
     * */

    private void deleteAllFiles(File root) {
        File files[] = root.listFiles();
        if (files != null)
            for (File f : files) {
                if (f.isDirectory()) { // 判断是否为文件夹
                    deleteAllFiles(f);
                    try {
                        f.delete();
                    } catch (Exception e) {
                    }
                } else {
                    if (f.exists()) { // 判断是否存在
                        deleteAllFiles(f);
                        try {
                            f.delete();
                        } catch (Exception e) {
                        }
                    }
                }
            }
    }

    /**
     * 版本更新提
     *
     * @param url
     * @param version*/
    private void updateDialog(final String url, final String version){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);  //先得到构造器
        builder.setTitle("版本提示"); //设置标题
        builder.setMessage("有新版本"+version+"，是否更新？\n(全程自动安装，请等待系统自动重启安装完成)"); //设置内容
        builder.setPositiveButton("更新",     new DialogInterface.OnClickListener() { //设置确定按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                boolean b = fileIsExists(version);//判断文件夹或者最新apk文件是否存在
                if(b==true){
                    //存在
                    updateApp(version);
                }else {
                    //不存在
                    downFile(url,version);
                }

            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() { //设置取消按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        //参数都设置完成了，创建并显示出来//不可按返回键取消
        builder.setCancelable(false).create().show();
    }

    /**
     * 判断文件夹和最新apk是否存在
     *
     * @param version*/
    public boolean fileIsExists(String version)
    {
            File f=new File(fileurl);
            if(!f.exists()){
               //不存在
                f.mkdirs();
                return false;
            }else {
               //文件夹存在
                File file = new File(fileurl + "/cheji" + version + ".apk");
                //判断最新版本apk是否存在
                if(!file.exists()){
                    return false;
                }else {
                    return true;
                }
            }

    }

    /**
     * 下载文件
     * */
    public void downFile(String url, final String version){
        if(loading!=null){
            loading.cancel();
        }
        loading = LoadingDialogUtils.createLoadingDialog(context, "自动完成更新，无需操作...");
        loading.setCancelable(false);
        //下载文件
        final DownloadManager dManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        // 设置下载路径和文件名
        request.setDestinationInExternalPublicDir("APPdown", "cheji"+version+".apk");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        request.setDescription("培训系统app正在下载");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setMimeType("application/vnd.android.package-archive");
        request.setAllowedOverRoaming(false);
        // 设置为可被媒体扫描器找到
        request.allowScanningByMediaScanner();
        // 设置为可见和可管理
         request.setVisibleInDownloadsUi(true);
        // 获取此次下载的ID
         final long refernece = dManager.enqueue(request);

        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);

        receiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                       long myDwonloadID = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (refernece == myDwonloadID) {
//                                Intent install = new Intent(Intent.ACTION_VIEW);
//                                Uri downloadFileUri = dManager.getUriForDownloadedFile(refernece);
//                                 install.setDataAndType(downloadFileUri, "application/vnd.android.package-archive");
//                                install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                startActivity(install);

                                updateApp(version);
                               }
                        }
              };
         registerReceiver(receiver, filter);
        }

    /**
     * 自动升级
     *
     * @param version*/
    public void updateApp(String version){

        InstallUtil.install(context,NettyConf.fileurl+"/cheji.apk");
    }

    /**
     * 注册网络监听广播
     * */

    private  void registerReceiver(){
        IntentFilter filter=new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        networkReceiver = new NetworkReceiver();
        this.registerReceiver(networkReceiver, filter);
    }

    /**
     * 网络广播
     * */
    public class NetworkReceiver extends BroadcastReceiver {
        boolean shenji=false;
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mobNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            NetworkInfo wifiNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

            boolean state;

            if (!mobNetInfo.isConnected() && !wifiNetInfo.isConnected()) {
                state=false;
            }else{
                state=true;
                if(shenji==false){
                    getVersion();//获取版本更新
                    shenji=true;
                }

            }

            if (NettyConf.netstate == null) {
                NettyConf.netstate = state;
                if(state){
                    ZdUtil.conServer();
                }
            } else if (NettyConf.netstate != state) {
                NettyConf.netstate = state;
                //如果网络变化
                if (state) {
                    ZdUtil.conServer();
                } else {
                    NettyConf.constate = 0;
                    NettyConf.jqstate = 0;
                    Speaking.in("网络已断开");
                }
            }

        }
    }

    /**
     * 释放home键跟菜单键，及下拉通知栏功能
     * */
    boolean b;

    public void seReboot(){
        Intent intentCust = new Intent();
        intentCust.setAction("com.rscja.CustomService");
        intentCust.setPackage("com.rscja.customservices");
        b = bindService(intentCust, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mCustomServices = ICustomServices.Stub.asInterface(iBinder);
            try {
                if(NettyConf.autoroot==2) {
                    mCustomServices.reboot();
                }else if(NettyConf.autoroot==1){
                    mCustomServices.shutdown();
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    /**
     * 强制帮用户打开GPS
     * @param context
     */
    public static final void openGPS(Context context) {
        Intent GPSIntent = new Intent();
        GPSIntent.setClassName("com.android.settings",
                "com.android.settings.widget.SettingsAppWidgetProvider");
        GPSIntent.addCategory("android.intent.category.ALTERNATIVE");
        GPSIntent.setData(Uri.parse("custom:3"));
        try {
            PendingIntent.getBroadcast(context, 0, GPSIntent, 0).send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }



    /**
     * 教练登录时间不是同一天强制登出
     * */
    public void compelobligeOut(){
        SharedPreferences coachsp = getSharedPreferences("coach", Context.MODE_PRIVATE);
        String logintime = coachsp.getString("logintime", "");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String format = sdf.format(new Date(System.currentTimeMillis()));
        if(!logintime.equals("")&&!logintime.equals(format)&&NettyConf.jlstate==1){
            //登录时间不是同一天
            final Dialog qzoutDialog = LoadingDialogUtils.createLoadingDialog(context, "正在强制登出，请稍后...");
            qzoutDialog.setCancelable(false);
            qzOutTimer = new Timer();
            qzOutTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    qzoutDialog.cancel();
                }
            },10000);
            new Timer().schedule(new LoginoutTimer(), 100);
            SharedPreferences.Editor edit = coachsp.edit();
            //清除登录时间
            edit.putString("logintime","");
            edit.commit();
        }
    }


    /**
     * 登录方式dialog
     * */
    private void logintypeDialog(final int whologin){
        final AlertDialog builder = new AlertDialog.Builder(this,R.style.CustomDialog).create(); // 先得到构造器
        builder.show();
        builder.getWindow().setContentView(R.layout.dialog_login_type);
        builder.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);//解决不能弹出键盘
        LayoutInflater factory = LayoutInflater.from(this);
        View view = factory.inflate(R.layout.dialog_login_type, null);
        builder.getWindow().setContentView(view);
        View login_face = view.findViewById(R.id.login_face_layout);
        View login_fingerprint = view.findViewById(R.id.login_fingerprint_layout);//指纹
        final Intent intent = new Intent();
        //指纹登录
        login_fingerprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(whologin==1){
                    //教练员
                    coachsp.edit().putString("jl_logintype","1").commit();
                    intent.setClass(context, LoginCoachActivity.class);
                    startActivityForResult(intent, REQUEST_COACH);

                }else {
                    //学员
                    stusp.edit().putString("xy_logintype","1").commit();
                    intent.setClass(context, LoginStudentActivity.class);
                    startActivityForResult(intent, REQUEST_STUDENT);

                }
                builder.cancel();
            }
        });

        //人脸识别登录
        login_face.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(whologin==1){
                    //教练员
                    intent.setClass(context, LoginCoachActivity.class);
                    startActivityForResult(intent, REQUEST_COACH);
                    coachsp.edit().putString("jl_logintype","4").commit();

                }else {
                    //学员
                    intent.setClass(context, LoginStudentActivity.class);
                    startActivityForResult(intent, REQUEST_STUDENT);
                    stusp.edit().putString("xy_logintype","4").commit();

                }
                builder.cancel();
            }
        });

    }


    public void preview(View view) {
        int cameraId = SharePreferUtil.isTimingPhotoUseCamera0() ? CameraInfo
                .CAMERA_FACING_INSIDE : CameraInfo.CAMERA_FACING_OUT;
        boolean previewMirrored = (cameraId == Camera.CameraInfo.CAMERA_FACING_FRONT) &&
                SharePreferUtil.isOutPhotoMirrorFlip();
        final boolean cameraRotate;
        if (cameraId == CameraInfo.CAMERA_FACING_OUT) {
            cameraRotate = SharePreferUtil.isOutCameraRotate();
        } else {
            cameraRotate = SharePreferUtil.isInsideCameraRotate();
        }
        releaseCamera();
        final ICameraManager iCameraManager = new NativeCameraManagerProxy();
        iCameraManager.init(CameraWindowSize.WINDOW_SIZE_SMALL, cameraId, previewMirrored,
                cameraRotate).observeOn(AndroidSchedulers.mainThread()).subscribe(new SingleObserver<ICameraManager>() {


            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onSuccess(ICameraManager iCameraManager) {
                //预览成功
                cameraManager = iCameraManager;
            }

            @Override
            public void onError(Throwable e) {
                L.e(e);
                Toast.makeText(CjApplication.getInstance(), "相机预览出错: " + e.getMessage(), Toast
                        .LENGTH_SHORT).show();
                if(iCameraManager != null) {
                    iCameraManager.release();
                }
            }
        });
    }

    private void releaseCamera() {
        if(cameraManager != null) {
            cameraManager.release();
            cameraManager = null;
        }
    }


    @Override
    public void onDestroy() {
        Log.e("TAG", "onDestroy:");
        //stopAudio();


        if(qzOutTimer!=null){
            qzOutTimer.cancel();
        }

        //解绑广播
        if(receiver!=null){
            unregisterReceiver(receiver);
        }
        if(networkReceiver!=null){
            unregisterReceiver(networkReceiver);
        }
        if(trainReceiver!=null) {
            this.unregisterReceiver(trainReceiver);
        }
        if(loading!=null){
            loading.cancel();
        }

        if(Speakout.tts!=null){
            Speakout.tts.stop();
            Speakout.tts.shutdown();
            Speakout.tts=null;
        }

        if(b){
            unbindService(mServiceConnection);
        }

        super.onDestroy();
    }


}
