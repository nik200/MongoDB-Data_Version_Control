package com.plantiss.mongoadapter.server;

import java.util.ArrayList;

import org.bson.Document;
import org.omg.CORBA.Request;

import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.plantiss.mongoadapter.objects.MongoVCRequest;
import com.plantiss.mongoadapter.objects.MongoVCResponse;

public class DBRequestHandler {

	public static MongoVCResponse queryProcessor(MongoVCRequest query) throws Exception{
		MongoVCResponse response = new MongoVCResponse();

		switch(query.getQueryType()){
		case CHECKIN:
			response.setStatus(CheckinHandler.checkin(query.getCollection(), 
					query.getDocList(), 
					query.getRemoveList(),
					query.getIgnoreFieldList(), 
					query.isAllowPartialCheckout(), 
					query.getUserName()));
			break;

		case CHECKOUT:
			response.setDocList(CheckoutHandler.checkout(query.getCollection(), 
					query.getDocList(), 
					query.getUserName(), 
					query.isAllowPartialCheckout()));
			response.setStatus(response.getDocList().get(0));
			response.getDocList().remove(0);
			break;

		case FIND:
			response.setDocList(DBConnection.getCollection(query.getCollection())
					.find(query.getQueryList().get(0))
					.into(response.getDocList()));
			
			// remove metadate fields
			for(Document d: response.getDocList()){
				d.remove("chckoutTime");
				d.remove("chkoutBy");
				d.remove("version");
			}
			
			response.setStatus(new Document("status", "success"));
			break;

		case REMOVEONE:
			DBConnection.getCollection(query.getCollection()).deleteOne(query.getRemoveList().get(0));
			response.setStatus(new Document("status", "success"));
			break;

		case UPSERTONE:
			DBConnection.getCollection(query.getCollection()).updateOne(query.getQueryList().get(0)
					, query.getDocList().get(0)
					, new UpdateOptions().upsert(true));
			response.setStatus(new Document("status", "success"));
			break;

		case FINDVER:
			response.setDocList(ReadHandler.find(query.getCollection()
					, query.getQueryList().get(0)
					, query.getVersion()));
			response.setStatus(new Document("status", "success"));
			break;

		case CHECKIN_HISTORY:
			ArrayList<Document> aggPipe = new ArrayList<Document>();
			response.setDocList(DBConnection.getCollection("verdata").find().sort(new Document("_id",-1)).into(new ArrayList<Document>()));
			response.setStatus(new Document("status", "success"));
			break;

		case CHECKIN_HISTORY_ONE:
			Document queryDoc = query.getQueryList().get(0);
			Document d = DBConnection.getCollection(query.getCollection()).
			find(queryDoc).first();
			
			ArrayList<Document> oldVersions = new ArrayList<Document>();
			ArrayList<String> versionIds = new ArrayList<String>();
			
			//oldVersions
			if(d!=null){
				DBConnection.getArchiveCollection(query.getCollection()).
				find(new Document("_id._id", d.getString("_id"))).into(oldVersions);
				System.out.println("Document forund in current collections");
			}else{
				// if d = null i.e. object has been deleted and only archived copies are present
				if(!query.isGenFlag()){
					response.setStatus(new Document("status", "success"));
					//System.out.println("Object is deleted. Only archived copies present. Aborting");
					break;
				}
				// adjust query to search over the archive collection
				if(queryDoc.get("_id")!=null){
					queryDoc.append("_id._id", queryDoc.get("_id"));
					queryDoc.remove("_id");
				}
				d = DBConnection.getArchiveCollection(query.getCollection()).
				find(new Document(queryDoc)).first();
				DBConnection.getCollection(query.getCollection()).
				find(new Document("_id._id", ((Document)d.get("_id")).getString("_id"))).into(oldVersions);
			}
			
			versionIds.add(((Document)d.get("version")).getString("_id"));
			for(Document doc:oldVersions){
				versionIds.add(((Document)doc.get("version")).getString("_id"));
			}
			
			response.setDocList(DBConnection.getCollection("verdata").find(new Document("_id", new Document("$in", versionIds))).sort(new Document("_id",-1)).into(new ArrayList<Document>()));
			response.setStatus(new Document("status", "success"));
			break;
		}

		return response;

	}
}
