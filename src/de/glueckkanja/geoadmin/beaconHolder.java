package de.glueckkanja.geoadmin;

import com.estimote.sdk.Beacon;

public class beaconHolder {
	private Beacon myBeacon;
	private String macAddress;
	private float minRange;
	private float maxRange;
	
	public beaconHolder(Beacon myBeacon){
		this.myBeacon = myBeacon;
	}

	public float getMinRange() {
		return minRange;
	}

	public void setMinRange(float minRange) {
		this.minRange = minRange;
	}

	public float getMaxRange() {
		return maxRange;
	}

	public void setMaxRange(float maxRange) {
		this.maxRange = maxRange;
	}

	public String getMacAddress() {
		return macAddress;
	}

	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}
	
	
}
