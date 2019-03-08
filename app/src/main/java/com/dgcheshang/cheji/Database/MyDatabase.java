package com.dgcheshang.cheji.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.dgcheshang.cheji.netty.conf.NettyConf;

/**
 * sqlite数据库
 */
public class MyDatabase extends SQLiteOpenHelper {

    public MyDatabase(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public MyDatabase(Context context) {
        super(context, DbConstants.DB_NAME, null, DbConstants.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建学时记录数据
        db.execSQL( "CREATE TABLE IF NOT EXISTS "+
                DbConstants.T_XSJL+ "("+
                "xid integer primary key autoincrement ,"+//主键
                "xsjlbh varchar(26),"+//学时记录编号
                "xybh varchar(16),"+//学员编号
                "jlbh varchar(16),"+//教练编号
                "ktid varchar(20),"+//课堂ID
                "jlcssj varchar(6),"+//记录长生时间
                "pxkc varchar(30),"+//培训课程
                "jlzt varchar(2),"+//记录状态
                "zdsd varchar(10),"+//最大速度
                "xclc varchar(10),"+//历程
                "gnss varchar(200),"+
                "sj integer"+
                ")"
        );
        //创建照片上传初始化数据
        db.execSQL( "CREATE TABLE IF NOT EXISTS "+
                DbConstants.T_ZPSC+ "("+
                "did integer primary key autoincrement,"+//多媒体数据主键
                "zpbh varchar(10),"+//照片编号
                "bh varchar(16),"+//编号
                "tdh varchar(5),"+//通道号
                "tpcc varchar(5),"+//图片尺寸
                "sjlx varchar(5),"+//时间类型
                "zbs varchar(5),"+//总包数
                "sjcd varchar(10),"+//照片数据长度
                "ktid varchar(20),"+//课堂id
                "gnss varchar(200),"+//定位数据
                "rlsb varchar(5),"+//人脸识别
                "sj integer"+//时间
                ")"
        );

        //创建注册成功后存值
        db.execSQL( "CREATE TABLE IF NOT EXISTS "+
                DbConstants.T_ZPDATA+ "("+
                "zid integer primary key autoincrement,"+//多媒体数据主键
                "zpbh varchar(10),"+//照片编号
                "zpsj blob,"+//照片数据
                "sj integer"+//时间
                ")"
        );

        //创建注册成功后存值
        db.execSQL( "CREATE TABLE IF NOT EXISTS "+
                DbConstants.T_DATA+ "("+
                "id integer primary key autoincrement,"+//多媒体数据主键
                "key varchar(20),"+//流水号
                "parentid varchar(20),"+//对应的主流水号，可以为空
                "data text,"+//缓存数据
                "level integer,"+//缓存数据
                "initsj integer,"+//最初保存时间
                "sj integer"+//时间
                ")"
        );

        //创建注册成功后存值
        db.execSQL( "CREATE TABLE IF NOT EXISTS "+
                DbConstants.T_DATAF+ "("+
                "id integer primary key autoincrement,"+//多媒体数据主键
                "key varchar(20),"+//流水号
                "parentid varchar(20),"+//对应的主流水号，可以为空
                "data text,"+//缓存数据
                "level integer,"+//缓存数据
                "initsj integer,"+//最初保存时间
                "sj integer"+//时间
                ")"
        );

        //创建注册成功后存值
        db.execSQL( "CREATE TABLE IF NOT EXISTS "+
                DbConstants.T_SFRZ+ "("+
                "id integer primary key autoincrement,"+//多媒体数据主键
                "uuid varchar(10),"+//对应内码
                "lx varchar(2),"+//人员类型
                "tybh varchar(32),"+//统一编号
                "sfzh varchar(32),"+//身份证号
                "cx varchar(4),"+//车型
                "xx text,"+//身份信息
                "xm varchar(32),"+//身份信息
                "sj integer,"+//时间
                "zp text"+//照片
                ")"
        );

        //创建注册成功后存值
        db.execSQL( "CREATE TABLE IF NOT EXISTS "+
                DbConstants.T_DZWL+ "("+
                "id integer primary key autoincrement,"+//多媒体数据主键
                "cdbh varchar(20),"+//场地编号
                "cdmc varchar(50),"+//场地名称
                "cddz varchar(100),"+//场地地址
                "pxcx varchar(50),"+//培训车型
                "cdlx varchar(4),"+//场地类型
                "zbd text,"+//坐标点
                "sj integer"+//时间
                ")"
        );

        //创建注册成功后存值
        db.execSQL( "CREATE TABLE IF NOT EXISTS "+
                DbConstants.T_LINE+ "("+
                "id integer primary key autoincrement,"+//多媒体数据主键
                "mc varchar(50),"+//路线名称
                "xlzb text,"+//线路坐标 形式:（序号,类型,纬度,经度;序号,类型,纬度,经度;...）
                "sj integer"+//时间
                ")"
        );
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(NettyConf.debug) {
            Log.e("TAG", "老版本：" + oldVersion + ";新版本：" + newVersion);
        }
            //创建注册成功后存值
            db.execSQL( "CREATE TABLE IF NOT EXISTS "+
                    DbConstants.T_DZWL+ "("+
                    "id integer primary key autoincrement,"+//多媒体数据主键
                    "cdbh varchar(20),"+//场地编号
                    "cdmc varchar(50),"+//场地名称
                    "cddz varchar(100),"+//场地地址
                    "pxcx varchar(50),"+//培训车型
                    "cdlx varchar(4),"+//场地类型
                    "zbd text,"+//坐标点
                    "sj integer"+//时间
                    ")"
            );

            //创建注册成功后存值
            db.execSQL( "CREATE TABLE IF NOT EXISTS "+
                    DbConstants.T_LINE+ "("+
                    "id integer primary key autoincrement,"+//多媒体数据主键
                    "mc varchar(50),"+//路线名称
                    "xlzb text,"+//线路坐标 形式:（序号,类型,纬度,经度;序号,类型,纬度,经度;...）
                    "sj integer"+//时间
                    ")"
            );

    }
}
