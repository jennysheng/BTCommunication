package com;

import java.io.IOException;
import java.util.Vector;

import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;

public class RemoteDeviceDiscovery {
	  private Vector/*<RemoteDevice>*/ devicesDiscovered = new Vector();
	    public RemoteDeviceDiscovery() {	
	    }
	    public Vector getRemoteDevices() {
	    	try {
	    		discoveryMain();
	    	}
	    	catch (IOException ie) {
	    		ie.printStackTrace();
	    	}
	    	catch (InterruptedException ie) {
	    		ie.printStackTrace();
	    	}
	    	return devicesDiscovered;
	    }
	    private void discoveryMain() throws IOException, InterruptedException {
	        final Object inquiryCompletedEvent = new Object();
	        devicesDiscovered.clear();
	        DiscoveryListener listener = new DiscoveryListener() {

	            public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
	                devicesDiscovered.addElement(btDevice);
	            }

	            public void inquiryCompleted(int discType) {
	                synchronized(inquiryCompletedEvent){
	                    inquiryCompletedEvent.notifyAll();
	                }
	            }

	            public void serviceSearchCompleted(int transID, int respCode) {
	            }

	            public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
	            }
	        };

	        synchronized(inquiryCompletedEvent) {
	            boolean started = LocalDevice.getLocalDevice().getDiscoveryAgent().startInquiry(DiscoveryAgent.GIAC, listener);
	            if (started) {
	                System.out.println("wait for device inquiry to complete...");
	                inquiryCompletedEvent.wait();
	            }
	        }
	 }
}
