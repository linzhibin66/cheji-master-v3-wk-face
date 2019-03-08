package com.dgcheshang.cheji.Activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.dgcheshang.cheji.Camera.Test;
import com.dgcheshang.cheji.Database.DbConstants;
import com.dgcheshang.cheji.Database.DbHandle;
import com.dgcheshang.cheji.R;
import com.dgcheshang.cheji.Tools.LoadingDialogUtils;
import com.dgcheshang.cheji.Tools.Speaking;
import com.dgcheshang.cheji.netty.conf.NettyConf;
import com.dgcheshang.cheji.netty.po.Jlydl;
import com.dgcheshang.cheji.netty.po.Tdata;
import com.dgcheshang.cheji.netty.serverreply.JlydcR;
import com.dgcheshang.cheji.netty.serverreply.JlydlR;
import com.dgcheshang.cheji.netty.serverreply.SfrzR;
import com.dgcheshang.cheji.netty.timer.LoadingTimer;
import com.dgcheshang.cheji.netty.util.ByteUtil;
import com.dgcheshang.cheji.netty.util.ForwardUtil;
import com.dgcheshang.cheji.netty.util.MsgUtilClient;
import com.dgcheshang.cheji.netty.util.RlsbUtil;
import com.dgcheshang.cheji.netty.util.ZdUtil;
import com.haoxueche.cameralib.manager.ICameraManager;
import com.haoxueche.mz200alib.util.MessageUtil;

import org.apache.commons.lang3.StringUtils;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 教练员登录
 * */
