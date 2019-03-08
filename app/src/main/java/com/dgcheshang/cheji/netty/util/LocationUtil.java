package com.dgcheshang.cheji.netty.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.dgcheshang.cheji.CjApplication;
import com.dgcheshang.cheji.Tools.Speaking;
import com.dgcheshang.cheji.netty.conf.NettyConf;
import com.dgcheshang.cheji.netty.timer.XsjlTimer;

public class LocationUtil {
	public static boolean state=true;//定位状态
	public static boolean gpskg;
	public static long endTime;//定位最后更新的时间

	LocationListener locationListener = new LocationListener() {
		/**
		 * 位置信息变化时触发
		 */
		@Override
		public void onLocationChanged(Location location) {
			if(location!=null) {
				//最后更新时间
				endTime=location.getTime();

				if(!state){
					state=true;
                    Speaking.in("GPS正常");
				}

				//1分钟内记录学时定位状态
				if(!XsjlTimer.dwstate){
					XsjlTimer.dwstate=true;
				}

				NettyConf.location = location;
				float speed = location.getSpeed();
				if (speed > NettyConf.zdsdM) {
					NettyConf.zdsdM = speed;
				}

				if(location.getSpeed()>=1){
					NettyConf.fx=location.getBearing();
				}

			}

		}
		/**
		 * GPS状态变化时触发
		 */
		@Override
		public void onStatusChanged(String s, int i, Bundle bundle) {
		}
		/**
		 * GPS开启时触发
		 */
		@Override
		public void onProviderEnabled(String s) {
			if(!gpskg) {
				gpskg=true;
                Speaking.in("GPS开启");
			}
			if(NettyConf.debug) {
				Log.e("TAG", "GPS开启时触发");
			}
		}
		/**
		 * GPS禁用时触发
		 */
		@Override
		public void onProviderDisabled(String s) {
			if(state){
				state=false;
				gpskg=false;
                Speaking.in("GPS禁用");
			}
			if(NettyConf.debug) {
				Log.e("TAG", "GPS禁用时触发");
			}
		}
	};

	public static Location getNewGps(){
		LocationManager locationManager = (LocationManager) CjApplication.getInstance().getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);//设置定位精准度
		criteria.setAltitudeRequired(true);//是否要求海拔
        criteria.setBearingRequired(true);//是否要求方向
		criteria.setCostAllowed(false);//是否要求收费
		criteria.setSpeedRequired(true);//是否要求速度
		criteria.setBearingAccuracy(Criteria.ACCURACY_HIGH);//设置方向精确度
		criteria.setSpeedAccuracy(Criteria.ACCURACY_HIGH);//设置速度精确度
		criteria.setPowerRequirement(Criteria.POWER_LOW);//设置相对省电

		String provider = locationManager.getBestProvider(criteria, true);
		return locationManager.getLastKnownLocation(provider);
	}

	/**
	 * 获取经纬度
	 *
	 * */
	public void getGPS() {
		LocationManager locationManager = (LocationManager) CjApplication.getInstance().getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);//设置定位精准度
		criteria.setAltitudeRequired(true);//是否要求海拔
		criteria.setBearingRequired(true);//是否要求方向
		criteria.setCostAllowed(false);//是否要求收费
		criteria.setSpeedRequired(true);//是否要求速度
		criteria.setBearingAccuracy(Criteria.ACCURACY_HIGH);//设置方向精确度
		criteria.setSpeedAccuracy(Criteria.ACCURACY_HIGH);//设置速度精确度
		criteria.setPowerRequirement(Criteria.POWER_LOW);//设置相对省电
//         取得效果最好的位置服务
		String provider = locationManager.getBestProvider(criteria, true);
		if (ActivityCompat.checkSelfPermission(CjApplication.getInstance(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(CjApplication.getInstance(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			// TODO: Consider calling
			//    ActivityCompat#requestPermissions
			// here to request the missing permissions, and then overriding
			//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
			//                                          int[] grantResults)
			// to handle the case where the user grants the permission. See the documentation
			// for ActivityCompat#requestPermissions for more details.
			return;
		}
		NettyConf.location = locationManager.getLastKnownLocation(provider);
		if(NettyConf.location==null){
			if(state) {
				state = false;
                Speaking.in("GPS获取失败");
			}
			if(NettyConf.debug) {
				Log.e("TAG", "没监听到");
			}
		}else{

            Speaking.in("GPS正常");

			if(NettyConf.debug) {
				Log.e("TAG", "监听到:" + NettyConf.location.getAltitude());
			}
		}
		locationManager.requestLocationUpdates(provider, 2000, 0, locationListener);

//		float speed = location.getSpeed();//速度
//		float bearing = location.getBearing();//方向
//		long time = location.getTime();//时间
//		double altitude = location.getAltitude();//海拔高度
//		float accuracy = location.getAccuracy();//精准度
//		double latitude = location.getLatitude();//纬度
//		double longitude = location.getLongitude();//经度
//
//		Log.d("TAG","定位信息："+location);


	}


	/**
	 * 判断GPS是否开启，GPS或者AGPS开启一个就认为是开启的
	 * @param
	 * @return true 表示开启
	 */
	public static final boolean isOPen() {
		LocationManager locationManager
				= (LocationManager) CjApplication.getInstance().getSystemService(Context.LOCATION_SERVICE);
		// 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
		boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		// 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
		boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		if (gps || network) {
			return true;
		}

		return false;
	}



}


