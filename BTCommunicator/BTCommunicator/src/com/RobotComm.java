package com;

/*
 * Some or all of the following jar files are necessary and should be included in the project Build path:
 * The jar file are not part of a standard java installation and must be downloaded from the respective sites.
 * bluecove-2.1.1.jar
 * jsr179.jar
 * jsr180.jar
 * jsr184.jar
 * jsr75.jar
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Vector;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DataElement;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;

public class RobotComm {

	/**
	 * @param args
	 */
	private static LocalDevice localDevice; // local Bluetooth Manager
	private static DiscoveryAgent discoveryAgent; // discovery agent
	/**
	 * Initialize
	 */
	static void btInit()  {
		try {
		    localDevice = null;
		    discoveryAgent = null;
		    // Retrieve the local device to get to the Bluetooth Manager
		    localDevice = LocalDevice.getLocalDevice();
		    // Servers set the discoverable mode to GIAC
		    localDevice.setDiscoverable(DiscoveryAgent.GIAC);
		    // Clients retrieve the discovery agent
		    discoveryAgent = localDevice.getDiscoveryAgent(); 
	    }
		catch (BluetoothStateException bse) {
			bse.printStackTrace();
		}
		System.out.println("Bluetooth Initialization Complete");
	}
	
	static String detectLocal() {
		String address = "";
		LocalDevice ld;
		
		try {
			ld = LocalDevice.getLocalDevice();
			address = localDevice.getBluetoothAddress();
			System.out.println("Local Device Properties: " + ld.getProperty(address) + " Friendly Name = " + ld.getFriendlyName());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return address;
	}
	
	static String readKBInput() {
		String inread = "";
		InputStreamReader in = new InputStreamReader(System.in);
		BufferedReader br = new BufferedReader(in);
		try {
				 inread = br.readLine();
			}
			catch (IOException ioe) {
				ioe.printStackTrace();
			}				
		return inread;
	}

	static Vector remoteDiscovery() {
		Vector devicesDiscovered = new Vector();
		RemoteDevice rd;
		int i= 0;
		RemoteDeviceDiscovery rdd = new RemoteDeviceDiscovery();
		devicesDiscovered = rdd.getRemoteDevices();

		while (i < devicesDiscovered.size()) {
			rd = (RemoteDevice) devicesDiscovered.get(i);
			try {
				System.out.println(i + ".. " + rd.getFriendlyName(false) + "\t\t -- \t" + rd.getBluetoothAddress());
			}
			catch (IOException ioe) {
			}
			i++;
		}
		System.out.print("Enter device number to search service on:- ");
		i = Integer.parseInt(readKBInput());
		ServiceSearcher ss = new ServiceSearcher((RemoteDevice) devicesDiscovered.get(i));
		System.out.println("== BACK FROM REMOTE DICOVERY:: " + devicesDiscovered.size() + " devices detected. \n");
		return ss.getServices();
	}
	
	public static void main(String[] args) {
		Vector services;
		String clientURL = "";

		btInit();
		System.out.println("Bluetooth Discovered on this machine at address " + detectLocal());

 		services = remoteDiscovery();

  		if (services.size()  > 0) {
			clientURL = (String) services.remove(0);
			System.out.println("**** In MAIN **** Service URL = " + clientURL + ".");
		}

		RFCommClient clientc = new RFCommClient(clientURL);
		clientc.start();
	}
}


