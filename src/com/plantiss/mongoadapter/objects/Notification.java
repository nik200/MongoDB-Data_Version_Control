package com.plantiss.mongoadapter.objects;

import java.io.Serializable;
import java.util.ArrayList;

public class Notification implements Serializable{
	private NotificationType type;
	private ArrayList<String> modifiedObjects = new ArrayList<String>();
	private ArrayList<String> removedObjects = new ArrayList<String>();
	private String collection;
	private String db;
	private long timestamp;
	private String userID;
	private String checkinID;
	public NotificationType getType() {
		return type;
	}
	public void setType(NotificationType type) {
		this.type = type;
	}
	public ArrayList<String> getModifiedObjects() {
		return modifiedObjects;
	}
	public void setModifiedObjects(ArrayList<String> modifiedObjects) {
		this.modifiedObjects = modifiedObjects;
	}
	public ArrayList<String> getRemovedObjects() {
		return removedObjects;
	}
	public void setRemovedObjects(ArrayList<String> removedObjects) {
		this.removedObjects = removedObjects;
	}
	public String getCollection() {
		return collection;
	}
	public void setCollection(String collection) {
		this.collection = collection;
	}
	public String getDb() {
		return db;
	}
	public void setDb(String db) {
		this.db = db;
	}
	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	public String getUserID() {
		return userID;
	}
	public void setUserID(String userID) {
		this.userID = userID;
	}
	public String getCheckinID() {
		return checkinID;
	}
	public void setCheckinID(String checkinID) {
		this.checkinID = checkinID;
	}
	
	public void addModifiedObjectID(String id){
		modifiedObjects.add(id);
	}
	
	public void addRemovedObjectID(String id){
		modifiedObjects.add(id);
	}
	
}
