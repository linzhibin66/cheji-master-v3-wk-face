package com.dgcheshang.cheji.Activity.Lukao;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dgcheshang.cheji.Activity.BaseInitActivity;
import com.dgcheshang.cheji.R;
import com.dgcheshang.cheji.Tools.IsMediaPlayer;

import java.io.IOException;

/**
 * 模拟路考详情
 * */

public class LukaoListDetailActivity extends BaseInitActivity implements View.OnClickListener{
    Context context=LukaoListDetailActivity.this;
    ImageView image_play;
    TextView tv_play_state,tv_title,tv_zhinan,tv_koufen;
    MediaPlayer mp;
    int isplay=0;//是否播放；0：未播放，1：播放中
       String text13="1.绕车一周检查车辆情况。\n2.注意贯彻车后方交通情况，在安全的情况下打开车门上车。\n3.上车后关好车门，调整座椅，调整后视镜，检查仪表盘，系好安全带。";
    String text14="1.确认操作杆在空挡的位置点火。（启动后，及时松开启动开关）\n2.踩离合。\n3.挂一档。\n4.打开左转向灯（等待3秒才能转向）。\n5.左右观察后方交通情况。\n6.慢抬离合器踏板，同时轻踩加速踏板。\n7.鸣笛，松手刹。\n注意：向前看，不能低头看；确保车辆起步平稳，不前冲，不抖动。（夜间应打开前照灯，在有路灯的情况下，禁止使用远光灯）。";
    String text15="1.打转向灯（等待3秒再转向）\n2.观察左后方交通状态，确认安全的情况下变更车道。\n注意：变更车道后，考生应主动寻找时机变回原来的车道；为确保安全，考试车只有在变更车道，超车，掉头时能占用左边车道，请他项目原则上应该在右车道完成。";
    String text16="1.直行行驶可增档加速，但不能跳档加速。\n2.不能用一档。\n3.车速保持在30Km/h左右。\n4.镇定心细，方向稳定，不左右摇摆。\n注意：如遇交通堵塞，需减速（不动方向），即使走便道都不要动方向，可减速甚至停下，鸣笛示意。";
    String text17="1.刹车减速（明显的刹点车），左右观察。\n2.避让公交车，确认安全后通过。";
    String text18="1.刹车减速（明显刹点车），左右观察。\n2.避让学生，确认安全后通过。\n注意：遇行人通过人行横道，需停车让行。";
    String text19="1.刹车减速（明显刹点车），左右观察。\n2.按路口信号灯指示行驶。\n3.若路口指示左转或者右转，应该打左或右方向灯，并等待3秒后转向。\n4.因交通堵塞无法行驶，将车停在安全线以外等待，路口内禁止停车。\n5.注意行人，慢车让行或停车让行。\n重点：减速礼让，禁鸣笛，掌握好刹车力度，注意观察，路口直行要在直行道行驶，记得提前变更车道（通过路口在30Km/h）以下。";
    String text20="1.刹车减速（明显刹点车），左右观察。\n2.确认安全后通过。\n注意：减速礼让，禁鸣笛，遇行人通过人行横道，需停车让行。";
    String text21="1.不得加速抢道，不得抢占车道，不能突然紧急刹车。\n注意：会车尽量保持横向距离1M，夜间会车远近灯光交替闪烁两次。";
    String text22="1.打左转向灯（保持3秒后转向），通过后视镜观察左后方。\n2.确认安全后方可转动方向盘，驶入左侧车道进行超车。\n3.确认与被超车辆保持安全距离后，打右转向灯（保持3秒）。\n4.观察右后方，确认安全后驶入原车道行驶。\n注意：当不具备超车条件时，可耐心等待，禁止右侧超车。";
    String text23="1.注意避让前后来往车辆，不影响其他车辆行驶。\n2.打左转向灯（保持3秒），左右观察。\n3.刹车减速，适当情况下降档。\n4.掉头（打方向转弯中，应停车观察）。";
    String text24="1.打开右转向灯（保持3秒后再转向），观察右后方。\n2平行停靠在距路边小于30cm的地方。\n3.拉手刹，置为空挡，松开制动踏板和离合。\n4.熄火，观察左后方，确认安全后下车。\n注意：双黄线是禁止停车区域，白虚线允许停车。（夜间考试还应该关闭前照灯，开启示宽灯和警示灯）";
    String text25="1.当听到“前方路口左转弯”口令时，迅速打开左转向灯。\n2.观察后方车辆情况，确保安全、开灯三秒后变道至左转弯车道上（考试车道为最里道）。\n3.再次打开左转向灯（变道后左转向灯会自动复位）。\n4.左右观察车外情况，低速转大弯通过。\n注意：当左转弯遇到红灯时，踩刹车，减速。换低速档（来不及时可不换档），即将停车时，踩离合，踩刹车，平稳停车，等待红灯。红灯倒记时8秒时，开始做半坡起步准备（打左转向灯，踩离合，挂一档，松手刹，），平稳起步。马上加速换二档，左右观察车外情况，转大弯通过。转弯结束，进入直道后，尽快加速换三档。";
    String text26="1.当听到“前方路口右转弯”口令时，迅速打开左转向灯。\n2.观察后方车辆情况，确保安全、开灯三秒后变道至右转弯车道上（考试车道为最里道）。\n3.再次打开右转向灯（变道后左转向灯会自动复位）。\n4.左右观察车外情况，低速转大弯通过。\n注意：当左转弯遇到红灯时，踩刹车，减速。换低速档（来不及时可不换档），即将停车时，踩离合，踩刹车，平稳停车，等待红灯。红灯倒记时8秒时，开始做半坡起步准备（打左转向灯，踩离合，挂一档，松手刹，），平稳起步。马上加速换二档，左右观察车外情况，转大弯通过。转弯结束，进入直道后，尽快加速换三档。";
    String text27="1.听到语音提示“减速让行”时，踩刹车，将车速降低至30km/h，遇有行人通过，将车辆停在安全线以外，确认安全后再通过。";
    String text28="1.当通过学校区域时，看到禁止鸣笛路标时，不能鸣笛提示。";
    String text29="1.打开右转向灯（保持3秒后再转向），观察右后方。\n2.平行停靠在距路边小于30cm的地方。\n3.拉手刹，置为空挡，松开制动踏板和离合。\n4.熄火，观察左后方，确认安全后下车。\n注意：双黄线是禁止停车区域，白虚线允许停车。（夜间考试还应该关闭前照灯，开启示宽灯和警示灯）";
    String text30="1.打开右转向灯（保持3秒后再转向），观察右后方。\n2.平行停靠在距路边小于30cm的地方。\n3.拉手刹，置为空挡，松开制动踏板和离合。\n4.熄火，观察左后方，确认安全后下车。\n注意：双黄线是禁止停车区域，白虚线允许停车。（夜间考试还应该关闭前照灯，开启示宽灯和警示灯）";
    String text31="1.听到语音提示“请完成加减档操作”，踩油门[转速1500转(15迈)时]→踩离合、松油门→换二档→松离合、踩油门[当转速1500或25迈时]→踩离合、松油门→换三档→快松离合(不踩油门)→[无需加油]踩离合→换四档→快松离合、踩离合→换三档→松离合→[把速度降到20迈]踩离合→换2档→慢松离合。\n注意：一般来说，20--30km/h挂二挡，30--40km/h挂三挡，40--50km/h挂四挡，50km/h以上挂五档。\n2.踩离合(器)，松油门：踩离合和松油门应同时(或几乎同时)进行，就算要排个先后次序，也应是踩离合在先，松油门在后。\n3.换挡：不可越级。怠速换档操作时切忌用力过猛，要仔细分清档位方向，以免挂错档位。\n4.抬离合、加油：要确保发动机转速和离合片转速同步。";
    String text32="1.通过指定地点完成本次模拟考试。\n2.拉手刹，置为空挡，松开制动踏板和离合。\n3.熄火，观察左后方，确认安全后下车。";