public class LoginCoachActivity extends BaseInitActivity implements View.OnClickListener {
    public String TAG="LoginCoachActivity";
    Context context=LoginCoachActivity.this;
    public static final int LOGIN_COA_SUCCESS = 0;
    ImageView image_shuaka,image_zhiwen,image_shenfen;
    TextView tv_shenfen,tv_jlbh,tv_chexin,tv_coach_name,tv_zhiwen;
    View layout_shenfen;
    private TextView tv_title;
    SharedPreferences.Editor editor;
    Dialog loading;
    Dialog outloalDialog;
    LoadingTimer loadingTimer;
    Timer timer;
    SfrzR jlxx;
    String yzmm;
    SharedPreferences coachsp,zdcssp;
    private final Object mSync = new Object();
    BroadcastReceiver receiver;//下载广播
    //nfc   可刷true 不可刷false
    boolean isnfc=true;
    String bdpic="";//比对成功后的照片
    private ICameraManager cameraManager;
    boolean isback=true;//是否可按返回键
    Handler handler=new  Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.arg1==1){//教练登录
                image_shenfen.setBackgroundResource(R.mipmap.login_ok_y);
                handleIn(msg);
            }else if(msg.arg1==2){//教练员登出
                Bundle data = msg.getData();
                JlydcR jldcr = (JlydcR) data.getSerializable("jlydcr");//教练登录成功后返回来的数据
                handleOut(jldcr);
            }else if(msg.arg1==5){//获取教练信息
                Bundle data = msg.getData();
                jlxx = (SfrzR) data.getSerializable("jlxx");
                if(jlxx.getJg()==0){
                    getJlxx(jlxx);
                }else {
                    isnfc=true;
                    Speaking.in("无效卡");
                }

            }else if(msg.arg1==6){//uid
                String jluid = msg.getData().getString("jluid");
                image_shuaka.setBackgroundResource(R.mipmap.login_rid_jlcard_y);

                String sql="select * from tsfrz where uuid=? and lx=?";
                String[] params={jluid,"1"};
                ArrayList<SfrzR> list= DbHandle.queryTsfrz(sql,params);
                if(list.size()==0){
                    if(ZdUtil.pdNetwork()&&NettyConf.constate==1) {
                        ZdUtil.sendSfrz(jluid, "1");
                    }else {
                        Speaking.in("请连接服务器");
                    }
                }else{
                    jlxx=list.get(0);
                    getJlxx(jlxx);
                    isnfc=false;
                }
            }else if(msg.arg1==7){
                //指纹验证成功返回登录
                image_zhiwen.setBackgroundResource(R.mipmap.login_fingerprint_y);
                loading = LoadingDialogUtils.createLoadingDialog(context, "正在登录...");
                coachLogin();
            }else if(msg.arg1==8){
                coachOut2();
            }else if(msg.arg1==9){
                //指纹匹配成功登出
                coachOut1();
            }else if (msg.arg1==10){
                //强制登出返回回来处理
                int yzjg = msg.getData().getInt("yzjg");
                if(yzjg==0){
                    editor.putString("yzmm",yzmm);//保存验证密码
                    editor.commit();
                    if(NettyConf.ispz==false){
                        if (NettyConf.xystate==1){
                            Toast.makeText(context,"请先登出学员！",Toast.LENGTH_SHORT).show();
                        }else {
                            coachOut1();
                        }
                    }else {
                        Toast.makeText(context,",正在拍照请稍后操作",Toast.LENGTH_SHORT).show();
                    }
                }else {
                    loading.cancel();
                    Speaking.in("密码验证失败");
                }
            }else if(msg.arg1==15){
                //拍照完成返回
                Bundle data = msg.getData();
                bdpic=data.getString("pic");
                coachLogin();

            }else if(msg.arg1==16){
                //拍照完成返回
                Bundle data = msg.getData();
                bdpic=data.getString("pic");
                coachOut3();

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e("TAG","onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_coach);
        NettyConf.handlersmap.put("logincoach",handler);
        initView();
        if(NettyConf.jlstate!=1){
            Speaking.in("教练员请刷卡");
        }
    }

    /**
     *
     * 初始化布局
     * */
    private void initView() {
        //保存教练信息
        coachsp = getSharedPreferences("coach", Context.MODE_PRIVATE);
        zdcssp = getSharedPreferences("zdcs", Context.MODE_PRIVATE);
        editor = coachsp.edit();//获取编辑器
        View layout_back = findViewById(R.id.layout_back);//返回
        tv_title = (TextView) findViewById(R.id.tv_title);//标题
        //登录页面布局
        View layout_coachin = findViewById(R.id.layout_coachin);//登录布局
        layout_shenfen = findViewById(R.id.layout_shenfen);//身份信息布局
        layout_shenfen.setVisibility(View.INVISIBLE);
        image_shuaka = (ImageView) findViewById(R.id.image_shuaka);//刷卡图片
        image_zhiwen = (ImageView) findViewById(R.id.image_zhiwen);//指纹图片
        image_shenfen = (ImageView) findViewById(R.id.image_shenfen);//身份验证图片
        tv_zhiwen = (TextView) findViewById(R.id.tv_zhiwen);//验证指纹文字
        tv_shenfen = (TextView) findViewById(R.id.tv_shenfen);//身份证号
        tv_jlbh = (TextView) findViewById(R.id.tv_jlbh);//教练编号
        tv_coach_name = (TextView) findViewById(R.id.tv_coach_name);//教练姓名
        tv_chexin = (TextView) findViewById(R.id.tv_chexin);//车型
        //登出页面布局
        View layout_coachout = findViewById(R.id.layout_coachout);//登出布局
        Button bt_coachout = (Button) findViewById(R.id.bt_coachout);//登出按钮
        TextView tv_coachcode = (TextView) findViewById(R.id.tv_coachcode);//教练编号
        TextView tv_coachzj = (TextView) findViewById(R.id.tv_coachzj);//证件号码
        TextView tv_cartype = (TextView) findViewById(R.id.tv_cartype);//车牌类型
        TextView tv_coachname = (TextView) findViewById(R.id.tv_coachname);//教练姓名
        TextView tv_logintime = (TextView) findViewById(R.id.tv_logintime);//登录时间

        View layout_qzout = findViewById(R.id.layout_qzout);//强制登出
        //判断是否教练登录过
        if(NettyConf.jlstate==1){//登录过
            layout_coachin.setVisibility(View.GONE);
            layout_coachout.setVisibility(View.VISIBLE);
            layout_qzout.setVisibility(View.VISIBLE);
            tv_title.setText("教练员管理");
            tv_coachcode.setText(NettyConf.jbh);
            tv_cartype.setText(NettyConf.cx);
            tv_coachzj.setText(NettyConf.jzjhm);
            String jlxm = coachsp.getString("jlxm", "");
            tv_coachname.setText(jlxm);
            tv_logintime.setText(coachsp.getString("logintime1",""));

        }else {//没登录
            layout_coachin.setVisibility(View.VISIBLE);
            layout_coachout.setVisibility(View.GONE);
            layout_qzout.setVisibility(View.GONE);
        }

        layout_back.setOnClickListener(this);
        bt_coachout.setOnClickListener(this);
        layout_qzout.setOnClickListener(this);
    }

    /**
     * 点击监听
     * */
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.layout_back://返回
                finish();
                break;

            case R.id.bt_coachout://登出
                if(!ZdUtil.ispz){
                    if (NettyConf.xystate==1){
                        Toast.makeText(context,"请先登出学员！",Toast.LENGTH_SHORT).show();
                    }else {
                        loading = LoadingDialogUtils.createLoadingDialog(context, "教练登出中...");
                        coachOut();
                    }
                }else {
                    Toast.makeText(context,",正在拍照请稍后操作",Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.layout_qzout://强制退出
                showliuyanDialog();
                break;
        }
    }



    /**
     * 教练登录
     * */
    private void coachLogin() {
        try {
            if (ZdUtil.pdGps()) {
                String gnss = ZdUtil.getGnss();
                Jlydl jlydl = new Jlydl();
                jlydl.setJlybh(NettyConf.jbh);//教练员编号
                jlydl.setJlyzjhm(NettyConf.jzjhm);
                jlydl.setZjcx(NettyConf.cx);//车型
                jlydl.setGnss(gnss);
                byte[] b3 = jlydl.getJlydlbytes();
                byte[] b2 = MsgUtilClient.getMsgExtend(b3, "0101", "13", "2");
                List<Tdata> list = MsgUtilClient.generateMsg(b2, "0900", NettyConf.mobile, "1");

                if(ZdUtil.pdNetwork()&&NettyConf.constate==1&&NettyConf.jqstate==1) {
                    if(NettyConf.debug){
                        Log.e("TAG"+TAG,"发送教练数据");
                    }
                    ForwardUtil.sendData(list, 0,1);
                }else{
                    if(NettyConf.debug){
                        Log.e("TAG"+TAG,"缓存教练数据");
                    }
                    DbHandle.insertTdatas(list,1);

                    Message msg=new Message();
                    JlydlR jr=new JlydlR();
                    jr.setJg(1);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("jldlr",jr);
                    msg.setData(bundle);
                    handleIn(msg);
                }
            } else {
                Toast.makeText(context, "gps数据获取失败", Toast.LENGTH_SHORT).show();
                isback=true;
            }
        }catch(Exception e){
            Toast.makeText(context,"教练员登陆数据异常",Toast.LENGTH_SHORT).show();
            isback=true;
        }
    }

    /**
     * 教练登出
     * */
    private void coachOut() {
        String sql="select * from tsfrz where tybh=? and lx=?";
        String[] params={NettyConf.jbh,"1"};
        ArrayList<SfrzR> list= DbHandle.queryTsfrz(sql,params);
        if(list.size()==0){
            coachOut1();
        }else{
            SfrzR jlxx=list.get(0);
            getJlyxxOut(jlxx);
        }
        //coachOut1();
    }

    /**
     * 教练登出
     * */
    private void coachOut1() {
        //loading = LoadingDialogUtils.createLoadingDialog(context, "正在登出...");
        loadingTimer = new LoadingTimer(loading);
        timer = new Timer();
        timer.schedule(loadingTimer, NettyConf.controltime);

        ZdUtil.coachOut1();
    }

    private void coachOut2() {
        try {
            if (ZdUtil.pdGps()) {
                List<Tdata> list=ZdUtil.coachOut2();

                if(ZdUtil.pdNetwork()&&NettyConf.constate==1&&NettyConf.jqstate==1){
                    ForwardUtil.sendData(list, 1,7);
                }else{
                    DbHandle.insertTdatas(list,7);
                    JlydcR jr=new JlydcR();
                    jr.setJg(1);
                    handleOut(jr);
                }
            } else {
                Toast.makeText(context,"gps数据获取失败",Toast.LENGTH_SHORT).show();
                isback=true;
            }
        }catch(Exception e){
            isback=true;
            Toast.makeText(context,"教练员登出数据异常",Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 教练登出
     * */
    private void coachOut3() {
        //loading = LoadingDialogUtils.createLoadingDialog(context, "正在登出...");
        loadingTimer = new LoadingTimer(loading);
        timer = new Timer();
        timer.schedule(loadingTimer, NettyConf.controltime);
        //人脸方式
        try {

            ZdUtil.sendZpsc2("129", "0", "21",ZdUtil.getGnss4(),bdpic);

        }catch(Exception e){
            isback=true;
            Speaking.in("教练员登出数据异常");
        }

    }

    /**
     * 登录处理
     * */
    public synchronized void handleIn(Message msg){
        if(loadingTimer!=null) {
            loadingTimer.cancel();
        }
        if(timer!=null) {
            timer.cancel();
        }
        if(NettyConf.jlstate!=1) {
            Bundle data = msg.getData();
            JlydlR jldlr = (JlydlR) data.getSerializable("jldlr");//教练登录成功后返回来的数据

            if (jldlr.getJg() == 1) {//教练登录成功
                //存入缓存
                DbHandle.insertTsfrz(jlxx);

                editor.putString("jlbh", NettyConf.jbh);//教练编号
                editor.putInt("jlstate", 1);
                editor.putString("cx", NettyConf.cx);//教练车型
                editor.putString("jzjhm", NettyConf.jzjhm);//证件号码
                editor.putString("jlxm", jlxx.getXm());//教练姓名
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");//保存年月日
                SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//保存年月日
                editor.putString("logintime",sdf.format(new Date(System.currentTimeMillis())));
                editor.putString("logintime1",sdf1.format(new Date(System.currentTimeMillis())));
                editor.commit();//提交修改

                NettyConf.jlstate = 1;
                //上传拍照数据
                ZdUtil.sendZpsc2("129", "0", "20",ZdUtil.getGnss4(),bdpic);

                Intent intent = new Intent();
                setResult(LOGIN_COA_SUCCESS, intent);

                LoadingDialogUtils.closeDialog(loading);
                Speaking.in("教练登陆成功");
            } else {
                //删除缓存
                String[] params = {jlxx.getUuid(), String.valueOf(jlxx.getLx())};
                DbHandle.deleteData(DbConstants.T_SFRZ, "uuid=? and lx=?", params);
                //取消模态
                LoadingDialogUtils.closeDialog(loading);
                //报读原因
                if(StringUtils.isNotEmpty(jldlr.getFjxx())) {
                    Speaking.in(jldlr.getFjxx());
                }
            }
            isback=true;
            finish();
        }
    }

    /**
     * 登出处理
     * */
    public synchronized void handleOut(JlydcR jldcr){
        if(loadingTimer!=null){
            loadingTimer.cancel();
        }
        if(timer!=null){
            timer.cancel();
        }

        if(NettyConf.jlstate==1) {
            if (jldcr.getJg() == 1) {
                NettyConf.jlstate = 0;
                editor.putInt("jlstate", 0);
                editor.commit();

                Intent intent = new Intent();
                setResult(LOGIN_COA_SUCCESS, intent);
                editor.putString("logintime","");//清除登录时间
                LoadingDialogUtils.closeDialog(loading);
                Speaking.in("教练员登出成功");
                finish();
            } else {
                Speaking.in("教练员登出失败");
            }
        }
    }

    /**
     * 读卡成功后获取教练信息
     * */
    public void getJlxx(SfrzR jlxx){
        String xx = jlxx.getXx();//教练指纹
        NettyConf.cx = jlxx.getCx();//车型
        NettyConf.jbh = jlxx.getTybh();//统一编号
        NettyConf.jzjhm = jlxx.getSfzh();//身份证号

        //获取信息成功后显示身份信息
        layout_shenfen.setVisibility(View.VISIBLE);
        tv_shenfen.setText(jlxx.getSfzh());
        tv_chexin.setText(jlxx.getCx());
        tv_jlbh.setText(jlxx.getTybh());
        tv_coach_name.setText(jlxx.getXm());
        //人脸识别登录
        if (ZdUtil.ispz == false) {
            commonCoach2(jlxx, "jllogin");
        }else {
            Toast.makeText(context, "正在拍照，请稍后...", Toast.LENGTH_SHORT).show();
        }



    }

    /**
     * 教练登出
     */
    public void getJlyxxOut(SfrzR jlxx){

        //人脸识别
        if(ZdUtil.ispz==false){
            commonCoach2(jlxx,"jlout");
        }else {
            Toast.makeText(context,"正在拍照，请稍后...",Toast.LENGTH_SHORT).show();
         }

    }

    /**
     * 人脸识别通道
     * type 分为login和out
     * */
    public void commonCoach2(final SfrzR jlxx,final String type){
        isback=false;
        final String zp = jlxx.getZp();
        Log.e("TAG","教练下载图片路径："+zp);
        if(zp==null||zp.equals("")){
         Toast.makeText(context,"没有照片下载有效路径",Toast.LENGTH_SHORT).show();
            isback=true;
            return;
        }
        final String sfzh = jlxx.getSfzh();
        //判断文件夹是否存在
        RlsbUtil.isexistAndBuild(NettyConf.jlyxy_picurl);
        //教练原始照片路径
        final String jlzp=NettyConf.jlyxy_picurl+sfzh+".jpg";

        if(RlsbUtil.isFileExist(jlzp)==false){
            //没有教练照片去下载
//                    Toast.makeText(context,"正在下载教练员照片",Toast.LENGTH_SHORT).show();
            String  zp1=new String(ByteUtil.hexStringToByte(zp));
            Log.e("TAG","照片："+zp1);
            downFile(zp1,sfzh,jlzp,type);
        }else {
            //有教练照片直接抓拍验证,传“have_pic”状态
            rlsb(jlzp,sfzh, type);
        }
    }


    /**
     * 下载文件
     * */
    public void downFile(String url, final String sfzh, final String jlzp, final String type){

        //下载文件
        try {
            final DownloadManager dManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            Uri uri = Uri.parse(url);
            DownloadManager.Request request = new DownloadManager.Request(uri);
            // 设置下载路径和文件名
            request.setDestinationInExternalPublicDir("jlyxypic", sfzh+".jpg");
            request.setMimeType("image/jpeg");
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
                    //下载状态查询
//                DownloadManager.Query query = new DownloadManager.Query().setFilterById(refernece);
//                Cursor c = dManager.query(query);if (c != null && c.moveToFirst()) {
//                    int status = c.getInt(c.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS));
//                    switch (status) {
//                        case DownloadManager.STATUS_PENDING:
//                            break;
//                        case DownloadManager.STATUS_PAUSED:
//                            break;
//                        case DownloadManager.STATUS_RUNNING:
//                            break;
//                        case DownloadManager.STATUS_SUCCESSFUL:
//                            Log.e("TAG","下载成功");
//                            //下载完成操作，保存原照片 身份证号用来区别
//                            rlsb(jlzp, sfzh,type);
//                            break;
//                        case DownloadManager.STATUS_FAILED:
//                            Log.e("TAG","下载失败");
//                            Speaking.in("照片下载失败");
//                            break;
//                    }
//                    if (c != null) {
//                        c.close();
//                    }
//                }
                    if (refernece == myDwonloadID) {
                        //下载完成操作，保存原照片 身份证号用来区别
                        Log.e("TAG","下载教练照片成功");
                        if(RlsbUtil.isFileExist(jlzp)==true){
                            //照片存在
                            editor.putString("coachphoto",jlzp);
                            editor.commit();
                            rlsb(jlzp, sfzh,type);
                        }else {
                            //照片不错在
                            Log.e("TAG","教练员照片下载失败1");
                            Speaking.in("教练员照片下载失败");
                        }



                    }else {
                        isback=true;
                        Log.e("TAG","教练员照片下载失败2");
                        Speaking.in("教练员照片下载失败");
                    }
                }
            };
            registerReceiver(receiver, filter);
        }catch (Exception ex){
            isback=true;
            Log.e("TAG","教练员照片下载失败3");
            Speaking.in("教练员照片下载失败");
        }

    }

    /**
     * 人脸识别成功后处理教练登录或教练登出
     * ishave_pic 判断是否有教练照片，有则无需保存特征值，无则保存特征值 true有照片，false没照片
     * */
    Timer pztimer;
    TimerTask pztask;
    public void rlsb(final String jlzp, final String sfzh, final String type){
        Speaking.in("正在人脸识别，请对准摄像头");
        //只有当原始照片保存成功才进行人脸识别
        Test.floatTakePicture(getWindow().getDecorView(),sfzh,type,"logincoach");
//            pztimer = new Timer();
//            pztask=new TimerTask() {
//                @Override
//                public void run() {
//                    if(isfinishphoto<20){
//                        isfinishphoto++;
//                        takePhoto(getWindow().getDecorView());
//                        final Timer bdtimer = new Timer();
//                        TimerTask bdtask=new TimerTask() {
//                            @Override
//                            public void run() {
//                                bdtimer.cancel();
//                                String path = xypz;
//                                File file = new File(xypz);
//                                long length = file.length();
//                                Log.e("TAG","拍完照片长度"+length);
//                                boolean stopcamera=compare(path,sfzh);
//                                if(stopcamera==true){
//                                    //关闭摄像头
//                                    pztimer.cancel();
//                                    closeCamera();
//                                    ZdUtil.ispz=false;
//                                    final Timer rlcltimer = new Timer();
//                                    TimerTask task=new TimerTask() {
//                                        @Override
//                                        public void run() {
//                                            //识别正确人脸
//                                            if(type.equals("login")){
//                                                //登录处理
//                                                rlcltimer.cancel();
//                                                //保存教练证件照
//                                                editor.putString("coachphoto",jlzp);
//                                                editor.commit();
//                                                coachLogin();
//                                            }else if(type.equals("out")){
//                                                //登出处理
//                                                rlcltimer.cancel();
//                                                coachOut3();
//                                            }
//                                        }
//                                    };
//                                    rlcltimer.schedule(task,200);
//                                    RlsbUtil.addtimer(rlcltimer);
//                                }
//                            }
//                        };
//                        bdtimer.schedule(bdtask,2000);
//
//
//                    }else {
//                        //如果超过60秒则自动关闭页面
//                        pztimer.cancel();
//                        ZdUtil.ispz=false;
//                        closeCamera();
//                        finish();
//                    }
//
//                }
//            };
//            pztimer.schedule(pztask,200,4000);
//            RlsbUtil.addtimer(pztimer);


    }

    /**
     * 强制登出dialog
     *
     * */

    private void showliuyanDialog(){
        final AlertDialog builder = new AlertDialog.Builder(this,R.style.CustomDialog).create(); // 先得到构造器
        builder.show();
        builder.getWindow().setContentView(R.layout.dialog_appoint_edt);
        builder.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);//解决不能弹出键盘
        LayoutInflater factory = LayoutInflater.from(this);
        View view = factory.inflate(R.layout.dialog_appoint_edt, null);
        builder.getWindow().setContentView(view);
        final EditText edt_content = (EditText) view.findViewById(R.id.edt_content);
        TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
        Button bt_cacnel = (Button) view.findViewById(R.id.bt_cacnel);
        Button bt_sure = (Button) view.findViewById(R.id.bt_sure);
        tv_title.setText("登出验证");

        //取消
        bt_cacnel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.dismiss();
            }
        });

        //确定
        bt_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 yzmm = edt_content.getText().toString().trim();
                if(!yzmm.equals("")){
                    builder.dismiss();
                    loading = LoadingDialogUtils.createLoadingDialog(context, "正在登出...");
                    ZdUtil.matchPassword(1,yzmm);


                }else {
                    Toast.makeText(context,"请输入登出密码",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 返回键监听
     * */
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(isback==true){
                finish();
            }
            return false;
        }else {
            return super.onKeyDown(keyCode, event);
        }
    }
    @Override
    protected void onStop() {
        super.onStop();

        if(pztask!=null){
            pztask.cancel();
            ZdUtil.ispz=false;
        }
        if(pztimer!=null){
            pztimer.cancel();
            ZdUtil.ispz=false;
        }
    }

    /**
     * 关闭页面调用
     * */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //清除计时器
        if(timer!=null){
            timer.cancel();
        }

        //解绑下载照片广播
        if(receiver!=null){
            unregisterReceiver(receiver);
        }

        if(pztimer!=null){
            pztimer.cancel();
            ZdUtil.ispz=false;
        }
        NettyConf.handlersmap.remove("logincoach");
    }

    /**
     * nfc刷卡
     * */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String intentActionStr = intent.getAction();// 获取到本次启动的action
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intentActionStr)// NDEF类型
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(intentActionStr)// 其他类型
                || NfcAdapter.ACTION_TAG_DISCOVERED.equals(intentActionStr)) {// 未知类型
            //在intent中读取Tag id
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            byte[] bytesId = tag.getId();// 获取id数组get

            String  cardNo = MessageUtil.bytesToHexString(bytesId).toUpperCase();
            if(NettyConf.jlstate==0&&isnfc==true){
                Message msg = new Message();
                msg.arg1=6;
                Bundle bundle = new Bundle();
                bundle.putString("jluid",cardNo);
                msg.setData(bundle);
                handler.sendMessage(msg);

            }
        }
    }

}
