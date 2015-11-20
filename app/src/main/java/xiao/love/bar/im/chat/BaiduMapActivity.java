/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package xiao.love.bar.im.chat;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;

import xiao.love.bar.R;
import xiao.love.bar.component.util.T;


public class BaiduMapActivity extends Activity {
	private Context mContext;
	private LocationClient mLocClient;
	private MyLocationListenner myListener = new MyLocationListenner();
	private Button sendButton = null;
	private BDLocation lastLocation;
	private ProgressDialog progressDialog;
	private BaiduMap mBaiduMap;
	private MapView mMapView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		//在使用SDK各组件之前初始化context信息，传入ApplicationContext
		//注意该方法要再setContentView方法之前实现
		SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.activity_baidumap);

		sendButton = (Button) findViewById(R.id.btn_location_send);
		/****地图初始化*****/
		mMapView = (MapView) findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap();
		// 开启定位图层
		mBaiduMap.setMyLocationEnabled(true);
		mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
				LocationMode.NORMAL, true, null));
		//如果传递了经纬度则直接显示地图定位；否则，获取定位信息并在地图上显示
		Intent intent = getIntent();
		double latitude = getIntent().getDoubleExtra("latitude", 0);
		if (latitude == 0) {
			initLocationClient();
		} else {
			double longtitude = intent.getDoubleExtra("longitude", 0);
			showMapLocation(latitude, longtitude);
		}
	}

	/**
	 * 初始化定位
	 */
	private void initLocationClient() {
		showDialog();

		mLocClient = new LocationClient(this);
		mLocClient.registerLocationListener(myListener);
		//设置定位选项
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("gcj02");
		option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
		option.setScanSpan(30000);
		option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
		option.setOpenGps(true);//可选，默认false,设置是否使用gps
		option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
		option.setIgnoreKillProcess(true);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
		option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
		option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
		option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
		mLocClient.setLocOption(option);
	}

	/**
	 * 显示地图定位
	 * @param latitude
	 * @param longtitude
	 */
	private void showMapLocation(double latitude, double longtitude){
		mBaiduMap.clear();
		LatLng llA = new LatLng(latitude, longtitude);
		//添加Overlay
		CoordinateConverter converter= new CoordinateConverter();
		converter.coord(llA);
		converter.from(CoordinateConverter.CoordType.COMMON);
		LatLng convertLatLng = converter.convert();
		OverlayOptions ooA = new MarkerOptions().position(convertLatLng).icon(BitmapDescriptorFactory
				.fromResource(R.drawable.icon_marka))
				.zIndex(4).draggable(true);
		mBaiduMap.addOverlay(ooA);
		//地图上显示定位
		MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(convertLatLng, 17.0f);
		mBaiduMap.animateMapStatus(u);
	}

	@Override
	protected void onPause() {
		mMapView.onPause();
		if (mLocClient != null) {
			mLocClient.stop();
		}
		super.onPause();
		lastLocation = null;
	}

	@Override
	protected void onResume() {
		mMapView.onResume();
		if (mLocClient != null) {
			mLocClient.start();
		}
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		if (mLocClient != null) {
			mLocClient.stop();
		}
		// 关闭定位图层
		mBaiduMap.setMyLocationEnabled(false);
		mMapView.onDestroy();
		super.onDestroy();
	}

	/**
	 * 监听函数，有新位置的时候，格式化成字符串，输出到屏幕中
	 */
	public class MyLocationListenner implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null) {
				return;
			}

			if (location.getLocType() == BDLocation.TypeServerError) {
				T.showShort(mContext, "定位失败，服务端异常");
			}else if (location.getLocType() == BDLocation.TypeNetWorkException ||
					location.getLocType() == BDLocation.TypeCriteriaException) {
				T.showShort(mContext, "定位失败，请检查手机网络是否通畅,是否处于飞行模式");
			}

			sendButton.setEnabled(true);
			dismissDialog();

			if (lastLocation != null) {
				if (lastLocation.getLatitude() == location.getLatitude() && lastLocation.getLongitude() == location.getLongitude()) {
					return;
				}
			}

			lastLocation = location;
			showMapLocation(lastLocation.getLatitude(), lastLocation.getLongitude());
		}
	}

	/**
	 * 回退按钮
	 * @param v
	 */
	public void back(View v) {
		finish();
	}

	/**
	 * 发送地理位置
	 * @param view
	 */
	public void sendLocation(View view) {
		Intent intent = this.getIntent();
		intent.putExtra("latitude", lastLocation.getLatitude());
		intent.putExtra("longitude", lastLocation.getLongitude());
		intent.putExtra("address", lastLocation.getAddrStr());
		this.setResult(RESULT_OK, intent);
		finish();
		overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
	}

	private void showDialog(){
		progressDialog = new ProgressDialog(this);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setMessage(getResources().getString(R.string.Making_sure_your_location));
		//用户主动取消进度条时，退出页面
		progressDialog.setOnCancelListener(new OnCancelListener() {
			public void onCancel(DialogInterface arg0) {
				dismissDialog();
				finish();
			}
		});
		progressDialog.show();
	}

	private void dismissDialog(){
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
	}
}

