package com.dgcheshang.cheji.Database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.dgcheshang.cheji.CjApplication;
import com.dgcheshang.cheji.netty.conf.NettyConf;
import com.dgcheshang.cheji.netty.po.Dzwl;
import com.dgcheshang.cheji.netty.po.Line;
import com.dgcheshang.cheji.netty.po.Tdata;
import com.dgcheshang.cheji.netty.po.Xsjl;
import com.dgcheshang.cheji.netty.po.Zpdata;
import com.dgcheshang.cheji.netty.po.Zpsc;
import com.dgcheshang.cheji.netty.serverreply.SfrzR;
import com.dgcheshang.cheji.netty.util.ZdUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * SQLite业务处理
 */

public class DbHandle {
    private static DbManager dbManager=DbManager.getInstance(CjApplication.getInstance());

    /**
     * 增加线路
     * */
    public static  void insertLine(Line line){
        try {
            ContentValues cv = new ContentValues();
            cv.put("mc", line.getMc());
            cv.put("xlzb", line.getXlzb());
            cv.put("sj", ZdUtil.getLongTime());
            long l = dbManager.getReadableDatabase().insert(DbConstants.T_LINE, null, cv);
            if(NettyConf.debug) {
                Log.e("TAG", "线路存储结果" + l);
            }
        }catch (Exception e){
            if(NettyConf.debug){
                Log.e("TAG",e.getMessage());
            }
        }finally {
            dbManager.closeDatabase();
        }
    }


    /**
     * 增
     * */
    public static  void insertXsjl(Xsjl xsjl){
        try {
            ContentValues cv = new ContentValues();
            cv.put("xsjlbh", xsjl.getXsjlbh());
            cv.put("xybh", xsjl.getXybh());
            cv.put("jlbh", xsjl.getJlbh());
            cv.put("ktid", xsjl.getKtid());
            cv.put("jlcssj", xsjl.getJlcssj());
            cv.put("pxkc", xsjl.getPxkc());
            cv.put("jlzt", xsjl.getJlzt());
            cv.put("zdsd", xsjl.getZdsd());
            cv.put("xclc", xsjl.getXclc());
            cv.put("gnss", xsjl.getGnss());
            cv.put("sj", ZdUtil.getLongTime());
            long l = dbManager.getReadableDatabase().insert(DbConstants.T_XSJL, null, cv);
            if(NettyConf.debug) {
                Log.e("TAG", "学时记录存储结果" + l);
            }
        }catch (Exception e){
            if(NettyConf.debug){
                Log.e("TAG",e.getMessage());
            }
        }finally {
            dbManager.closeDatabase();
        }
    }

    /**
     * 增
     * */
    public static  void insertZpsc(Zpsc zpsc){
        try {
            ContentValues cv=new ContentValues();
            cv.put("zpbh",zpsc.getZpbh());
            cv.put("bh",zpsc.getBh());
            cv.put("tdh",zpsc.getTdh());
            cv.put("tpcc",zpsc.getTpcc());
            cv.put("sjlx",zpsc.getSjlx());
            cv.put("zbs",zpsc.getZbs());
            cv.put("sjcd",zpsc.getZpsjcd());
            cv.put("ktid",zpsc.getKtid());
            cv.put("gnss",zpsc.getGnss());
            cv.put("rlsb",zpsc.getRlsb());
            cv.put("sj",ZdUtil.getLongTime());
            long l=dbManager.getReadableDatabase().insert(DbConstants.T_ZPSC,null,cv);
            if(NettyConf.debug) {
                Log.e("TAG", "照片上传存储结果" + l);
            }
        }catch (Exception e){
            if(NettyConf.debug){
                Log.e("TAG",e.getMessage());
            }
        }finally {
            dbManager.closeDatabase();
        }
    }

    /**
     * 增
     * */
    public static  void insertZpdata(Zpdata zpdata){
        try {
            ContentValues cv=new ContentValues();
            cv.put("zpbh",zpdata.getZpbh());
            cv.put("zpsj",zpdata.getZpsj());
            cv.put("sj",ZdUtil.getLongTime());
            long l=dbManager.getReadableDatabase().insert(DbConstants.T_ZPDATA,null,cv);
            if(NettyConf.debug) {
                Log.e("TAG", "照片数据存储结果" + l);
            }
        }catch (Exception e){
            if(NettyConf.debug){
                Log.e("TAG",e.getMessage());
            }
        }finally {
            dbManager.closeDatabase();
        }
    }

