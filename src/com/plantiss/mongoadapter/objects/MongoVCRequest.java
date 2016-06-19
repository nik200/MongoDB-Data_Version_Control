package com.plantiss.mongoadapter.objects;

import java.io.Serializable;
import java.util.ArrayList;
import javax.naming.PartialResultException;
import org.bson.Document;

import com.mongodb.client.model.UpdateOptions;


public class MongoVCRequest implements Serializable{
	
	private RequestType queryType;
	private ArrayList<Document> queryList = new ArrayList<Document>();
	private ArrayList<Document> docList = new ArrayList<Document>();
	private ArrayList<Document> removeList = new ArrayList<Document>();
	private boolean allowPartialCheckout;
	private String userName;
	private String collection;
	private ArrayList<String> fields = new ArrayList<String>();
	//private UpdateOptions updateOptions;
	private String version;
	private boolean genFlag;
	
	
	
	
	public boolean isGenFlag() {
		return genFlag;
	}

	public void setGenFlag(boolean genFlag) {
		this.genFlag = genFlag;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public ArrayList<Document> getQueryList() {
		return queryList;
	}

	public void addQuery (Document queryDoc) {
		this.queryList.add(queryDoc);
	}

	/*public UpdateOptions getUpdateOptions() {
		return updateOptions;
	}

	public void setUpdateOptions(UpdateOptions updateOptions) {
		this.updateOptions = updateOptions;
	}
*/
	public MongoVCRequest setType(RequestType query){
		this.queryType = query;
		return this;
	}
	
	public MongoVCRequest addDocument(Document doc){
		docList.add(doc);
		return this;
	}
	
	public MongoVCRequest allowPartialCheckout(){
		allowPartialCheckout = true;
		return this;
	}
	
	public MongoVCRequest setUserName(String userName){
		this.userName = userName;
		return this;
	}

	public RequestType getQueryType() {
		return queryType;
	}

	public ArrayList<Document> getDocList() {
		return docList;
	}

	public String getCollection() {
		return collection;
	}

	public void setCollection(String collection) {
		this.collection = collection;
	}

	public boolean isAllowPartialCheckout() {
		return allowPartialCheckout;
	}

	public String getUserName() {
		return userName;
	}
	
	public void setIgnoreField(ArrayList<String> fieldList){
		fields = fieldList;
	}

	public ArrayList<String> getIgnoreFieldList() {
		return fields;
	}
	
	public void removeDoc(Document doc){
		removeList.add(doc);
	}

	public ArrayList<Document> getRemoveList() {
		return removeList;
	}	
}
