package com;

import java.io.IOException;
import java.util.Vector;

import javax.bluetooth.DataElement;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;

public class ServiceSearcher {
    static final UUID OBEX_FILE_TRANSFER = new UUID(0x1106);
	static final UUID OBEX_OBJECT_PUSH = new UUID(0x1106);		//?????
    private	int leng = 0;
    private Vector remoteDevices;
    RemoteDevice btDevice;
    public static final Vector/*<String>*/ serviceFound = new Vector();

    public ServiceSearcher(RemoteDevice btDevice) {
    	this.btDevice = btDevice;
    	try {
    		System.out.println("Searching for service.... device = " + btDevice.getFriendlyName(false) + "....");
    		mainSearcher( "0000110100001000800000805F9B34FB");
    	}
    	catch (InterruptedException ie) {
    		ie.printStackTrace();
    	}
    	catch (IOException ie) {
    		ie.printStackTrace();
    	}
    	
    }
    public Vector getServices() {
    	return serviceFound;
    }
    private void mainSearcher(String servString) throws IOException, InterruptedException {
        serviceFound.clear();

        UUID serviceUUID = OBEX_OBJECT_PUSH;
        if ((servString.length() > 0)) {
            serviceUUID = new UUID(servString, false);
        }

        final Object serviceSearchCompletedEvent = new Object();

        DiscoveryListener listener = new DiscoveryListener() {

            public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
            }

            public void inquiryCompleted(int discType) {
            }

            public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
            	
                for (int i = 0; i < servRecord.length; i++) {
                    String url = servRecord[i].getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);
                    if (url == null) {
                        continue;
                    }
                    serviceFound.add(url);
                    DataElement serviceName = servRecord[i].getAttributeValue(0x0100);
                    if (serviceName != null) {
                        System.out.println("service " + serviceName.getValue() + " found " + url);
                    } else {
                        System.out.println("service found " + url);
                    }
                    leng++;
                }
            }

            public void serviceSearchCompleted(int transID, int respCode) {
                System.out.println("service search completed! " + leng  + " service(s) found.");
                synchronized(serviceSearchCompletedEvent){
                    serviceSearchCompletedEvent.notifyAll();
                }
            }
        };

        UUID[] searchUuidSet = new UUID[] { serviceUUID };
        int[] attrIDs =  new int[] {
                0x0100 // Service name
        };

        if (btDevice != null) {
            synchronized(serviceSearchCompletedEvent) {
                System.out.println("search services on " + btDevice.getBluetoothAddress() + " " + btDevice.getFriendlyName(false));
                LocalDevice.getLocalDevice().getDiscoveryAgent().searchServices(attrIDs, searchUuidSet, btDevice, listener);
                serviceSearchCompletedEvent.wait();
            }
        }
    }
}