    /**
     * 增
     * */
    public static void insertTdata(Tdata tdata){
        try{
            ContentValues cv=new ContentValues();
            cv.put("key",tdata.getKey());
            cv.put("parentid",tdata.getParentid());
            cv.put("data",tdata.getData());
            if(tdata.getInitsj()==null){
                cv.put("initsj",ZdUtil.getLongTime());
            }else{
                cv.put("initsj",tdata.getInitsj());
            }
            cv.put("sj",ZdUtil.getLongTime());
            long l=dbManager.getReadableDatabase().insert(DbConstants.T_DATA,null,cv);
            if(NettyConf.debug) {
                Log.e("TAG", "缓存存储结果" + l);
            }
        }catch (Exception e){
            if(NettyConf.debug){
                Log.e("TAG",e.getMessage());
            }
        }finally {
            dbManager.closeDatabase();
        }
    }

    /**
     * 增
     * */
    public static void insertTdatas(List<Tdata> tdataList){
        try {
            for (Tdata tdata : tdataList) {
                ContentValues cv = new ContentValues();
                cv.put("key", tdata.getKey());
                cv.put("parentid", tdata.getParentid());
                cv.put("data", tdata.getData());
                if (tdata.getInitsj() == null) {
                    cv.put("initsj", ZdUtil.getLongTime());
                } else {
                    cv.put("initsj", tdata.getInitsj());
                }
                cv.put("sj", ZdUtil.getLongTime());
                long l = dbManager.getReadableDatabase().insert(DbConstants.T_DATA, null, cv);
                Log.e("TAG", "缓存存储结果" + l);
            }
        }catch (Exception e){
            if(NettyConf.debug){
                Log.e("TAG",e.getMessage());
            }
        }finally {
            dbManager.closeDatabase();
        }
    }

    /**
     * 增
     * */
    public static void insertTdatas(List<Tdata> tdataList,int level){
        try {
            for (Tdata tdata : tdataList) {
                ContentValues cv = new ContentValues();
                cv.put("key", tdata.getKey());
                cv.put("parentid", tdata.getParentid());
                cv.put("data", tdata.getData());
                cv.put("level", level);
                if (tdata.getInitsj() == null) {
                    cv.put("initsj", ZdUtil.getLongTime());
                } else {
                    cv.put("initsj", tdata.getInitsj());
                }
                cv.put("sj", ZdUtil.getLongTime());
                long l = dbManager.getReadableDatabase().insert(DbConstants.T_DATA, null, cv);
                if(NettyConf.debug) {
                    Log.e("TAG", "缓存存储结果" + l);
                }
            }
        }catch (Exception e){
            if(NettyConf.debug){
                Log.e("TAG",e.getMessage());
            }
        }finally {
            dbManager.closeDatabase();
        }
    }

    /**
     * 增
     * */
    public static void insertTdataf(Tdata tdata){
        try {
            ContentValues cv = new ContentValues();
            cv.put("key", tdata.getKey());
            cv.put("parentid", tdata.getParentid());
            cv.put("data", tdata.getData());
            if (tdata.getInitsj() == null) {
                cv.put("initsj", ZdUtil.getLongTime());
            } else {
                cv.put("initsj", tdata.getInitsj());
            }
            cv.put("sj", ZdUtil.getLongTime());
            long l = dbManager.getReadableDatabase().insert(DbConstants.T_DATAF, null, cv);
            if(NettyConf.debug) {
                Log.e("TAG", "缓存存储结果" + l);
            }
        }catch (Exception e){
            if(NettyConf.debug){
                Log.e("TAG",e.getMessage());
            }
        }finally {
            dbManager.closeDatabase();
        }
    }

