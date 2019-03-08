package com.dgcheshang.cheji.Activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
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
import com.dgcheshang.cheji.netty.po.Tdata;
import com.dgcheshang.cheji.netty.po.Xydl;
import com.dgcheshang.cheji.netty.proputil.PxkcUtil;
import com.dgcheshang.cheji.netty.serverreply.SfrzR;
import com.dgcheshang.cheji.netty.serverreply.XydcR;
import com.dgcheshang.cheji.netty.serverreply.XydlR;
import com.dgcheshang.cheji.netty.timer.LoadingTimer;
import com.dgcheshang.cheji.netty.timer.XsjlTimer;
import com.dgcheshang.cheji.netty.util.ByteUtil;
import com.dgcheshang.cheji.netty.util.CountDistance;
import com.dgcheshang.cheji.netty.util.ForwardUtil;
import com.dgcheshang.cheji.netty.util.MsgUtilClient;
import com.dgcheshang.cheji.netty.util.RlsbUtil;
import com.dgcheshang.cheji.netty.util.ZdUtil;
import com.haoxueche.mz200alib.util.MessageUtil;
import org.apache.commons.lang3.StringUtils;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;


/**
 * 学员登录
 * */
public class LoginStudentActivity extends BaseInitActivity implements View.OnClickListener{

    Context context=LoginStudentActivity.this;
    private String TAG="LoginStudentActivity";
    public static final int REQUEST_A = 1;
    public static final int LOGIN_STU_SUCCESS = 1;

