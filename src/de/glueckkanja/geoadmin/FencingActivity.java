package de.glueckkanja.geoadmin;

import java.util.ArrayList;
import java.util.List;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.Utils;

import android.app.Activity;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class FencingActivity extends Activity {
	private static final String ESTIMOTE_PROXIMITY_UUID = "B9407F30-F5F8-466E-AFF9-25556B57FE6D";
	private static final Region ALL_ESTIMOTE_BEACONS = new Region("regionId", ESTIMOTE_PROXIMITY_UUID, null, null);
	private BeaconManager beaconManager = new BeaconManager(this);
	private ArrayList<beaconHolder> beaconList = new ArrayList<beaconHolder>();
	
	public boolean isFencing=false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fencing);
		init();
		
	}
	
	private void init(){
		beaconManager.setRangingListener(new BeaconManager.RangingListener() {
			@Override
			public void onBeaconsDiscovered(Region region, List<Beacon> pulledBeacons) {
				// TODO Auto-generated method stub
				Log.d("Beacons", "Ranged beacons: " + pulledBeacons.toString());	    	
				if(isFencing){
					for(int i=0;i<pulledBeacons.size();i++){
						String currentMAC = pulledBeacons.get(i).getMacAddress();
						//Seach if Beacon already in List
					}
				}
			}			
		});
	}
	private void connect(){
		beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
			@Override public void onServiceReady() {
				try {
					beaconManager.startRanging(ALL_ESTIMOTE_BEACONS);
					Log.d("Beacons", "Starting Ranging");
				} catch (RemoteException e) {
					Log.e("Beacons", "Cannot start ranging", e);
				}
		    }
		});
	}
	
	protected void onStart(){
		super.onStart();
		connect();		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.fencing, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void onDestroy(){
		super.onDestroy();
		try {
			beaconManager.stopRanging(ALL_ESTIMOTE_BEACONS);
		} catch (RemoteException e) {
			Log.e("Beacons", "Cannot stop but it does not matter now", e);
		}
		Toast.makeText(getBaseContext(), "Service stopped", Toast.LENGTH_LONG).show();
	}

}