    public static void insertTsfrz(SfrzR sfrzR){
        try {
            String[] params = {sfrzR.getUuid(), String.valueOf(sfrzR.getLx())};
            int num = dbManager.getReadableDatabase().delete(DbConstants.T_SFRZ, "uuid=? and lx=?", params);

            if (NettyConf.debug) {
                Log.e("TAG", "身份认证删除数量：" + num);
            }
            ContentValues cv = new ContentValues();
            cv.put("uuid", sfrzR.getUuid());
            cv.put("lx", String.valueOf(sfrzR.getLx()));
            cv.put("tybh", sfrzR.getTybh());
            cv.put("sfzh", sfrzR.getSfzh());
            cv.put("cx", sfrzR.getCx());
            cv.put("xx", sfrzR.getXx());
            cv.put("xm", sfrzR.getXm());
            cv.put("sj", ZdUtil.getLongTime());
            cv.put("zp", sfrzR.getZp());
            long l = dbManager.getReadableDatabase().insert(DbConstants.T_SFRZ, null, cv);
            if(NettyConf.debug) {
                Log.e("TAG", "身份认证存储结果" + l);
            }
        }catch (Exception e){
            if(NettyConf.debug){
                Log.e("TAG",e.getMessage());
            }
        }finally {
            dbManager.closeDatabase();
        }
    }

    public static void insertDzwl(List<Dzwl> dlist){
        try {
            int num = dbManager.getReadableDatabase().delete(DbConstants.T_DZWL, null, null);
            if (NettyConf.debug) {
                Log.e("TAG", "场地信息证删除数量：" + num);
            }

            for(Dzwl dzwl:dlist){
                ContentValues cv = new ContentValues();
                cv.put("cdbh",dzwl.getCdbh());
                cv.put("cdmc",dzwl.getCdmc());
                cv.put("cddz",dzwl.getCddz());
                cv.put("pxcx",dzwl.getPxcx());
                cv.put("cdlx",dzwl.getCdlx());
                cv.put("zbd",dzwl.getZbd());
                cv.put("sj",ZdUtil.getLongTime());
                long l = dbManager.getReadableDatabase().insert(DbConstants.T_DZWL, null, cv);
                if(NettyConf.debug) {
                    Log.e("TAG", "场地存储结果：" + l);
                }
            }
        }catch (Exception e){
            if(NettyConf.debug){
                Log.e("TAG",e.getMessage());
            }
        }finally {
            dbManager.closeDatabase();
        }
    }

    public static void insertDzwl(Dzwl dzwl){
        try {
            String[] params={dzwl.getCdbh()};
            String condition="cdbh=?";
            int num = dbManager.getReadableDatabase().delete(DbConstants.T_DZWL, condition, params);
            if (NettyConf.debug) {
                Log.e("TAG", "场地信息证删除数量：" + num);
            }

            Log.e("TAG", "场地信息证删除数量：" + num);

            ContentValues cv = new ContentValues();
            cv.put("cdbh",dzwl.getCdbh());
            cv.put("cdmc",dzwl.getCdmc());
            cv.put("cddz",dzwl.getCddz());
            cv.put("pxcx",dzwl.getPxcx());
            cv.put("cdlx",dzwl.getCdlx());
            cv.put("zbd",dzwl.getZbd());
            cv.put("sj",ZdUtil.getLongTime());
            long l = dbManager.getReadableDatabase().insert(DbConstants.T_DZWL, null, cv);
            if(NettyConf.debug) {
                Log.e("TAG", "场地存储结果：" + l);
            }

        }catch (Exception e){
            if(NettyConf.debug){
                Log.e("TAG",e.getMessage());
            }
        }finally {
            dbManager.closeDatabase();
        }
    }

    public static void clearAllDzwl(){
        try {
            int num = deleteData(DbConstants.T_DZWL, null, null);
            if (NettyConf.debug) {
                Log.e("TAG", "场地信息证删除数量：" + num);
            }

        }catch (Exception e){
            if(NettyConf.debug){
                Log.e("TAG",e.getMessage());
            }
        }finally {
            dbManager.closeDatabase();
        }
    }

    public static void deleteDzwlByBh(List<String> slist){
        try {
            for(String s:slist){
                String[] params={s};
                String condition="cdbh=?";
                int num = deleteData(DbConstants.T_DZWL, condition, params);
                if (NettyConf.debug) {
                    Log.e("TAG", "场地信息证删除数量：" + num);
                }
            }
        }catch (Exception e){
            if(NettyConf.debug){
                Log.e("TAG",e.getMessage());
            }
        }finally {
            dbManager.closeDatabase();
        }
    }

