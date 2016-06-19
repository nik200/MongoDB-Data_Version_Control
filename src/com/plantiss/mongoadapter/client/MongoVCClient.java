package com.plantiss.mongoadapter.client;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

import org.bson.Document;

import com.plantiss.mongoadapter.objects.MongoVCRequest;
import com.plantiss.mongoadapter.objects.MongoVCResponse;
import com.plantiss.mongoadapter.objects.RequestType;


public class MongoVCClient {
	private static Socket socket;
	 
    static MongoVCResponse requestor(MongoVCRequest queryObject, String serviceHost, int servicePort)
    {
    	MongoVCResponse response = null;
        try
        {
        	//String host = "107.180.74.131";
            String host = serviceHost;
            int port = servicePort;
            InetAddress address = InetAddress.getByName(host);
            socket = new Socket(address, port);
 
            //Send the message to the server    
            OutputStream os = socket.getOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(os);
           
            oos.writeObject(queryObject);
            oos.flush();
            System.out.println("Message sent to the server");
 
            //Get the return message from the server
            InputStream is = socket.getInputStream();
            ObjectInputStream ois = new ObjectInputStream(is);
            response = (MongoVCResponse) ois.readObject();
            
            System.out.println("Message received from the server with status : " +response.getStatus());
            return response;
        }
        catch (Exception exception)
        {
        	//exception.getMessage();
            exception.printStackTrace();      
        }
        finally
        {
            //Closing the socket
            try
            {
            	response = new MongoVCResponse();
            	response.setStatus(new Document("status", "Connection failure"));
            	if(socket!=null && !socket.isClosed())
            		socket.close();
            }
            catch(Exception e)
            {
               e.printStackTrace();
            }
        }
        System.out.println("Status : " +response.getStatus());
        return response;
    }
}
