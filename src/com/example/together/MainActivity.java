package com.example.together;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.PoiOverlay;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {
	private MapView mapView;
	private TextView baseMap;
	private TextView satelliteMap;
	private TextView trafficMap;
	private BaiduMap mBaiduMap;
	private Boolean isFirstLoc = true;
	private TextView path;
	private EditText keyword;
	private TextView search;
	private PoiSearch mPoiSearch = null;
	private SuggestionSearch mSuggestionSearch = null;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.activity_main);

		mapView = (MapView) findViewById(R.id.bmapView);
		keyword = (EditText) findViewById(R.id.main_edit_input);
		search = (TextView) findViewById(R.id.main_btu_search);
		
		
		mapView.showZoomControls(false);
		mBaiduMap = mapView.getMap();
		mBaiduMap.setMapStatus(MapStatusUpdateFactory
				.newMapStatus(new MapStatus.Builder().zoom(16).build()));
		
		switchMap();// �л���ͼ
		myLocation();// ��λ��ǰλ��
		searchLocation();//����

	}
	
	/**
	 * ����λ����Ϣ
	 */
	private void searchLocation(){
		mPoiSearch = PoiSearch.newInstance();
		mPoiSearch.setOnGetPoiSearchResultListener(new GetPoiSearchResult());
		mSuggestionSearch = SuggestionSearch.newInstance();
		mSuggestionSearch.setOnGetSuggestionResultListener(new GetSuggestionResult());
		
		search.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mPoiSearch.searchInCity((new PoiCitySearchOption())
						.city("����")
						.keyword(keyword.getText().toString())
						.pageNum(1));
			}
		});
	}
	
	private class GetPoiSearchResult implements OnGetPoiSearchResultListener{

		@Override
		public void onGetPoiDetailResult(PoiDetailResult result) {
			// TODO Auto-generated method stub
			if (result.error != SearchResult.ERRORNO.NO_ERROR) {
				Toast.makeText(MainActivity.this, "��Ǹ��δ�ҵ����", Toast.LENGTH_SHORT)
						.show();
			} else {
				Toast.makeText(MainActivity.this, result.getName() + ": " + result.getAddress(), Toast.LENGTH_SHORT)
				.show();
			}
			
		}

		@Override
		public void onGetPoiResult(PoiResult result) {
			// TODO Auto-generated method stub
			if (result == null
					|| result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
				Toast.makeText(MainActivity.this, "δ�ҵ����", Toast.LENGTH_LONG)
				.show();
				return;
			}
			if (result.error == SearchResult.ERRORNO.NO_ERROR) {
				mBaiduMap.clear();
				PoiOverlay overlay = new MyPoiOverlay(mBaiduMap);
				mBaiduMap.setOnMarkerClickListener(overlay);
				overlay.setData(result);
				overlay.addToMap();
				overlay.zoomToSpan();
				return;
			}
			if (result.error == SearchResult.ERRORNO.AMBIGUOUS_KEYWORD) {

				// ������ؼ����ڱ���û���ҵ����������������ҵ�ʱ�����ذ����ùؼ�����Ϣ�ĳ����б�
				/*String strInfo = "��";
				for (CityInfo cityInfo : result.getSuggestCityList()) {
					strInfo += cityInfo.city;
					strInfo += ",";
				}
				strInfo += "�ҵ����";
				Toast.makeText(MainActivity.this, strInfo, Toast.LENGTH_LONG)
						.show();*/
			}
			
		}
		
	}
	
	private class GetSuggestionResult implements OnGetSuggestionResultListener{
		@Override
		public void onGetSuggestionResult(SuggestionResult res) {
			// TODO Auto-generated method stub
			/*if (res == null || res.getAllSuggestions() == null) {
				return;
			}
			sugAdapter.clear();
			for (SuggestionResult.SuggestionInfo info : res.getAllSuggestions()) {
				if (info.key != null)
					sugAdapter.add(info.key);
			}
			sugAdapter.notifyDataSetChanged();*/
		}
		
	}
	
	private class MyPoiOverlay extends PoiOverlay {
		
		public MyPoiOverlay(BaiduMap baiduMap) {
			super(baiduMap);
		}

		@Override
		public boolean onPoiClick(int index) {
			super.onPoiClick(index);
			PoiInfo poi = getPoiResult().getAllPoi().get(index);
			// if (poi.hasCaterDetails) {
				mPoiSearch.searchPoiDetail((new PoiDetailSearchOption())
						.poiUid(poi.uid));
			// }
			return true;
		}
	}
	

	/**
	 * ��λ��ǰ����λ��
	 */
	private void myLocation() {
		// TODO Auto-generated method stub
		mBaiduMap.setMyLocationEnabled(true);
		// ��λ��ʼ��
		LocationClient mLocClient = new LocationClient(getApplicationContext());
		LocationClientOption option = new LocationClientOption();

		mLocClient.registerLocationListener(new MyLocationListener());

		option.setOpenGps(true);// ��gps
		option.setCoorType("bd09ll"); // ������������
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
					// �˴����ÿ����߻�ȡ���ķ�����Ϣ��˳ʱ��0-360
					.direction(100).latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			mBaiduMap.setMyLocationData(locData);
			if (isFirstLoc) {
				isFirstLoc = false;
				LatLng ll = new LatLng(location.getLatitude(),
						location.getLongitude());
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
				mBaiduMap.animateMapStatus(u);
			}
		}
	}

	/**
	 * �л���ͼ
	 */
	private void switchMap() {
		baseMap = (TextView) findViewById(R.id.main_btu_base);
		satelliteMap = (TextView) findViewById(R.id.main_btu_satellite);
		trafficMap = (TextView) findViewById(R.id.main_btu_traffic);
		path = (TextView) findViewById(R.id.main_btu_path);
		baseMap.setOnClickListener(new SwitchMapCilck());
		satelliteMap.setOnClickListener(new SwitchMapCilck());
		trafficMap.setOnClickListener(new SwitchMapCilck());
		path.setOnClickListener(new SwitchMapCilck());

	}

	private class SwitchMapCilck implements android.view.View.OnClickListener {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.main_btu_base:
				mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
				mBaiduMap.setTrafficEnabled(false);
				Toast.makeText(getApplicationContext(), "�л���ͨ��ͼ��",
						Toast.LENGTH_SHORT).show();
				break;
			case R.id.main_btu_satellite:
				mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
				Toast.makeText(getApplicationContext(), "�л����ǵ�ͼ��",
						Toast.LENGTH_SHORT).show();
				break;
			case R.id.main_btu_traffic:
				mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
				mBaiduMap.setTrafficEnabled(true);
				Toast.makeText(getApplicationContext(), "�л���ͨ��ͼ��",
						Toast.LENGTH_SHORT).show();
				break;
			case R.id.main_btu_path:
				Intent it = new Intent(MainActivity.this, PathActivity.class);
				startActivity(it);
				break;

			default:
				Toast.makeText(getApplicationContext(), "��ͼ�л�����",
						Toast.LENGTH_SHORT).show();
				break;
			}
		}

	}

}