    /**
     * 查
     * */
    public static ArrayList<Dzwl> querydzwl(String sql, String[] params){
        try {
            SQLiteDatabase readableDatabase = dbManager.getReadableDatabase();
            Cursor cursor = readableDatabase.rawQuery(sql, params);
            ArrayList<Dzwl> list = new ArrayList<>();
            if (cursor.moveToFirst()) {
                do {
                    Dzwl dzwl=new Dzwl();
                    dzwl.setCdbh(cursor.getString(1));
                    dzwl.setCdmc(cursor.getString(2));
                    dzwl.setCddz(cursor.getString(3));
                    dzwl.setPxcx(cursor.getString(4));
                    dzwl.setCdlx(cursor.getString(5));
                    dzwl.setZbd(cursor.getString(6));
                    list.add(dzwl);
                } while (cursor.moveToNext());
            }
            cursor.close();

            return list;
        }catch (Exception e){
            if(NettyConf.debug){
                Log.e("TAG",e.getMessage());
            }
            return null;
        }finally {
            dbManager.closeDatabase();
        }
    }

    /**
     * 查
     * */
    public static ArrayList<Line> queryline(String sql, String[] params){
        if(NettyConf.debug){
            Log.e("TAG","查询线路");
        }
        try {
            SQLiteDatabase readableDatabase = dbManager.getReadableDatabase();
            Cursor cursor = readableDatabase.rawQuery(sql, params);
            ArrayList<Line> list = new ArrayList<>();
            if (cursor.moveToFirst()) {
                do {
                    Line line=new Line();
                    line.setId(cursor.getInt(0));
                    line.setMc(cursor.getString(1));
                    line.setXlzb(cursor.getString(2));
                    list.add(line);
                } while (cursor.moveToNext());
            }
            cursor.close();

            return list;
        }catch (Exception e){
            if(NettyConf.debug){
                Log.e("TAG",e.getMessage());
            }
            return null;
        }finally {
            dbManager.closeDatabase();
        }
    }

    /**
     * 查
     * */
    public static ArrayList<Xsjl> queryxsjl(String sql, String[] params){
        try {
            SQLiteDatabase readableDatabase = dbManager.getReadableDatabase();
            Cursor cursor = readableDatabase.rawQuery(sql, params);
            ArrayList<Xsjl> list = new ArrayList<>();
            if (cursor.moveToFirst()) {
                do {
                    Xsjl xsjl = new Xsjl();
                    xsjl.setXsjlbh(cursor.getString(1));
                    xsjl.setXybh(cursor.getString(2));
                    xsjl.setJlbh(cursor.getString(3));
                    xsjl.setKtid(String.valueOf(cursor.getInt(4)));
                    xsjl.setJlcssj(cursor.getString(5));
                    xsjl.setPxkc(cursor.getString(6));
                    xsjl.setJlzt(cursor.getString(7));
                    xsjl.setZdsd(String.valueOf(cursor.getShort(8)));
                    xsjl.setXclc(String.valueOf(cursor.getShort(9)));
                    xsjl.setGnss(cursor.getString(10));
                    list.add(xsjl);
                } while (cursor.moveToNext());
            }
            cursor.close();

            return list;
        }catch (Exception e){
            if(NettyConf.debug){
                Log.e("TAG",e.getMessage());
            }
            return null;
        }finally {
            dbManager.closeDatabase();
        }
    }