    String koufen13="以下情况不合格（-100分）\n1.不绕车一周检查车辆外观及安全情况。\n2.打开车门不观察后方交通情况。\n3.上车后不关闭车门。\n4.不系好安全带。\n以下情况扣除5分：\n1.不调整后视镜。\n2.不调整好座椅。";
    String koufen14="以下情况不合格：\n1.车门未关闭起步。\n2.起步前，未通过后视镜并向左方侧头\n3.起步时，车辆后溜距离大于30厘米。\n以下情况扣除20分：\n1.起步前不使用转向灯。\n2.起步时车辆后溜，但后溜距离小于30厘米。\n以下情况扣10分：\n1.启动发动机时，操作杆未置空挡。（或者P挡）\n2.发动机启动后，不及时松开启动开关。\n3.起步前，开转向灯小于3秒即转向。\n4.起步挂错档，不能及时纠正。\n5.不松驻车制动器起步。\n6.起步车辆发生闯动。\n7.因不正确操作导致车辆熄火。\n以下情况扣5分：\n1.启动发动机前，不调整座椅，后视镜，检查仪表盘。\n2.起步时，加速踏板控制不当，导致发动机转速过高。";
    String koufen15="以下情况不合格：\n1.变更车道前，不通过内外后视镜观察后方道路交通情况。\n2.变更车道时，判断车辆安全距离不合理，妨碍其他车辆行驶。\n3.连续变更两车道。\n4.在规定的时间与路程内，未按照语音指令变更车道。\n以下情况扣20分：\n1.变更车道不使用转向灯。\n以下情况扣除10分：\n1.变更车道前，开转向灯小于3秒即转向。";
    String koufen16="以下情况不合格：\n1.方向控制不稳，不能保持车辆直线行驶。\n2.遇到前车制动，不采取减速措施。\n以下情况扣除10分：\n1.超过20秒不通过后视镜观察后方交通情况。\n2.不了解车辆行驶速度。\n3.未及时发现路面障碍物，未及时采取减速措施。";
    String koufen17="以下情况不合格：\n1.不观察左右方交通情况。\n2.不按照规定减速。";
    String koufen18="以下情况不合格：\n1.不观察左右方交通情况。\n2.不按照规定减速。\n3.遇行人通过人行横道不停车礼让。";
    String koufen19="以下情况不合格：\n1.通过路口不减速慢行。\n2.路口直行不观察左右方交通情况。\n3.转弯通过路口，未观察侧前方交通情况或未通过内外后视镜观察侧、后方交通情况。\n4.遇有路口交通堵塞时进入路口，将车辆停留在路口内等候。\n5.不按规定避让行人和优先通过的车辆。\n6.左转通过路口时，未靠路口中心点左侧转弯。";
    String koufen20="以下情况不合格：\n1.不按规定减速慢行。\n2.不观察左右方交通情况。\n3.遇行人通过人行横道不停车礼让。";
    String koufen21="以下情况不合格：\n1.在没有中心距离设施或者中心线的道路会车，不减速靠右行驶，并注意与其他车辆或行人保持安全距离。\n2.会车困难时不让行。\n3.横向安全距离判断差，紧急转向避让相对方向来车。\n4.在规定的时间，路程内，未按语音指令完成会车。";
    String koufen22="以下情况不合格：\n1.超车前不通过内外后视镜观察后方和左侧的交通状况。\n2.超车时机选择不正确，影响其他车辆行驶。\n3.超车时位于被超车辆保持安全距离。\n4超车后，急转向，驶回原车道，妨碍被超车辆行驶。\n5.从右侧超车。\n以下情况扣除20分：\n1.超车时，开转向灯时间小于3秒即转向。\n2.当后车发出超车信号时，具备让车条件不减速靠右让行。";
    String koufen23="以下情况不合格：\n1.不能正确观察交通情况选择掉头时机。\n2.掉头地点选择不正确。（车辆所占车道不正确）。\n3.掉头时，妨碍正常行驶的其他车辆和行人通过。（如转弯停车时车头出线，人行横道掉头）。\n4.在规定的时间，路程内，未按照语音提示掉头。\n以下情况扣除20分：\n1.车辆掉头不使用转向灯。\n以下情况扣除10分：\n1.车辆掉头前，开转向灯小于3秒即转向。";
    String koufen24="以下情况不合格：\n1.停车时，不通过内外后视镜观察右后方交通情况。\n2.停车后，车身超过道路右侧边缘线或者人行道边缘。\n3.下车打开车门时，不侧头观察后方交通情况。\n以下情况扣除20分：\n1.停车后，距边缘距离超过30cm。\n2.停车后，未拉紧驻车制动器。\n以下情况扣除10分：\n1.下车不关闭车门。\n2.拉驻车制动器前松开制动踏板。\n以下情况扣除5分：\n1.下车前不将发动机熄火。\n2.夜间在路边临时停车不关闭前照灯或警示灯。";
    String koufen25="以下情况不合格：\n1.不按规定减速或停车瞭望的，不合格。\n2.不观察左、右方交通情况，转弯通过路口时，未观察侧前方交通情况的，不合格。\n3.遇有路口交通阻塞时进入路口，将车辆停在路口内等候的，不合格。\n以下情况扣除10分：\n1.左转通过路口时，未靠路口中心点左侧转弯的。";
    String koufen26="以下情况不合格：\n1.不按规定减速或停车瞭望的，不合格。\n2.不观察左、右方交通情况，转弯通过路口时，未观察侧前方交通情况的，不合格。\n3.遇有路口交通阻塞时进入路口，将车辆停在路口内等候的，不合格。\n以下情况扣除10分：\n1.左转通过路口时，未靠路口中心点左侧转弯的。";
    String koufen27="以下情况不合格：\n1.不按规定减速慢行的，不合格。\n2.不观察左、右方交通情况的，不合格。\n2.未停车礼让行人的，不合格";
    String koufen28="以下情况扣除10分：\n1.不能根据交通情况合理使用喇叭的。";
    String koufen29="以下情况不合格：\n1.通过急弯、破路、拱桥、人行横道或者没有交通信号灯控制的路口时，不交替使用远、近光灯示意的，不合格。";
    String koufen30="以下情况不合格：\n1.通过急弯、破路、拱桥、人行横道或者没有交通信号灯控制的路口时，不交替使用远、近光灯示意的，不合格。";
    String koufen31="以下情况不合格：\n1.未按指令平稳加、减挡。\n以下情况扣除10分：\n1.车辆运行速度和挡位不匹配。";
    String koufen32="以下情况不合格：\n1.未能通过指定地点完成本次模拟考试。\n2.停车，但未熄火，关闭所有车窗。";

