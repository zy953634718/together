package com.example.together;

import java.util.ArrayList;
import java.util.List;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import com.baidu.mapapi.SDKInitializer;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.example.bean.Address;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class PathActivity extends Activity {
	private MapView mapView;

	private BaiduMap baiduMap;

	private List<Address> addressList;
	private Marker marker, tempMark;
	private GeoCoder mSearch;
	private View popupView;
	private TextView shoolName, distance, schoolAddress;
	private Boolean isFirstLoc = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.activity_path);

		mapView = (MapView) findViewById(R.id.path_map_view);

		baiduMap = mapView.getMap();
		baiduMap.setMapStatus(MapStatusUpdateFactory
				.newMapStatus(new MapStatus.Builder().zoom(16).build()));
		 initData();
		
		
		myLocation();// 定位当前位置
	}

	private void initData() {
		// TODO Auto-generated method stub

		addressList = new ArrayList<Address>();

		Address address = new Address();
		address.setLng(120.18718);
		address.setLat(30.194557);
		addressList.add(address);

		address = new Address();
		address.setLng(120.187036);
		address.setLat(30.196804);
		addressList.add(address);

		address = new Address();
		address.setLng(120.179957);
		address.setLat(30.19387);
		addressList.add(address);

		address = new Address();
		address.setLng(120.17331);
		address.setLat(30.191029);
		addressList.add(address);

		address = new Address();
		address.setLng(120.172124);
		address.setLat(30.195181);
		addressList.add(address);
		address = new Address();
		address.setLng(120.165261);
		address.setLat(30.198115);
		addressList.add(address);
		List<LatLng> pts = new ArrayList<LatLng>();

		for (int i = 0; i < addressList.size(); i++) {
			address = addressList.get(i);

			LatLng point = new LatLng(address.getLat(), address.getLng());
			pts.add(point);
			// 构建Marker图标
			BitmapDescriptor bitmap = BitmapDescriptorFactory
					.fromResource(R.drawable.school);
			// 构建MarkerOption，用于在地图上添加Marker
			OverlayOptions option = new MarkerOptions().position(point).icon(
					bitmap);
			// 在地图上添加Marker，并显示
			baiduMap.addOverlay(option);
			marker = (Marker) (baiduMap.addOverlay(option));

		}

		 

		baiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {
			@SuppressLint("ResourceAsColor")
			@Override
			public boolean onMarkerClick(Marker marker) {
				// TODO Auto-generated method stub
				if (tempMark != null) {
					BitmapDescriptor bitmap = BitmapDescriptorFactory
							.fromResource(R.drawable.school);
					tempMark.setIcon(bitmap);
				}
				tempMark = marker;

				popupView = LayoutInflater.from(PathActivity.this).inflate(
						R.layout.popup, null);
				shoolName = (TextView) popupView
						.findViewById(R.id.select_tv_name);
				distance = (TextView) popupView
						.findViewById(R.id.select_tv_distance);
				schoolAddress = (TextView) popupView
						.findViewById(R.id.select_tv_addrss);

				Drawable d = popupView.getBackground();
				int Y = d.getIntrinsicHeight();

				// 创建InfoWindow , 传入 view， 地理坐标， y 轴偏移量
				final InfoWindow mInfoWindow = new InfoWindow(popupView, marker
						.getPosition(), Y - 20);

				BitmapDescriptor bitmap = BitmapDescriptorFactory
						.fromResource(R.drawable.select_school);
				// 构建MarkerOption，用于在地图上添加Marker
				marker.setIcon(bitmap);
				mSearch = GeoCoder.newInstance();
				ReverseGeoCodeOption rge = new ReverseGeoCodeOption();
				mSearch.reverseGeoCode(rge.location(marker.getPosition()));
				mSearch.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
					public void onGetGeoCodeResult(GeoCodeResult result) {
						if (result == null
								|| result.error != SearchResult.ERRORNO.NO_ERROR) {
							// 没有检索到结果
						}
						// 获取地理编码结果
					}

					@Override
					public void onGetReverseGeoCodeResult(
							ReverseGeoCodeResult result) {
						if (result == null
								|| result.error != SearchResult.ERRORNO.NO_ERROR) {
							// 没有找到检索结果
							schoolAddress.setText("没有找到检索结果");
						} else {
							// 获取反向地理编码结果
							schoolAddress.setText(result.getAddress());

						}
					}
				});
				// 显示InfoWindow
				baiduMap.showInfoWindow(mInfoWindow);

				return false;
			}

		});

		baiduMap.setOnMapClickListener(new OnMapClickListener() {

			@Override
			public boolean onMapPoiClick(MapPoi arg0) {
				// TODO Auto-generated method stub

				return false;
			}

			@Override
			public void onMapClick(LatLng arg0) {
				// TODO Auto-generated method stub
				baiduMap.hideInfoWindow();

				BitmapDescriptor bitmap = BitmapDescriptorFactory
						.fromResource(R.drawable.school);
				if (tempMark != null)
					tempMark.setIcon(bitmap);
			}
		});

	}
	
	
	/**
	 * 定位当前所在位置
	 */
	private void myLocation() {
		// TODO Auto-generated method stub
		baiduMap.setMyLocationEnabled(true);
		// 定位初始化
		LocationClient mLocClient = new LocationClient(PathActivity.this);
		LocationClientOption option = new LocationClientOption();

		mLocClient.registerLocationListener(new MyLocationListener());

		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(1000);
		mLocClient.setLocOption(option);
		mLocClient.start();

	}

	public class MyLocationListener implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			// TODO Auto-generated method stub
			if (location == null || mapView == null)
				return;
			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(location.getRadius())
					// 此处设置开发者获取到的方向信息，顺时针0-360
					.direction(100).latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			baiduMap.setMyLocationData(locData);
			if (isFirstLoc) {
				isFirstLoc = false;
				LatLng ll = new LatLng(location.getLatitude(),
						location.getLongitude());
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
				baiduMap.animateMapStatus(u);
			}
		}
	}

}