    /**
     * 查询照片上传
     * @param sql
     * @param params
     * @return
     */
    public static ArrayList<Zpsc> queryZpsc(String sql,String[] params){
        try {
            SQLiteDatabase readableDatabase = dbManager.getReadableDatabase();
            Cursor cursor = readableDatabase.rawQuery(sql, params);
            ArrayList<Zpsc> list = new ArrayList<>();
            if (cursor.moveToFirst()) {
                do {
                    Zpsc zpsc = new Zpsc();
                    zpsc.setZpbh(cursor.getString(1));
                    zpsc.setBh(cursor.getString(2));
                    zpsc.setTdh(String.valueOf(cursor.getShort(3)));
                    zpsc.setTpcc(String.valueOf(cursor.getShort(4)));
                    zpsc.setSjlx(String.valueOf(cursor.getShort(5)));
                    zpsc.setZbs(String.valueOf(cursor.getShort(6)));
                    zpsc.setZpsjcd(String.valueOf(cursor.getInt(7)));
                    zpsc.setKtid(String.valueOf(cursor.getInt(8)));
                    zpsc.setGnss(cursor.getString(9));
                    zpsc.setRlsb(String.valueOf(cursor.getShort(10)));
                    list.add(zpsc);
                } while (cursor.moveToNext());
            }
            cursor.close();

            return list;
        }catch (Exception e){
            if(NettyConf.debug){
                Log.e("TAG",e.getMessage());
            }
            return null;
        }finally {
            dbManager.closeDatabase();
        }
    }

    /**
     * 查询照片数据
     * @param sql
     * @param params
     * @return
     */
    public static ArrayList<Zpdata> queryZpdata(String sql,String[] params){
        try {
            SQLiteDatabase readableDatabase = dbManager.getReadableDatabase();
            Cursor cursor = readableDatabase.rawQuery(sql, params);
            ArrayList<Zpdata> list = new ArrayList<>();
            if (cursor.moveToFirst()) {
                do {
                    Zpdata z = new Zpdata();
                    z.setZpbh(cursor.getString(1));
                    z.setZpsj(cursor.getBlob(2));
                    list.add(z);
                } while (cursor.moveToNext());
            }
            cursor.close();

            return list;
        }catch (Exception e){
            if(NettyConf.debug){
                Log.e("TAG",e.getMessage());
            }
            return null;
        }finally {
            dbManager.closeDatabase();
        }
    }

    /**
     * 查询缓存数据
     * @param sql
     * @param params
     * @return
     */
    public static ArrayList<Tdata> queryTdata(String sql,String[] params){
        try {
            SQLiteDatabase readableDatabase = dbManager.getReadableDatabase();
            Cursor cursor = readableDatabase.rawQuery(sql, params);
            ArrayList<Tdata> list = new ArrayList<>();
            if (cursor.moveToFirst()) {
                do {
                    Tdata tdata = new Tdata();
                    tdata.setKey(cursor.getString(1));
                    tdata.setParentid(cursor.getString(2));
                    tdata.setData(cursor.getString(3));
                    tdata.setInitsj(cursor.getLong(4));
                    list.add(tdata);
                } while (cursor.moveToNext());
            }
            cursor.close();

            return list;
        }catch (Exception e){
            if(NettyConf.debug){
                Log.e("TAG",e.getMessage());
            }
            return null;
        }finally {
            dbManager.closeDatabase();
        }
    }

    public static ArrayList<SfrzR> queryTsfrz(String sql,String[] params){
        try {
            SQLiteDatabase readableDatabase = dbManager.getReadableDatabase();
            Cursor cursor = readableDatabase.rawQuery(sql, params);
            ArrayList<SfrzR> list = new ArrayList<>();
            if (cursor.moveToFirst()) {
                do {
                    SfrzR sr = new SfrzR();
                    sr.setUuid(cursor.getString(1));
                    sr.setLx(Byte.valueOf(cursor.getString(2)));
                    sr.setTybh(cursor.getString(3));
                    sr.setSfzh(cursor.getString(4));
                    sr.setCx(cursor.getString(5));
                    sr.setXx(cursor.getString(6));
                    sr.setXm(cursor.getString(7));
                    sr.setZp(cursor.getString(9));
                    list.add(sr);
                } while (cursor.moveToNext());
            }
            cursor.close();

            return list;
        }catch (Exception e){
            if(NettyConf.debug){
                Log.e("TAG",e.getMessage());
            }
            return null;
        }finally {
            dbManager.closeDatabase();
        }
    }

    /**
     * 删除数据
     * @param params
     * @return
     */
    public static int deleteData(String table ,String condition,String[] params){
        try {
            int num = dbManager.getReadableDatabase().delete(table, condition, params);
            return num;
        }catch(Exception e){
            Log.e("TAG",e.getMessage());
            return 0;
        }finally {
            dbManager.closeDatabase();
        }
    }

}
