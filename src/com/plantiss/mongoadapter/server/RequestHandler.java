package com.plantiss.mongoadapter.server;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import com.plantiss.mongoadapter.objects.MongoVCRequest;
import com.plantiss.mongoadapter.objects.RequestType;

public class RequestHandler {
	private static Socket socket;
	public static void main(String[] args) {

		// initialize system variables
		int port = 25000;
		String dbName = "repo";
		int dbPort = 27017;
		String dbHost = "localhost";
		try{
			port=Integer.parseInt(System.getProperty("mongovc.service.port"));
			System.out.println("Setting port "+port);
		}catch(Exception e){
			System.out.println("Setting default port 25000.");
		}

		dbName = System.getProperty("mongovc.db.name");
		if(dbName==null|| dbName.equals("")){
			//System.out.println("Connecting to default db 'repo'");
			dbName = "repo";
		}else{
			//System.out.println("Connecting to db "+dbName);
		}

		try{
			dbPort=Integer.parseInt(System.getProperty("mongovc.db.port"));
			System.out.println("Setting DB port "+dbPort);
		}catch(Exception e){
			System.out.println("Setting to default DB port 27017.");
		}
		
		dbHost = System.getProperty("mongovc.db.host");
		if(dbHost==null|| dbHost.equals("")){
			//System.out.println("Connecting to default db 'repo'");
			dbHost = "localhost";
		}

		try
		{
			//   int port = 25000;
			ServerSocket serverSocket = new ServerSocket(port);
			System.out.println("Server Started and listening to the port "+port);
			DBConnection.initController(dbName, dbHost, dbPort);
			System.out.println("Connection initialized to DB server "+dbHost+":"+dbPort);

			while(true)
			{
				try{
					//Reading the message from the client
					socket = serverSocket.accept();
					InputStream is = socket.getInputStream();
					ObjectInputStream ois = new ObjectInputStream(is);
					MongoVCRequest query = (MongoVCRequest) ois.readObject();

					System.out.println("Query recieved. Username : "+query.getUserName()
							+" Query type "+query.getQueryType());

					OutputStream os = socket.getOutputStream();
					ObjectOutputStream oos = new ObjectOutputStream(os);
					
					if(query.getQueryType()==RequestType.REGISTER
							|| query.getQueryType()==RequestType.UNREGISTER){
						oos.writeObject(NotificationHandler.handler(query, socket));
					}else{
						oos.writeObject(DBRequestHandler.queryProcessor(query));
					}
					
					//oos.writeObject(DBRequestHandler.queryProcessor(query));
					oos.flush();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				socket.close();
			}
			catch(Exception e){}
		}
	}
}