    String[] zhinan=new String[]{text13,text14,text15,text16,text17,text18,text19,text20,text21,text22,text23,text24,text25,text26,text27,text28,text29,text30,text31,text32};
    String[] koufen=new String[]{koufen13,koufen14,koufen15,koufen16,koufen17,koufen18,koufen19,koufen20,koufen21,koufen22,koufen23,koufen24,koufen25,koufen26,koufen27,koufen28,koufen29,koufen30,koufen31,koufen32};

    int[] richanglist={R.raw.lukao13,R.raw.lukao14,R.raw.lukao15,R.raw.lukao16,R.raw.lukao17,R.raw.lukao18,R.raw.lukao19,R.raw.lukao20,R.raw.lukao21,R.raw.lukao22,R.raw.lukao23,R.raw.lukao24,R.raw.lukao25,R.raw.lukao26,R.raw.lukao27,R.raw.lukao28,R.raw.lukao29,R.raw.lukao30,R.raw.lukao31,R.raw.lukao32};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lukao_list_detail);
        initView();
    }

    private void initView() {
        Bundle extras = getIntent().getExtras();
        int position = extras.getInt("position");
        String who = extras.getString("who");
        String title = extras.getString("title");
        image_play = (ImageView) findViewById(R.id.image_play);//播放声音
        tv_play_state = (TextView) findViewById(R.id.tv_play_state);//状态
        tv_title = (TextView) findViewById(R.id.tv_title);//标题
        tv_zhinan = (TextView) findViewById(R.id.tv_zhinan);//操作指南
        tv_koufen = (TextView) findViewById(R.id.tv_koufen);//扣分标准
        View layout_back = (View) findViewById(R.id.layout_back);//返回
        IsMediaPlayer.isRelease();
//        String url="/mnt/sdcard/chejidoal/lukao"+(position+1)+ ".ogg";//声音播放路径
        int i1 = richanglist[position];
        Uri url = Uri.parse("android.resource://com.dgcheshang.cheji/"+i1);
        mp=new MediaPlayer();
        try {
            mp.setDataSource(context,url);
            mp.prepare();
            mp.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
            tv_zhinan.setText(zhinan[position]);
            tv_koufen.setText(koufen[position]);

        tv_title.setText(title);
        isplay=1;
        image_play.setBackgroundResource(R.mipmap.play_no);
        tv_play_state.setText("暂停播放");
        //播放完成
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.pause();
                image_play.setBackgroundResource(R.mipmap.play);
                tv_play_state.setText("开始播放");
                isplay=0;
            }
        });
        image_play.setOnClickListener(this);
        layout_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.image_play://播放
                if(isplay==0){
                    image_play.setBackgroundResource(R.mipmap.play_no);
                    tv_play_state.setText("暂停播放");
                    mp.start();
                    isplay=1;
                }else if(isplay==1){
                    image_play.setBackgroundResource(R.mipmap.play);
                    tv_play_state.setText("开始播放");
                    mp.pause();
                    isplay=0;
                }

                break;
            case R.id.layout_back://返回
                finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        if(mp!=null){
            mp.stop();
            mp = null;
        }
        super.onDestroy();
    }
}
