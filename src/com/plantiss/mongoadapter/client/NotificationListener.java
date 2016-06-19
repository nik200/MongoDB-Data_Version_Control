package com.plantiss.mongoadapter.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import com.plantiss.mongoadapter.objects.MongoVCRequest;
import com.plantiss.mongoadapter.objects.MongoVCResponse;
import com.plantiss.mongoadapter.objects.Notification;
import com.plantiss.mongoadapter.objects.NotificationReciever;
import com.plantiss.mongoadapter.objects.RequestType;

/**
 * Recieve notifications from VC services.
 * @author Nikhil
 *
 */
public class NotificationListener implements Runnable{

	private String serviceHost;
	private String userName;
	private int servicePort;
	private Socket socket;
	private NotificationReciever recieverImpl;
	Thread listener;

	public NotificationListener(String userName, String serviceHost,
			int servicePort) {
		super();
		this.userName = userName;
		this.serviceHost = serviceHost;
		this.servicePort = servicePort;
	}

	
	public void setServiceHost(String serviceHost) {
		this.serviceHost = serviceHost;
	}

	public void setServicePort(int servicePort) {
		this.servicePort = servicePort;
	}


	public void setUserName(String userName) {
		this.userName = userName;
	}

	
	public boolean unregister() throws IOException, ClassNotFoundException{
		// create request object
		
		InetAddress address = InetAddress.getByName(serviceHost);
		Socket socket1 = new Socket(address, servicePort);
		
		MongoVCRequest regReq = new MongoVCRequest();
		regReq.setType(RequestType.UNREGISTER);
		regReq.setUserName(userName);


		//Send the message to the server    
		OutputStream os = socket1.getOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(os);
		oos.writeObject(regReq);
		oos.flush();
		System.out.println("Message sent to the server for notification unregistration");

		//Get the return message from the server
		InputStream is = socket1.getInputStream();
		ObjectInputStream ois = new ObjectInputStream(is);
		MongoVCResponse response = (MongoVCResponse) ois.readObject();
		System.out.println("Message received from the server with unregistration status : " +response.getStatus().getString("status"));
		if(response.getStatus().getString("status").equals("success")){
			listener.interrupt();
			socket.close();
			socket1.close();
			socket = null;
			return true;
		}
		else{
			socket.close();
			socket1.close();
			return false;
		}

		
	}

	public boolean register(NotificationReciever reciever) throws IOException, ClassNotFoundException{
		this.recieverImpl = reciever;
		String host = serviceHost;
		int port = servicePort;
		InetAddress address = InetAddress.getByName(host);
		socket = new Socket(address, port);
		socket.setKeepAlive(true);
		// create request object
		MongoVCRequest regReq = new MongoVCRequest();
		regReq.setType(RequestType.REGISTER);
		regReq.setUserName(userName);


		//Send the message to the server    
		OutputStream os = socket.getOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(os);
		oos.writeObject(regReq);
		oos.flush();
		System.out.println("Message sent to the server for notification registration");

		//Get the return message from the server
		InputStream is = socket.getInputStream();
		ObjectInputStream ois = new ObjectInputStream(is);
		MongoVCResponse response = (MongoVCResponse) ois.readObject();
		System.out.println("Message received from the server with registration status : " +response.getStatus().getString("status"));
		if(response.getStatus().getString("status").equals("success")){
			listener = new Thread(this);
			listener.start();
			return true;
		}
		else return false;
	}


	@Override
	public void run() {

		while(socket!=null && !socket.isClosed()){
			
			try{
				InputStream is = socket.getInputStream();
				if(is.available()!=0){
					ObjectInputStream ois = new ObjectInputStream(is);
					Notification n = (Notification) ois.readObject();
					recieverImpl.notify(n);
				}
				Thread.sleep(2000);
			}
			catch(InterruptedException e){
				System.out.println("Terminating notification listener");
				return;
			}
			catch(SocketException e){
				
				// it can happen..
				// add code here to retry session.
				
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

}