    ImageView image_shuaka,image_zhiwen,image_shenfen,image_project;
    SharedPreferences stusp,zdcssp;
    View layout_shenfen;
    TextView tv_bianhao,tv_idcard,tv_kechen,tv_carlx,tv_stu_name,tv_logintime,tv_IDcard,tv_stuname,tv_cartype,tv_valid_time,tv_zhiwen;
    SharedPreferences.Editor stuedit;
    Dialog loading;
    LoadingTimer loadingTimer;
    Timer timer;
    SfrzR xyxx;//全局参数
    String yzmm;
    String bdpic="";//比对成功后的照片
    private final Object mSync = new Object();
    BroadcastReceiver receiver;//下载广播
    //nfc   可刷true 不可刷false
    boolean isnfc=true;
    Handler handler=new  Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.arg1==1){
                //学员登录
                image_shenfen.setBackgroundResource(R.mipmap.login_ok_y);
                handleIn(msg);
            }else if(msg.arg1==2){
                //学员登出
                Bundle data = msg.getData();
                XydcR xydcr = (XydcR) data.getSerializable("xydcr");//学员登录成功后返回来的数据
                handleOut(xydcr);

            }else if(msg.arg1==5){
                //读卡获取学员信息
                Bundle data = msg.getData();
                xyxx = (SfrzR) data.getSerializable("xyxx");
                if(xyxx.getJg()==0){
                    getXyxx(xyxx);
                }else {
                    isnfc=true;
                    Speaking.in("无效卡");
                }

            }else if(msg.arg1==6){//uid
                isnfc=false;
                String xyuid = msg.getData().getString("xyuid");
                image_shuaka.setBackgroundResource(R.mipmap.login_rid_xycard_y);

                String sql="select * from tsfrz where uuid=? and lx=?";
                String[] params={xyuid,"4"};
                ArrayList<SfrzR> list= DbHandle.queryTsfrz(sql,params);
                if(list.size()==0){
                    if(ZdUtil.pdNetwork()&&NettyConf.constate==1) {
                        ZdUtil.sendSfrz(xyuid,"4");
                    }else {
                        Speaking.in("请连接服务器");
                    }
                }else{
                    xyxx=list.get(0);
                    getXyxx(xyxx);
                }
            }else if(msg.arg1==7){//验证指纹成功后
                //学员登录
                image_zhiwen.setBackgroundResource(R.mipmap.login_fingerprint_y);
                loading = LoadingDialogUtils.createLoadingDialog(context, "登录中...");
                studentLogin();
            }else if(msg.arg1==8){
                studentOut2();
            }else if(msg.arg1==9){
                //登出拍照
                studentOut1();
            }else if(msg.arg1==10){
                //强制登出验证返回结果
                int yzjg = msg.getData().getInt("yzjg");
                if(yzjg==0){
                    stuedit.putString("yzmm",yzmm);//保存验证密码
                    stuedit.commit();
                    if(NettyConf.ispz==false){
                        studentOut1();
                    }else {
                        Toast.makeText(context,",正在拍照请稍后操作",Toast.LENGTH_SHORT).show();
                    }
                }else {
                    loading.cancel();
                    Speaking.in("密码验证失败");
                }

            }else if(msg.arg1==15){
                loading = LoadingDialogUtils.createLoadingDialog(context, "登录中...");
                //拍照完成返回
                Bundle data = msg.getData();
                bdpic=data.getString("pic");
                studentLogin();

            }else if(msg.arg1==16){
                //拍照完成返回
                Bundle data = msg.getData();
                bdpic=data.getString("pic");
                studentOut3();

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_student);
        NettyConf.handlersmap.put("loginstudent",handler);
        initView();
        if(NettyConf.xystate!=1) {
            Speaking.in("请选择培训课程");
            isnfc=false;
        }
    }

    /**
     * 初始化布局
     * */
    private void initView() {
        //保存教练信息
        stusp = getSharedPreferences("student", Context.MODE_PRIVATE); //私有数据
        zdcssp = getSharedPreferences("zdcs", Context.MODE_PRIVATE);
        stuedit = stusp.edit();//获取编辑器
        View layout_back = findViewById(R.id.layout_back);
        TextView tv_title = (TextView) findViewById(R.id.tv_title);//标题
        tv_kechen = (TextView) findViewById(R.id.tv_kechen);//选择课程显示
        //学员登录布局
        View layout_studentin = findViewById(R.id.layout_studentin);//学员登录布局
        layout_shenfen = findViewById(R.id.layout_shenfen);//身份证布局
        image_shuaka = (ImageView) findViewById(R.id.image_shuaka);//刷卡图片
        image_zhiwen = (ImageView) findViewById(R.id.image_zhiwen);//指纹图片
        image_shenfen = (ImageView) findViewById(R.id.image_shenfen);//身份图片
        image_project = (ImageView) findViewById(R.id.image_project);//课堂图片
        tv_zhiwen = (TextView) findViewById(R.id.tv_zhiwen);//验证指纹文字
        tv_bianhao = (TextView) findViewById(R.id.tv_bianhao);//学员编号
        tv_idcard = (TextView) findViewById(R.id.tv_idcard);//身份证号
        tv_stu_name = (TextView) findViewById(R.id.tv_stu_name);//姓名
        tv_carlx = (TextView) findViewById(R.id.tv_carlx);//车型
        layout_shenfen.setVisibility(View.INVISIBLE);
        //学员登出布局
        View layout_studentout = findViewById(R.id.layout_studentout);//学员登出布局
        Button bt_studentout = (Button) findViewById(R.id.bt_studentout);//登出按钮
        Button bt_choose = (Button) findViewById(R.id.bt_choose);//课程选择按钮
        TextView tv_studentcode = (TextView) findViewById(R.id.tv_studentcode);//学员编号
        TextView tv_zxs = (TextView) findViewById(R.id.tv_zxs);//总学时
        TextView tv_ywcxs = (TextView) findViewById(R.id.tv_ywcxs);//已完成学时
        TextView tv_todayxs = (TextView) findViewById(R.id.tv_todayxs);//今日学时
        TextView tv_zlc = (TextView) findViewById(R.id.tv_zlc);//总里程
        TextView tv_ywclc = (TextView) findViewById(R.id.tv_ywclc);//已完成里程
        tv_logintime = (TextView) findViewById(R.id.tv_logintime);//登录时间
        tv_valid_time = (TextView) findViewById(R.id.tv_valid_time);//有效培训时长
        tv_IDcard = (TextView) findViewById(R.id.tv_IDcard);//身份证号
        tv_stuname = (TextView) findViewById(R.id.tv_stuname);//姓名
        tv_cartype = (TextView) findViewById(R.id.tv_cartype);//车型
        Button bt_validtime = (Button) findViewById(R.id.bt_validtime);//有效学时查询
        bt_validtime.setOnClickListener(this);
        View layout_qzout = findViewById(R.id.layout_qzout);//强制登出
        // 判断学员是否登录
        if(NettyConf.xystate==1){//已登录
            layout_studentin.setVisibility(View.GONE);
            layout_studentout.setVisibility(View.VISIBLE);
            layout_qzout.setVisibility(View.VISIBLE);
            tv_title.setText("学员管理");
            String xybh = stusp.getString("xybh", "");//学员编号
            tv_studentcode.setText(xybh);
            int wcxs = stusp.getInt("wcxs", 0);//当前培训部分已完成学时
            if(wcxs<60) {
                tv_ywcxs.setText(wcxs + "分钟");
            }else{
                tv_ywcxs.setText(wcxs/60 + "小时"+wcxs%60+"分钟");
            }
            int zpxxs = stusp.getInt("zpxxs",0);//总培训学时
            if(zpxxs<60) {
                tv_zxs.setText(zpxxs + "分钟");
            }else{
                tv_zxs.setText(zpxxs/60 + "小时"+zpxxs%60+"分钟");
            }
            int zpxlc = stusp.getInt("zpxlc", 0);//总培训里程
            tv_zlc.setText((zpxlc/10.0)+"公里");
            int wclc = stusp.getInt("wclc", 0);//当前培训部分已完成里程
            tv_ywclc.setText((wclc/10.0)+"公里");
            String xzkc = stusp.getString("xzkc", "");//选择的课程
            tv_kechen.setText(xzkc);
            String xm = stusp.getString("xyxm", "");//姓名
            tv_stuname.setText(xm);
            String xyidcard = stusp.getString("xyidcard", "");//身份证
            tv_IDcard.setText(xyidcard);
            String xydltime = stusp.getString("xydltime", "");//登录时间
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmss");
                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                tv_logintime.setText(sdf2.format(sdf.parse(xydltime)));
            }catch(Exception e2){}
            String cx = stusp.getString("cx", "");//车型
            tv_cartype.setText(cx);
            String jrxs = stusp.getString("jrxs", "");//今日学时
            int jxs=Integer.valueOf(jrxs);
            if(jxs<60) {
                tv_todayxs.setText(jrxs + "分钟");
            }else if(jxs>240){
                tv_todayxs.setText("4小时0分钟");
            }else{
                tv_todayxs.setText(jxs/60 + "小时"+jxs%60+"分钟");
            }
            int fzpxjlsc = XsjlTimer.fzpxjlsc;
            if(fzpxjlsc<60) {
                tv_valid_time.setText(fzpxjlsc + "分钟");
            }else if(fzpxjlsc>240){
                tv_valid_time.setText("4小时0分钟");
            }else{
                tv_valid_time.setText(fzpxjlsc/60 + "小时"+fzpxjlsc%60+"分钟");
            }

        }else {//未登录
            layout_studentin.setVisibility(View.VISIBLE);
            layout_studentout.setVisibility(View.GONE);
            layout_qzout.setVisibility(View.GONE);
        }

        layout_back.setOnClickListener(this);
        bt_choose.setOnClickListener(this);
        bt_studentout.setOnClickListener(this);
        layout_qzout.setOnClickListener(this);
    }

    /**
     * 点击监听
     * */
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.layout_back://返回
                if(NettyConf.isback==true){
                    finish();
                }
                break;

            case R.id.bt_choose://课程选择
                Choosedialog();
                break;

            case R.id.bt_studentout://学员登出
                if(NettyConf.ispz==false){
                    loading = LoadingDialogUtils.createLoadingDialog(context,"学员登出中...");
                    studentOut();
                }else {
                    Toast.makeText(context,",正在拍照请稍后操作",Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.layout_qzout://强制登出
                showliuyanDialog();
                break;


            case R.id.bt_validtime://有效学时
                int shuiji = (int)(Math.random()*(9999-1000+1))+1000;
                String s = String.valueOf(shuiji);
                showyzDialog(s);
                break;
        }
    }

    /**
     * 课程选择完后跳转回来
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode){

            case REQUEST_A:

                switch (resultCode){
                    case ObjectContent1Activity.LOGIN_CONTENT_SUCCESS:
                        String pxnr = data.getStringExtra("pxnr");//培训内容
                        String objecttype = data.getStringExtra("objecttype");//培训第几部分
                        String kcbh = data.getStringExtra("kcbh");
                        if(objecttype.equals("2")){
                            tv_kechen.setText("第二部分——"+pxnr);
                            stuedit.putString("xzkc","第二部分——"+pxnr);
                            stuedit.commit();
                        }else if(objecttype.equals("3")){
                            tv_kechen.setText("第三部分——"+pxnr);
                            stuedit.putString("xzkc","第三部分——"+pxnr);
                            stuedit.commit();
                        }
                        SharedPreferences coachsp = getSharedPreferences("coach", Context.MODE_PRIVATE);
                        String cx = coachsp.getString("cx", "");
                        String jlcx = PxkcUtil.getValue(cx);
                        String pxck="1"+jlcx+objecttype+kcbh+"0000";//培训课程
                        NettyConf.pxkc=pxck;
                        //选择完成后改变课程选择图片
                        image_project.setBackgroundResource(R.mipmap.login_project_y);
                        //启动刷卡
                        Speaking.in("学员请刷卡");
                        isnfc=true;
                        break;
                }
                break;
        }
    }

    /**
     * 学员登录
     * */
    private void studentLogin() {
        try {
            if (ZdUtil.pdGps()) {
                String gnss = ZdUtil.getGnss();
                Xydl xydl = new Xydl();
                xydl.setXybh(NettyConf.xbh);//学员编号
                xydl.setJlbh(NettyConf.jbh);//教练编号

                String ktid = String.valueOf(new Date().getTime());
                ktid = ktid.substring(ktid.length() - 12, ktid.length() - 3);
                NettyConf.ktid = ktid;
                xydl.setKtid(ktid);
                xydl.setPxkc(NettyConf.pxkc);
                xydl.setGnss(gnss);
                byte[] xydlb3 = xydl.getXydlBytes();
                byte[] xydlb2 = MsgUtilClient.getMsgExtend(xydlb3, "0201", "13", "2");
                List<Tdata> list = MsgUtilClient.generateMsg(xydlb2, "0900", NettyConf.mobile, "1");

                if(ZdUtil.pdNetwork()&&NettyConf.constate==1&&NettyConf.jqstate==1) {
                    ForwardUtil.sendData(list, 0,1);
                }else{
                    NettyConf.sendState=false;//发送状态
                    if(NettyConf.debug){
                        Log.e("TAG","缓存学员登陆数据");
                    }
                    stuedit.putBoolean("sendState",NettyConf.sendState);
                    stuedit.commit();
                    DbHandle.insertTdatas(list,2);

                    Message msg=new Message();
                    XydlR xr=new XydlR();
                    xr.setJg(1);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("xydlr",xr);
                    msg.setData(bundle);
                    handleIn(msg);
                }
            } else {
                Log.e(TAG, "gps数据获取失败！");
                Toast.makeText(context, "gps数据获取失败", Toast.LENGTH_SHORT).show();
                NettyConf.isback=true;
            }
        }catch (Exception e){
            Log.e(TAG,"学员登陆数据异常:"+e.getMessage());
            Toast.makeText(context,"学员登陆数据异常",Toast.LENGTH_SHORT).show();
            NettyConf.isback=true;
        }
    }

    /**
     * 学员登出
     * */
    private void studentOut() {
        //验证指纹
        String sql="select * from tsfrz where tybh=? and lx=?";
        String[] params={NettyConf.xbh,"4"};
        ArrayList<SfrzR> list = DbHandle.queryTsfrz(sql,params);
        if(list.size()==0){
            studentOut1();
        }else{
            SfrzR xyxx=list.get(0);
            getXyxxout(xyxx);
        }

        //studentOut1();
    }

    /**
     * 登出拍照
     */
    private void studentOut1() {
        //loading = LoadingDialogUtils.createLoadingDialog(context,"正在登出...");
        loadingTimer = new LoadingTimer(loading);
        timer = new Timer();
        timer.schedule(loadingTimer,NettyConf.controltime);
        ZdUtil.studentOut1();
    }

    /**
     * 登出拍照
     */
    private void studentOut3() {
        //loading = LoadingDialogUtils.createLoadingDialog(context,"正在登出...");
        loadingTimer = new LoadingTimer(loading);
        timer = new Timer();
        timer.schedule(loadingTimer,NettyConf.controltime);
        //人脸识别
        try {

            ZdUtil.sendZpsc2("129", "0", "18",ZdUtil.getGnss4(),bdpic);

        }catch(Exception e){
            timer.cancel();
            Speaking.in("教练员登出数据异常");
            NettyConf.isback=true;
        }
    }

    private void studentOut2(){
        List<Tdata> list=ZdUtil.studentOut2();

        if(NettyConf.sendState&&NettyConf.constate==1&&NettyConf.jqstate==1){
            ForwardUtil.sendData(list, 1,6);
        }else{
            DbHandle.insertTdatas(list,6);
            //改变学员登出状态
            XydcR xr=new XydcR();
            xr.setJg(1);
            handleOut(xr);
        }

    }

    /**
     * 第几部分选择
     * */
    private void Choosedialog(){
        final String items[]={"第二部分","第三部分"};
        final String[] pxnr = {"2"};
        AlertDialog.Builder builder=new AlertDialog.Builder(context);  //先得到构造器
        builder.setTitle("部分选择"); //设置标题
        builder.setSingleChoiceItems(items,0,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //dialog.dismiss();
                if( items[which].equals("第二部分")){
                    pxnr[0] ="2";
                }else {
                    pxnr[0] ="3";
                }
            }
        });
        builder.setPositiveButton("确定",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
//                Toast.makeText(context, "确定"+pxnr[0], Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setClass(context, ObjectContent1Activity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("objecttype",pxnr[0]);
                startActivityForResult(intent,REQUEST_A);
            }
        });
        builder.create().show();
    }

    /**
     * 登录处理
     * */
    public synchronized void handleIn(Message msg){
        //取消加载动画
        if(loadingTimer!=null) {
            loadingTimer.cancel();
        }
        if(timer!=null) {
            timer.cancel();
        }

        if(NettyConf.xystate!=1) {

            Bundle data = msg.getData();
            XydlR xydlr = (XydlR) data.getSerializable("xydlr");//学员登录成功后返回来的数据
            if (xydlr.getJg() == 1) {//学员登录成功
                //保存登陆信息
                DbHandle.insertTsfrz(xyxx);

                String jrxs = "0";
                if (StringUtils.isNotEmpty(xydlr.getFjxx())) {
                    jrxs = xydlr.getFjxx().split(",")[1];
                }


                NettyConf.xystate = 1;
                CountDistance.setTotalMile(0);
                //把总里程存储起来
                stuedit.putFloat("zlc",0);
                stuedit.commit();

                NettyConf.xydltime = ZdUtil.getTime2();
                XsjlTimer.fzpxjlsc=0;

                //发送学员登陆的广播
                Intent xydlIntent = new Intent();
                xydlIntent.setAction("xydl");
                sendBroadcast(xydlIntent);
                //上传拍照数据
                //人脸识别上传对比成功的照片
                ZdUtil.sendZpsc2("129","0","17",ZdUtil.getGnss4(),bdpic);

                stuedit.putString("xybh", NettyConf.xbh);//学员编号
                if (xydlr.getWcxs() != 0) {
                    stuedit.putInt("wcxs", xydlr.getWcxs());//当前培训部分已完成学时
                }
                if (xydlr.getZpxxs() != 0) {
                    stuedit.putInt("zpxxs", xydlr.getZpxxs());//总培训学时
                }
                if (xydlr.getZpxlc() != 0) {
                    stuedit.putInt("zpxlc", xydlr.getZpxlc());//总培训里程
                }
                if (xydlr.getWclc() != 0) {
                    stuedit.putInt("wclc", xydlr.getWclc());//当前培训部分已完成里程
                }
                stuedit.putInt("xystate", 1);
                stuedit.putString("ktid", NettyConf.ktid);
                stuedit.putString("xydltime", NettyConf.xydltime);//学员登录时间
                stuedit.putString("xyxm", xyxx.getXm());//姓名
                stuedit.putString("xyidcard", xyxx.getSfzh());//身份证号
                stuedit.putString("jrxs", jrxs);//今日学时
                stuedit.putString("cx", xyxx.getCx());//车型
                stuedit.putInt("fzpxjlsc",0);
                stuedit.commit();

                //今日学时监控
                if (StringUtils.isNotEmpty(jrxs)) {
                    NettyConf.jrxxsc = Integer.valueOf(jrxs);
                } else {
                    NettyConf.jrxxsc = 0;
                }

                //返回信息
                Intent intent = new Intent();
                setResult(LOGIN_STU_SUCCESS, intent);

                LoadingDialogUtils.closeDialog(loading);

                Speaking.in("学员登录成功");

                finish();
            } else {
                //失败则删除认证的缓存信息
                String[] params = {xyxx.getUuid(), String.valueOf(xyxx.getLx())};
                DbHandle.deleteData(DbConstants.T_SFRZ, "uuid=? and lx=?", params);

                LoadingDialogUtils.closeDialog(loading);
                if(StringUtils.isNotEmpty(xydlr.getFjxx())) {
                    Speaking.in(xydlr.getFjxx().split(",")[0]);
                }
                finish();
                NettyConf.isback=true;
            }
        }
    }

    /**
     * 登出处理
     * */
    public void handleOut(XydcR xydcr){
        //取消加载动画
        if(loadingTimer!=null){
            loadingTimer.cancel();
        }
        if(timer!=null) {
            timer.cancel();
        }

        if(xydcr.getJg()==1){//学员登出成功
            //发出学员登出广播
            Intent xydcIntent=new Intent();
            xydcIntent.setAction("xydc");
            sendBroadcast(xydcIntent);

            ZdUtil.handleStudentOut();
            //返回登录页面
            Intent intent = new Intent();
            setResult(LOGIN_STU_SUCCESS,intent);
            LoadingDialogUtils.closeDialog(loading);
            finish();
            NettyConf.isback=true;
        }else {
            //登出失败
            LoadingDialogUtils.closeDialog(loading);
            Speaking.in("学员登出失败");
            NettyConf.isback=true;
        }
    }

    /**
     * 读卡成功后获取学员信息
     * */
    public void getXyxx(SfrzR xyxx){
        NettyConf.xbh=xyxx.getTybh();
        //获取信息成功后显示身份信息
        layout_shenfen.setVisibility(View.VISIBLE);
        tv_bianhao.setText(xyxx.getTybh());
        tv_idcard.setText(xyxx.getSfzh());
        tv_stu_name.setText(xyxx.getXm());
        tv_carlx.setText(xyxx.getCx());
        commonXy2(xyxx,"stulogin");

    }

    /**
     * 学员登出指纹验证
     */
    public void getXyxxout(SfrzR xyxx){
        //人脸识别
        if(NettyConf.ispz==false){

            commonXy2(xyxx,"stuout");
        }else {
            Toast.makeText(context,"正在拍照，请稍后...",Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * 人脸识别通道
     * type 分为login和out
     * */
    public void commonXy2(final SfrzR xyxx,final String type){
        NettyConf.isback=false;
        final String zp = xyxx.getZp();//下载路径
        Log.e("TAG","学员下载图片路径："+zp);
        if(zp==null||zp.equals("")){
            Toast.makeText(context,"没有照片下载有效路径",Toast.LENGTH_SHORT).show();
            NettyConf.isback=true;
            return;
        }
        final String sfzh = xyxx.getSfzh();
        //判断文件夹是否存在
        RlsbUtil.isexistAndBuild(NettyConf.jlyxy_picurl);
        //学员原始照片路径
        final String xyzp=NettyConf.jlyxy_picurl+sfzh+".jpg";

        if(RlsbUtil.isFileExist(xyzp)==false){
            String zp1=new String(ByteUtil.hexStringToByte(zp));
            Log.e("TAG","学员下载图片路径："+zp1);
            //没有学员照片去下载
            downFile(zp1,sfzh,xyzp,type);
        }else {
            //有学员照片直接抓拍验证
            rlsb(xyzp,sfzh, type);
        }

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
                    loading = LoadingDialogUtils.createLoadingDialog(context, "正在登出...");
                    ZdUtil.matchPassword(4,yzmm);
                    builder.dismiss();
                }else {
                    Toast.makeText(context,"请输入登出密码",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    /**
     * 验证查看有效学时dialog
     *
     *
     * @param shuiji*/

    private void showyzDialog(final String shuiji){
        final AlertDialog builder = new AlertDialog.Builder(this,R.style.CustomDialog).create(); // 先得到构造器
        builder.show();
        builder.getWindow().setContentView(R.layout.dialog_shuiji_edt);
        builder.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);//解决不能弹出键盘
        LayoutInflater factory = LayoutInflater.from(this);
        View view = factory.inflate(R.layout.dialog_shuiji_edt, null);
        builder.getWindow().setContentView(view);
        final EditText edt_content = (EditText) view.findViewById(R.id.edt_content);
        TextView tv_yzm = (TextView) view.findViewById(R.id.tv_yzm);//验证码
        Button bt_cacnel = (Button) view.findViewById(R.id.bt_cacnel);
        Button bt_sure = (Button) view.findViewById(R.id.bt_sure);
        tv_yzm.setText(shuiji);

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
                String yzm = edt_content.getText().toString().trim();
                if(yzm.equals(shuiji)){
                    Intent intent = new Intent();
                    intent.setClass(context,ValidTimeActivity.class);
                    startActivity(intent);
                    builder.dismiss();
                }else {
                    Toast.makeText(context,"输入不正确",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }



    /**
     * 下载文件
     * */
    public void downFile(String url, final String sfzh, final String xyzp, final String type){
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
                    if (refernece == myDwonloadID) {
                        Log.e("TAG","下载学员照片成功");
                        //下载完成操作，保存原照片 身份证号用来区别

                        if(RlsbUtil.isFileExist(xyzp)==true){
                            //照片存在
                            stuedit.putString("stuphoto",xyzp);
                            stuedit.commit();
                            rlsb(xyzp, sfzh,type);

                        }else {
                            //照片不错在
                            Log.e("TAG","学员照片下载失败1");
                            Speaking.in("学员照片下载失败");
                        }

                    }else {
                        NettyConf.isback=true;
                        Log.e("TAG","下载学员照片失败2");
                        Speaking.in("学员照片下载失败");
                    }
                }
            };
            registerReceiver(receiver, filter);
        }catch (Exception ex){
            NettyConf.isback=true;
            Log.e("TAG","下载学员照片失败3");
            Speaking.in("学员照片下载失败");
        }

    }

    /**
     * 人脸识别成功后处理教练登录或教练登出
     * ishave_pic 判断是否有教练照片，有则无需保存特征值，无则保存特征值 true有照片，false没照片
     * */

    public void rlsb(final String xyzp, final String sfzh, final String type ){
        Speaking.in("正在人脸识别，请对准摄像头");
        //只有当原始照片保存成功才进行人脸识别
        Test.floatTakePicture(getWindow().getDecorView(),sfzh,type,"loginstudent");

//        if(isCameraL==true){
//            pztimer = new Timer();
//            pztask=new TimerTask() {
//                @Override
//                public void run() {
//                    if(isfinishphoto<20){
//                        isfinishphoto++;
//                        String path = captureSnapshot();
//                        boolean stopcamera=compare(path,sfzh);
//                        if(stopcamera==true){
//                            //关闭摄像头
//                            pztimer.cancel();
//                            closeCamera();
//                            ZdUtil.ispz=false;
//                            final Timer rlcltimer = new Timer();
//                            TimerTask task2=new TimerTask() {
//                                @Override
//                                public void run() {
//                                    rlcltimer.cancel();
//                                    //识别正确人脸
//                                    if(type.equals("login")){
//                                        //登录处理
//                                        stuedit.putString("stuphoto",xyzp);
//                                        stuedit.commit();
//                                        studentLogin();
//                                    }else if(type.equals("out")){
//                                        //登出处理
//                                        studentOut3();
//                                    }
//                                }
//                            };
//                            rlcltimer.schedule(task2,300);
//                        }
//                    }else {
//                        //超过60秒自动关闭页面
//                        pztimer.cancel();
//                        closeCamera();
//                        ZdUtil.ispz=false;
//                        finish();
//                    }
//
//                }
//            };
//            pztimer.schedule(pztask,200,3000);
//            RlsbUtil.addtimer(pztimer);

//        }else {
//            //保存原始图片失败
//            Speaking.in("人脸识别失败");
//        }

    }


    /**
     * 返回键监听
     * */
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(NettyConf.isback==true){
                finish();
            }
            return false;
        }else {
            return super.onKeyDown(keyCode, event);
        }
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
            if(NettyConf.xystate==0&&isnfc==true){
                Message msg = new Message();
                msg.arg1=6;
                Bundle bundle = new Bundle();
                bundle.putString("xyuid",cardNo);
                msg.setData(bundle);
                handler.sendMessage(msg);

            }
        }
    }


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
        NettyConf.isback=true;

        NettyConf.handlersmap.remove("loginstudent");
    }


}
