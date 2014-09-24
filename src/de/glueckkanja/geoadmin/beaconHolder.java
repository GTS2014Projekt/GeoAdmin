package de.glueckkanja.geoadmin;

import com.estimote.sdk.Beacon;

public class BeaconHolder {
	private String macAddress;
	private double minRange;
	private double maxRange;
	
	public BeaconHolder(String macAddress, double range){
		this.macAddress=macAddress;
		compute(range);
	}
	
	public void compute(double range){
		if(range < minRange){
			minRange=range;
		}else{
			if(range>maxRange){
				maxRange=range;
			}
		}
	}
	
	public String doString(){
		return macAddress+", minRange: "+String.valueOf(minRange)+", maxRange: "+String.valueOf(maxRange);				
	}

	public double getMinRange() {
		return minRange;
	}

	public void setMinRange(double minRange) {
		this.minRange = minRange;
	}

	public double getMaxRange() {
		return maxRange;
	}

	public void setMaxRange(double maxRange) {
		this.maxRange = maxRange;
	}

	public String getMacAddress() {
		return macAddress;
	}

	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}
	
	
}
