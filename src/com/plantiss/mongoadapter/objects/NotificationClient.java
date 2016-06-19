package com.plantiss.mongoadapter.objects;

import org.bson.Document;

public class NotificationClient {
	private String host;
	private int port;
	private int status = 0;
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	
	
	public Document getDocument(){
		Document returnVal = new Document();
		returnVal.append("_id", new Document("host", host).append("port", port))
		.append("status", status);
		
		return returnVal;
	}
	
	public NotificationClient getClient(Document doc){
		NotificationClient client = new NotificationClient();
		client.host = ((Document)doc.get("_id")).getString("host");
		client.port = ((Document)doc.get("_id")).getInteger("port");
		client.status = doc.getInteger("status");
		return client;
	}
}
