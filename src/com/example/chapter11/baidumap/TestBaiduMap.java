package com.example.chapter11.baidumap;

import java.util.ArrayList;
import java.util.List;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.GroundOverlayOptions;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.BaiduMap.OnMarkerDragListener;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.example.chapter11.R;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Window;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

public class TestBaiduMap extends Activity {
	
//	private BMapManager manager;//�ɰ�
	private MapView mMapView;
	private BaiduMap mBaiduMap;
	
	private LocationManager locationManager;
	private String provider;
	
	LocationClient mLocClient;
	public MyLocationListenner myListener = new MyLocationListenner();
	boolean isFirstLoc = true; // �Ƿ��״ζ�λ
	
	private LocationMode mCurrentMode;
    BitmapDescriptor mCurrentMarker;
    
    private Marker mMarkerA;
    
    BitmapDescriptor bdA = BitmapDescriptorFactory
            .fromResource(R.drawable.icon_marka);
    BitmapDescriptor bdB = BitmapDescriptorFactory
            .fromResource(R.drawable.icon_markb);
    BitmapDescriptor bdC = BitmapDescriptorFactory
            .fromResource(R.drawable.icon_markc);
    BitmapDescriptor bdD = BitmapDescriptorFactory
            .fromResource(R.drawable.icon_markd);
    BitmapDescriptor bd = BitmapDescriptorFactory
            .fromResource(R.drawable.icon_gcoding);
    BitmapDescriptor bdGround = BitmapDescriptorFactory
            .fromResource(R.drawable.ground_overlay);
    

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
//		manager = new BMapManager();
//		manager.init();
		setContentView(R.layout.b2_baidu_map);
		mMapView = (MapView) findViewById(R.id.map_view);
		mBaiduMap = mMapView.getMap();
		mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
		
//		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//		// ��ȡ���п��õ�λ���ṩ��
//		List<String> providerList = locationManager.getProviders(true);
//		if (providerList.contains(LocationManager.GPS_PROVIDER)) {
//			provider = LocationManager.GPS_PROVIDER;
//		} else if (providerList.contains(LocationManager.NETWORK_PROVIDER)) {
//			provider = LocationManager.NETWORK_PROVIDER;
//		} else {
//			// ��ǰû�п���λ���ṩ��ʱ������Toast��ʾ�û�
//			Toast.makeText(this, "No location provider to use(��ǰû�п���λ���ṩ��)",
//					Toast.LENGTH_SHORT).show();
//			return;
//		}
//		Location location = locationManager.getLastKnownLocation(provider);
//		if(location !=null){
//			
//		}
		mCurrentMode = LocationMode.NORMAL;
        mBaiduMap
        .setMyLocationConfigeration(new MyLocationConfiguration(
                mCurrentMode, true, null));
		
		
		mBaiduMap.setMyLocationEnabled(true);
        // ��λ��ʼ��
        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // ��gps
        option.setCoorType("bd09ll"); // ������������
        option.setScanSpan(1000);
        mLocClient.setLocOption(option);
        mLocClient.start();
        initOverlay();
	}
	
//	private void navigateTo(Location location){
//		MapController controller = mapView.getCon
//	}

	@Override
    protected void onPause() {
        // MapView������������Activityͬ������activity����ʱ�����MapView.onPause()
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        // MapView������������Activityͬ������activity�ָ�ʱ�����MapView.onResume()
        mMapView.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        // MapView������������Activityͬ������activity����ʱ�����MapView.destroy()
        mMapView.onDestroy();
        super.onDestroy();
    }
    
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view ���ٺ��ڴ����½��յ�λ��
            if (location == null || mMapView == null) {
                return;
            }
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
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(18.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }
        }

        public void onReceivePoi(BDLocation poiLocation) {
        }
    }
    
    private void initOverlay(){
    	LatLng llA = new LatLng(31.189931, 121.644883);//�ٶȵ�ͼ��ȷλ��
        LatLng llB = new LatLng(31.191242, 121.629119);
        LatLng llC = new LatLng(31.191342, 121.629219);
        LatLng llD = new LatLng(31.191442, 121.629319);
        
    	MarkerOptions ooA = new MarkerOptions().position(llA).icon(bdA)
                .zIndex(9).draggable(true);
    	mMarkerA = (Marker) (mBaiduMap.addOverlay(ooA));
    	
    	MarkerOptions ooB = new MarkerOptions().position(llB).icon(bdB)
                .zIndex(5);
    	MarkerOptions ooC = new MarkerOptions().position(llC).icon(bdC)
                .perspective(false).anchor(0.5f, 0.5f).rotate(30).zIndex(7);
    	
    	mBaiduMap.addOverlay(ooB);
    	mBaiduMap.addOverlay(ooC);
    	
    	ArrayList<BitmapDescriptor> giflist = new ArrayList<BitmapDescriptor>();
        giflist.add(bdA);
        giflist.add(bdB);
        giflist.add(bdC);
        MarkerOptions ooD = new MarkerOptions().position(llD).icons(giflist)
                .zIndex(0).period(10);
        mBaiduMap.addOverlay(ooD);
    	
    	LatLng southwest = new LatLng(31.191042, 121.628919);
        LatLng northeast = new LatLng(31.191242, 121.629119);
        LatLngBounds bounds = new LatLngBounds.Builder().include(northeast)
                .include(southwest).build();

        OverlayOptions ooGround = new GroundOverlayOptions()
                .positionFromBounds(bounds).image(bdGround).transparency(0.8f);
        mBaiduMap.addOverlay(ooGround);

        MapStatusUpdate u = MapStatusUpdateFactory
                .newLatLng(bounds.getCenter());
        mBaiduMap.setMapStatus(u);

        mBaiduMap.setOnMarkerDragListener(new OnMarkerDragListener() {
            public void onMarkerDrag(Marker marker) {
            }

            public void onMarkerDragEnd(Marker marker) {
                Toast.makeText(
                		TestBaiduMap.this,
                        "��ק��������λ�ã�" + marker.getPosition().latitude + ", "
                                + marker.getPosition().longitude,
                        Toast.LENGTH_LONG).show();
            }

            public void onMarkerDragStart(Marker marker) {
            }
        });
    }
}
