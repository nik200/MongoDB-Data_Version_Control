package com.plantiss.mongoadapter.server;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.bson.Document;

import com.plantiss.mongoadapter.objects.MongoVCRequest;
import com.plantiss.mongoadapter.objects.MongoVCResponse;
import com.plantiss.mongoadapter.objects.Notification;
import com.plantiss.mongoadapter.objects.NotificationClient;

public class NotificationHandler {
	
	
	HashMap<String, NotificationClient> clientSet = new HashMap<String, NotificationClient>();
	
	static HashMap<String, Socket> sockets = new HashMap<String, Socket>();
	
	static void sendNotification(final Notification notification) throws IOException{
		// send notifications to all the registered clients
		// this operation needs to be performed from a seperate thread....
		
		Thread notifier = new Thread(){

			@Override
			public void run() {
				ArrayList<String> toBeUnreg = new ArrayList<String>();
				for(String str:sockets.keySet()){
					
					try{
					OutputStream os = sockets.get(str).getOutputStream();
					ObjectOutputStream oos = new ObjectOutputStream(os);
					oos.writeObject(notification);
					oos.flush();
					System.out.println("Notification for "+notification.getCheckinID()+" sent to "+str);
					}
					catch(SocketException e){
						System.out.println("Error while sending notification to "+str);
						System.out.println(str+" will be unregistered");
						toBeUnreg.add(str);
					}
					catch(Exception e){
						System.out.println("Error while sending notification to "+str);
						e.printStackTrace();
					}
				}
				for(String str : toBeUnreg){
					sockets.remove(str);
				}
			}
		};
		notifier.start();
	}
		

	public static MongoVCResponse handler(MongoVCRequest query, Socket socket) {
		switch(query.getQueryType()){
		case REGISTER:
			sockets.put(query.getUserName(),socket);
			try {
				socket.setKeepAlive(true);
			} catch (SocketException e) {
				e.printStackTrace();
			}
			break;
		case UNREGISTER:
			Socket s = sockets.get(query.getUserName());
			try {
				s.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			sockets.remove(query.getUserName());
			break;
		}
		
		MongoVCResponse response = new MongoVCResponse();
		response.setStatus(new Document().append("status", "success"));
		return response;
	}

}
