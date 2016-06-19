package com.plantiss.mongoadapter.client;

import java.util.ArrayList;

import org.bson.Document;

import com.plantiss.mongoadapter.objects.CheckinOptions;
import com.plantiss.mongoadapter.objects.CheckoutOptions;
import com.plantiss.mongoadapter.objects.MongoVCRequest;
import com.plantiss.mongoadapter.objects.MongoVCResponse;
import com.plantiss.mongoadapter.objects.RequestType;

/**
 * QueryHandler will immitate Collection object.
 * @author Nikhil
 *
 */
public class QueryHandler {

	private String collName;
	private String userName;
	private String serviceHost;
	private int servicePort;
	
	
	
	public QueryHandler(String collName, String userName, String serviceHost,
			int servicePort) {
		super();
		this.collName = collName;
		this.userName = userName;
		this.serviceHost = serviceHost;
		this.servicePort = servicePort;
	}

	/*public void setServiceHost(String serviceHost) {
		this.serviceHost = serviceHost;
	}

	public void setServicePort(int servicePort) {
		this.servicePort = servicePort;
	}

	public void setCollName(String collName) {
		this.collName = collName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}*/

	public void upsertOne(Document search, Document update){
		MongoVCRequest query = new MongoVCRequest();
		query.addQuery(search);
		query.addDocument(update);
		//query.setUpdateOptions(options);
		query.setType(RequestType.UPSERTONE);
		query.setCollection(collName);
		query.setUserName(userName);
		
		MongoVCResponse response = MongoVCClient.requestor(query, serviceHost,servicePort);
		//return response.getUpdateResult();
	}
	
	public ArrayList<Document> find(Document search){
		MongoVCRequest query = new MongoVCRequest();
		query.addQuery(search);
		query.setType(RequestType.FIND);
		query.setCollection(collName);
		query.setUserName(userName);
		
		MongoVCResponse response = MongoVCClient.requestor(query, serviceHost,servicePort);
		return response.getDocList();
	}
	
	public ArrayList<Document> find(Document search, String version,ArrayList<Document> asList){
		MongoVCRequest query = new MongoVCRequest();
		query.addQuery(search);
		query.setType(RequestType.FINDVER);
		query.setCollection(collName);
		query.setUserName(userName);
		query.setVersion(version);
		
		MongoVCResponse response = MongoVCClient.requestor(query, serviceHost,servicePort);
		return response.getDocList();
	}
	
	
	
	public void deleteOne(Document toBeRemoved){
		MongoVCRequest query = new MongoVCRequest();
		query.removeDoc(toBeRemoved);
		query.setType(RequestType.REMOVEONE);
		query.setCollection(collName);
		query.setUserName(userName);
		
		MongoVCResponse response = MongoVCClient.requestor(query, serviceHost,servicePort);
		//return response.getDeleteResult();
	}
	
	// returns back all documents with status
	public ArrayList<Document> checkOut(ArrayList<Document> criteriaList, CheckoutOptions options){
		MongoVCRequest query = new MongoVCRequest();
		for(Document d: criteriaList)
			query.addDocument(d);
		query.setType(RequestType.CHECKOUT);
		query.setCollection(collName);
		query.setUserName(userName);
		if(options.isAllowPartial()) query.allowPartialCheckout();
		
		MongoVCResponse response = MongoVCClient.requestor(query, serviceHost, servicePort);
		return response.getDocList();
	}
	
	public Document checkIn(ArrayList<Document> documentList, ArrayList<Document> toBeRemoved, CheckinOptions options){
		MongoVCRequest query = new MongoVCRequest();
		for(Document d: documentList)
			query.addDocument(d);
		for(Document d : toBeRemoved)
			query.removeDoc(d);
		query.setType(RequestType.CHECKIN);
		query.setCollection(collName);
		query.setUserName(userName);
		if(options.isAllowPartial()) query.allowPartialCheckout();
		query.setIgnoreField(options.getIgnoreFields());
		
		
		MongoVCResponse response = MongoVCClient.requestor(query, serviceHost, servicePort);
		return response.getStatus();
	}
	
	public ArrayList<Document> getCheckinHistory(){
		MongoVCRequest request = new MongoVCRequest();
		request.setUserName(userName);
		request.setType(RequestType.CHECKIN_HISTORY);
		MongoVCResponse response = MongoVCClient.requestor(request, serviceHost, servicePort);
		return response.getDocList();
		
	}
	
	public ArrayList<Document> getCheckinHistory(Document searchCriteria, boolean getIfDeleted){
		MongoVCRequest request = new MongoVCRequest();
		request.setUserName(userName);
		request.addQuery(searchCriteria);
		request.setCollection(collName);
		request.setGenFlag(getIfDeleted);
		request.setType(RequestType.CHECKIN_HISTORY_ONE);
		MongoVCResponse response = MongoVCClient.requestor(request, serviceHost, servicePort);
		return response.getDocList();
	}
}
