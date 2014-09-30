package de.glueckkanja.geoadmin;

import java.util.ArrayList;
import java.util.List;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.Utils;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
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
	private static final int REQUEST_ENABLE_BT = 1234;
	
	private BeaconManager beaconManager;
	private ArrayList<BeaconHolder> beaconList = new ArrayList<BeaconHolder>();
	
	private ListView listview;
	
	public boolean isFencing=false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fencing);
		
		listview = (ListView)findViewById(R.id.listView);
		
		beaconManager = new BeaconManager(this);
	    beaconManager.setRangingListener(new BeaconManager.RangingListener() {
	    	@Override
	    	public void onBeaconsDiscovered(Region region, final List<Beacon> pulledBeacons) {
	    		// Note that results are not delivered on UI thread.
	    		runOnUiThread(new Runnable() {
	    			@Override
	    			public void run() {	  
						if(isFencing){	 
							if(!pulledBeacons.isEmpty()){
								for(int i=0;i<pulledBeacons.size();i++){
									String currentMAC = pulledBeacons.get(i).getMacAddress();
									//Search if Beacon already in List
									boolean isInside = false;
									int index=0;
									for(int j=0;j<beaconList.size();j++){
										if(beaconList.get(j).getMacAddress().compareTo(currentMAC)==0){
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
	    });	
	}
	
	
	
	private void populateListView(String[] myItems) {
		//String[] myitems = {"BeaconA","BeaconB","BeaconC"};
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.da_item, myItems);
		
		listview.setAdapter(adapter);
		
	}



	
	
	public void oc_startFencing(View v){
		beaconList.clear();
		isFencing=true;
		Log.d("Fencing", "starte Fencing");
	}
	
	
	public void oc_stopFencing(View v){
		isFencing=false;
		for(int i=0;i<beaconList.size();i++){
			Log.d("Fencing", beaconList.get(i).doString());
		}
		populateListView(createStringArray(beaconList));
	}
	
	
	
	private String[] createStringArray(ArrayList<BeaconHolder> list){
		String[] returnList  = new String[20];
		for(int i=0;i<list.size();i++){
			returnList[i] = list.get(i).doString();
		}		
		return returnList;
	}
	
	
	protected void onStart(){
		super.onStart();

	    // Check if device supports Bluetooth Low Energy.
	    if (!beaconManager.hasBluetooth()) {
	      Toast.makeText(this, "Device does not have Bluetooth Low Energy", Toast.LENGTH_LONG).show();
	      return;
	    }

	 // If Bluetooth is not enabled, let user enable it.
	    if (!beaconManager.isBluetoothEnabled()) {
	      Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
	      startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
	    } else {
	      connectToService();
	    }	 
	   	
	}

	private void connectToService() {
		// TODO Auto-generated method stub
	    beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
	      @Override
	      public void onServiceReady() {
	        try {
	          beaconManager.startRanging(ALL_ESTIMOTE_BEACONS);
	        } catch (RemoteException e) {
	          Toast.makeText(getBaseContext(), "Cannot start ranging, something terrible happened",
	              Toast.LENGTH_LONG).show();
	          Log.e("Fencing", "Cannot start ranging", e);
	        }
	      }
	    });
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
