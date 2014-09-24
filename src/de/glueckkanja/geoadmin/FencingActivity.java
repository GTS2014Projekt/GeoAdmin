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
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class FencingActivity extends Activity {
	private static final String ESTIMOTE_PROXIMITY_UUID = "B9407F30-F5F8-466E-AFF9-25556B57FE6D";
	private static final Region ALL_ESTIMOTE_BEACONS = new Region("regionId", ESTIMOTE_PROXIMITY_UUID, null, null);
	
	private BeaconManager beaconManager = new BeaconManager(this);
	private ArrayList<BeaconHolder> beaconList = new ArrayList<BeaconHolder>();
	
	private ListView listview;
	
	public boolean isFencing=false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fencing);
		
		listview = (ListView)findViewById(R.id.listView);
		
		init();
		
		
	}
	
	
	
	private void populateListView(String[] myItems) {
		//String[] myItems = {"BeaconA","BeaconB","BeaconC"};
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.da_item, myItems);
		
		listview.setAdapter(adapter);
		
	}



	private void init(){
		beaconManager.setRangingListener(new BeaconManager.RangingListener() {
			@Override
			public void onBeaconsDiscovered(Region region, List<Beacon> pulledBeacons) {
				// TODO Auto-generated method stub
				Log.d("Fencing", "Ranged beacons: " + pulledBeacons.toString());	    	
				if(isFencing){
					if(!pulledBeacons.isEmpty()){
						for(int i=0;i<pulledBeacons.size();i++){
							String currentMAC = pulledBeacons.get(i).getMacAddress();
							//Search if Beacon already in List
							boolean isInside = false;
							int index=0;
							for(int j=0;i<beaconList.size();i++){
								if(beaconList.get(j).getMacAddress() == currentMAC){
									isInside=true;
									index=j;
								}
							}
							if(isInside){
								double range = Utils.computeAccuracy(pulledBeacons.get(i));
								beaconList.get(index).compute(range);
							}else{
								double range = Utils.computeAccuracy(pulledBeacons.get(i));
								beaconList.add(new BeaconHolder(currentMAC, range));
							}
						}	
					}else{
						Toast.makeText(getBaseContext(), "No Beacons in Range, cannot start Fencing", Toast.LENGTH_LONG).show();
					}
					
				}
			}			
		});
	}
	
	
	public void oc_startFencing(View v){
		beaconList.clear();
		isFencing=true;
	}
	
	public void oc_stopFencing(View v){
		isFencing=false;
		for(int i=0;i<beaconList.size();i++){
			Log.d("Fencing", beaconList.get(i).doString());
		}
		populateListView(createStringArray(beaconList));
	}
	
	private String[] createStringArray(ArrayList<BeaconHolder> list){
		String[] returnList = null;
		for(int i=0;i<list.size();i++){
			returnList[i] = list.get(i).doString();
		}		
		return returnList;
	}
	
	
	
	private void connect(){
		beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
			@Override public void onServiceReady() {
				try {
					beaconManager.startRanging(ALL_ESTIMOTE_BEACONS);
					Log.d("Fencing", "Starting Ranging");
				} catch (RemoteException e) {
					Log.e("Fencing", "Cannot start ranging", e);
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
			Log.e("Fencing", "Cannot stop but it does not matter now", e);
		}
		Toast.makeText(getBaseContext(), "Ranging-Service stopped", Toast.LENGTH_LONG).show();
	}

}
