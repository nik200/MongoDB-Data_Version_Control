package com.plantiss.mongoadapter.server;

import java.util.ArrayList;
import java.util.HashMap;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class DBConnection {
	static MongoClient mongoClient = null;
	static MongoDatabase db = null;
	
	private static HashMap<String,MongoCollection<Document>> collMap 
			= new HashMap<String, MongoCollection<Document>>();

	private static HashMap<String,MongoCollection<Document>> archive_collMap 
	= new HashMap<String, MongoCollection<Document>>();
	
	public static void initController(String dbName, String host, int port) {		
		if(db!=null) return;
		MongoClientOptions options = MongoClientOptions.builder().connectionsPerHost(100).build();
		try {
			mongoClient = new MongoClient(new ServerAddress(host,port),options);
			System.out.println("Connecting to db "+dbName);
			db = mongoClient.getDatabase(dbName);
			
			//for(String str: db.listCollectionNames()){
			//	System.out.println("Adding collection "+str);
			//	collMap.put(str,db.getCollection(str));
			//	archive_collMap.put(str,db.getCollection(str+"_archive"));
			//}
			collMap.put("verdata",db.getCollection("verdata"));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static String getDBName() {
		// TODO Auto-generated method stub
		return db.getName();
	}
	
	static MongoCollection<Document> getCollection(String collName){
		if(collMap.get(collName)==null){
			collMap.put(collName, db.getCollection(collName));
			archive_collMap.put(collName,  db.getCollection(collName+"_archive"));
			System.out.println("Collection '"+collName+"' initialized and pooled");
		}
		return collMap.get(collName);
	}
	static MongoCollection<Document> getArchiveCollection(String collName){
		return archive_collMap.get(collName);
	}
}