//public class BaiduMapActivity extends Activity {
//	private Context mContext;
//	private LocationClient mLocClient;
//	private MyLocationListenner myListener = new MyLocationListenner();
//	private Button sendButton = null;
//	private BDLocation lastLocation;
//	private ProgressDialog progressDialog;
//	private BaiduMap mBaiduMap;
//	private MapView mMapView;
//	/**
//	 * 构造广播监听类，监听 SDK key 验证以及网络异常广播
//	 */
//	private BroadcastReceiver mBaiduReceiver = new BroadcastReceiver() {
//		public void onReceive(Context context, Intent intent) {
//			String action = intent.getAction();
//			if (action.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)) {
//				String str = getResources().getString(R.string.please_check);
//				Toast.makeText(mContext, str, Toast.LENGTH_SHORT).show();
//			} else if (action.equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)) {
//				String str = getResources().getString(R.string.Network_error);
//				Toast.makeText(mContext, str, Toast.LENGTH_SHORT).show();
//			}
//		}
//	};
//
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		mContext = this;
//		//在使用SDK各组件之前初始化context信息，传入ApplicationContext
//        //注意该方法要再setContentView方法之前实现
//        SDKInitializer.initialize(getApplicationContext());
//		setContentView(R.layout.activity_baidumap);
//
//		sendButton = (Button) findViewById(R.id.btn_location_send);
//		mMapView = (MapView) findViewById(R.id.bmapView);
//		mBaiduMap = mMapView.getMap();
//		MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(15.0f);
//		mBaiduMap.setMapStatus(msu);
//		mMapView.setLongClickable(true);
//
//		Intent intent = getIntent();
//		double latitude = getIntent().getDoubleExtra("latitude", 0);
//		if (latitude == 0) {
//			mMapView = new MapView(this, new BaiduMapOptions());
//			mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
//					LocationMode.NORMAL, true, null));
//			showMapWithLocationClient();
//		} else {
//			double longtitude = intent.getDoubleExtra("longitude", 0);
//			String address = intent.getStringExtra("address");
//			LatLng p = new LatLng(latitude, longtitude);
//			mMapView = new MapView(this,
//					new BaiduMapOptions().mapStatus(new MapStatus.Builder()
//							.target(p).build()));
//			showMap(latitude, longtitude, address);
//		}
//		// 注册 SDK 广播监听者
//		IntentFilter iFilter = new IntentFilter();
//		iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
//		iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
//		registerReceiver(mBaiduReceiver, iFilter);
//	}
//
//	private void showMap(double latitude, double longtitude, String address) {
//		sendButton.setVisibility(View.GONE);
//		LatLng llA = new LatLng(latitude, longtitude);
//		CoordinateConverter converter= new CoordinateConverter();
//		converter.coord(llA);
//		converter.from(CoordinateConverter.CoordType.COMMON);
//		LatLng convertLatLng = converter.convert();
//		OverlayOptions ooA = new MarkerOptions().position(convertLatLng).icon(BitmapDescriptorFactory
//				.fromResource(R.drawable.icon_marka))
//				.zIndex(4).draggable(true);
//		mBaiduMap.addOverlay(ooA);
//		MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(convertLatLng, 17.0f);
//		mBaiduMap.animateMapStatus(u);
//	}
//
//	private void showMapWithLocationClient() {
//		String str1 = getResources().getString(R.string.Making_sure_your_location);
//		progressDialog = new ProgressDialog(this);
//		progressDialog.setCanceledOnTouchOutside(false);
//		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//		progressDialog.setMessage(str1);
//
//		progressDialog.setOnCancelListener(new OnCancelListener() {
//
//			public void onCancel(DialogInterface arg0) {
//				if (progressDialog.isShowing()) {
//					progressDialog.dismiss();
//				}
//				Log.d("map", "cancel retrieve location");
//				finish();
//			}
//		});
//
//		progressDialog.show();
//
//		mLocClient = new LocationClient(this);
//		mLocClient.registerLocationListener(myListener);
//
//		LocationClientOption option = new LocationClientOption();
//		option.setOpenGps(true);// 打开gps
//		// option.setCoorType("bd09ll"); //设置坐标类型
//		// Johnson change to use gcj02 coordination. chinese national standard
//		// so need to conver to bd09 everytime when draw on baidu map
//		option.setCoorType("gcj02");
//		option.setScanSpan(30000);
//		option.setAddrType("all");
//		mLocClient.setLocOption(option);
//	}
//
//	@Override
//	protected void onPause() {
//		mMapView.onPause();
//		if (mLocClient != null) {
//			mLocClient.stop();
//		}
//		super.onPause();
//		lastLocation = null;
//	}
//
//	@Override
//	protected void onResume() {
//		mMapView.onResume();
//		if (mLocClient != null) {
//			mLocClient.start();
//		}
//		super.onResume();
//	}
//
//	@Override
//	protected void onDestroy() {
//		if (mLocClient != null)
//			mLocClient.stop();
//		mMapView.onDestroy();
//		unregisterReceiver(mBaiduReceiver);
//		super.onDestroy();
//	}
//
//	/**
//	 * 监听函数，有新位置的时候，格式化成字符串，输出到屏幕中
//	 */
//	public class MyLocationListenner implements BDLocationListener {
//		@Override
//		public void onReceiveLocation(BDLocation location) {
//			if (location == null) {
//				return;
//			}
//			Log.d("map", "On location change received:" + location);
//			Log.d("map", "addr:" + location.getAddrStr());
//			sendButton.setEnabled(true);
//			if (progressDialog != null) {
//				progressDialog.dismiss();
//			}
//
//			if (lastLocation != null) {
//				if (lastLocation.getLatitude() == location.getLatitude() && lastLocation.getLongitude() == location.getLongitude()) {
//					Log.d("map", "same location, skip refresh");
//					// mMapView.refresh(); //need this refresh?
//					return;
//				}
//			}
//			lastLocation = location;
//			mBaiduMap.clear();
//			LatLng llA = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
//			CoordinateConverter converter= new CoordinateConverter();
//			converter.coord(llA);
//			converter.from(CoordinateConverter.CoordType.COMMON);
//			LatLng convertLatLng = converter.convert();
//			OverlayOptions ooA = new MarkerOptions().position(convertLatLng).icon(BitmapDescriptorFactory
//					.fromResource(R.drawable.icon_marka))
//					.zIndex(4).draggable(true);
//			mBaiduMap.addOverlay(ooA);
//			MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(convertLatLng, 17.0f);
//			mBaiduMap.animateMapStatus(u);
//		}
//
//		public void onReceivePoi(BDLocation poiLocation) {
//			if (poiLocation == null) {
//				return;
//			}
//		}
//	}
//
//	public void back(View v) {
//		finish();
//	}
//
//	public void sendLocation(View view) {
//		Intent intent = this.getIntent();
//		intent.putExtra("latitude", lastLocation.getLatitude());
//		intent.putExtra("longitude", lastLocation.getLongitude());
//		intent.putExtra("address", lastLocation.getAddrStr());
//		this.setResult(RESULT_OK, intent);
//		finish();
//		overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
//	}
//
//}
