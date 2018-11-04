package com;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;

public class RFCommClient extends Thread {
	private static final String myServiceUUID = "0000110100001000800000805F9B34FB";		//"2d26618601fb47c28d9f10b8ec891363";
	private UUID MYSERVICEUUID_UUID = new UUID(myServiceUUID, false);
	private String serviceURL;
	private OutputStream os;
	
	public RFCommClient() {
		
	}
	public RFCommClient(String serviceURL) {
		this.serviceURL = serviceURL;
	}
	static byte readInput() {
		String inread = "";
		InputStreamReader in = new InputStreamReader(System.in);
		BufferedReader br = new BufferedReader(in);
		try {
				 inread = br.readLine();
			}
			catch (IOException ioe) {
				ioe.printStackTrace();
			}		
		byte b = (byte) inread.charAt(0);
		return b;
	}
	public void run() {
		try {
            String url = "btspp://B0D09C5720EA:3";
            StreamConnection con = (StreamConnection) Connector.open(serviceURL);
            System.out.println("**** In RFcommclient run method **** Service URL = " + serviceURL + ".");

            os = con.openOutputStream();
            
            System.out.println("Client Connection opened at " + con.toString());
            byte buffer[] = new byte[1];

            int ii = 0;		 
            while (ii < 6) {
            	System.out.print("\nPlease Enter command:- ");
            	buffer[0] = readInput();            	
            	if (buffer[0] == 'q') break;
            	if (buffer[0] == 0) continue;
            	os.write(buffer);
            	os.write(buffer);
            }            
            con.close();
        } 
		catch ( IOException e ) {
        	System.err.println("Unable to Connect to Bluetooth Device.  Check connection! \nProgram is now QUITTING...");
        	return;
        }
	}

}
