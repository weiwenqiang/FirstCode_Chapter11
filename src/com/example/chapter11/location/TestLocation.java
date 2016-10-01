package com.example.chapter11.location;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.chapter11.R;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

public class TestLocation extends Activity {
	private TextView positionText;
	private LocationManager locationManager;
	private String provider;

	public static final int SHOW_LOCATION = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.b1_location);
		positionText = (TextView) findViewById(R.id.b1_position_text);

		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		// ��ȡ���п��õ�λ���ṩ��
		List<String> providerList = locationManager.getProviders(true);
		if (providerList.contains(LocationManager.GPS_PROVIDER)) {
			provider = LocationManager.GPS_PROVIDER;
		} else if (providerList.contains(LocationManager.NETWORK_PROVIDER)) {
			provider = LocationManager.NETWORK_PROVIDER;
		} else {
			// ��ǰû�п���λ���ṩ��ʱ������Toast��ʾ�û�
			Toast.makeText(this, "No location provider to use(��ǰû�п���λ���ṩ��)",
					Toast.LENGTH_SHORT).show();
			positionText.setText("No location provider to use(��ǰû�п���λ���ṩ��)");
			return;
		}
		Location location = locationManager.getLastKnownLocation(provider);
		if (location != null) {
			// ��ʾ��ǰλ����Ϣ
			showLocation(location);
		}
		/*
		 * �����ĸ����� ��һ��������λ���ṩ�������� �ڶ��������Ǽ���λ�ñ仯��ʱ�������Ժ���Ϊ��λ
		 * �����������Ǽ���λ�ñ仯�ľ�����������Ϊ��λ ���ĸ���������LocationListener������
		 */
		locationManager.requestLocationUpdates(provider, 5000, 1,
				locationListener);
	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case SHOW_LOCATION:
				String currentPosition = (String) msg.obj;
				positionText.setText(currentPosition);
				break;

			default:
				break;
			}
		}

	};

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (locationManager != null) {
			// �رճ���ʱ���������Ƴ�
			locationManager.removeUpdates(locationListener);
		}
	}

	LocationListener locationListener = new LocationListener() {

		@Override
		public void onLocationChanged(Location location) {
			// ���µ�ǰ�豸��λ��
			showLocation(location);
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {

		}

		@Override
		public void onProviderEnabled(String provider) {

		}

		@Override
		public void onProviderDisabled(String provider) {

		}

	};

	private void showLocation(final Location location) {
		String currentPosition = "latitude is " + location.getLatitude() + "\n"
				+ "longitude is " + location.getLongitude();
		positionText.setText(currentPosition);
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					// ��װ����������Ľӿڵ�ַ
					StringBuilder url = new StringBuilder();
					url.append("http://maps.googleapis.com/maps/api/geocode/json?latlng=");
					url.append(location.getLatitude()).append(",");
					url.append(location.getLongitude());
					url.append("&sensor=false");

					HttpClient httpClient = new DefaultHttpClient();
					HttpGet httpGet = new HttpGet(url.toString());
					// ��������Ϣͷ��ָ�����ԣ���֤�������᷵����������
					httpGet.addHeader("Accept-Language", "zh-CN");
					HttpResponse httpResponse = httpClient.execute(httpGet);
					if (httpResponse.getStatusLine().getStatusCode() == 200) {
						HttpEntity entity = httpResponse.getEntity();
						String response = EntityUtils.toString(entity, "utf-8");
						JSONObject jsonObject = new JSONObject(response);
						// ��ȡresults�ڵ��µ�λ����Ϣ
						JSONArray resultArray = jsonObject
								.getJSONArray("results");
						if (resultArray.length() > 0) {
							JSONObject subObject = resultArray.getJSONObject(0);
							// ȡ����ʽ�����λ����Ϣ
							String address = subObject
									.getString("formatted_address");
							Message message = new Message();
							message.what = SHOW_LOCATION;
							message.obj = address;
							handler.sendMessage(message);
						}
					}
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (ParseException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
}
