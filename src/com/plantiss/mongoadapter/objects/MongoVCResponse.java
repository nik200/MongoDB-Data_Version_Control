package com.plantiss.mongoadapter.objects;

import java.io.Serializable;
import java.util.ArrayList;

import org.bson.Document;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;

public class MongoVCResponse implements Serializable{
	private ArrayList<Document> docList = new ArrayList<Document>();
	private Document status;
	
	public ArrayList<Document> getDocList() {
		return docList;
	}
	public void setDocList(ArrayList<Document> docList) {
		this.docList = docList;
	}
	public Document getStatus() {
		return status;
	}
	public void setStatus(Document status) {
		this.status = status;
	}
	
}
