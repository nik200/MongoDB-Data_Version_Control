package com.plantiss.mongoadapter.server;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import com.plantiss.mongoadapter.objects.Notification;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class CheckinHandler {

	public static synchronized Document checkin(String col, ArrayList<Document> docList,
			ArrayList<Document> removeList,
			ArrayList<String> ignoreFields,
			boolean allowPartial, String userName) throws Exception{
		
		ArrayList<Document> list1 = new ArrayList<Document>(); // can be checked-in
		ArrayList<Document> list2 = new ArrayList<Document>(); // does not require check-in
		ArrayList<Document> list3 = new ArrayList<Document>(); // cannot be checked-in/removed
		ArrayList<Document> list4 = new ArrayList<Document>(); // can be removed
		
		Notification checkinNotif = new Notification();
		
		
		// create deep copy of documents to be checked-in
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bos);
		oos.writeObject(docList);
		oos.flush();
		ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
		ObjectInputStream ois = new ObjectInputStream(bis);
		ArrayList<Document> inList = (ArrayList<Document>) ois.readObject();
		
		
		// segregate documents in list1,2,3,4
		for(int i=0;i<inList.size();i++){
			Document inDoc = inList.get(i);
			Document fromDB = getDocument(col, inDoc.getString("_id"));
			// remove fields which need not be compared
			// currently only top level fields are supported
			
			if(fromDB==null){
				// this is a new doc. No comparison needed.
				list1.add(docList.get(i));
				continue;
			}
			String user = fromDB.getString("chkoutBy");
			
			ignoreFields.add("version");
			ignoreFields.add("chkoutBy");
			ignoreFields.add("chckoutTime");
			
			for(String s:ignoreFields){
				inDoc.remove(s);
				fromDB.remove(s);
			}
			
			if(inDoc.equals(fromDB)) list2.add(docList.get(i));
			else if(user.equals(userName)) list1.add(docList.get(i));
			else list3.add(docList.get(i));
		}
		
		for(int i=0;i<removeList.size();i++){
			Document inDoc = removeList.get(i);
			Document fromDB = getDocument(col, inDoc.getString("_id"));
			if(fromDB==null){
				// new doc
				list3.add(removeList.get(i));
				continue;
			}
			String user = fromDB.getString("chkoutBy");
			if(user.equals(userName)) list4.add(removeList.get(i));
			else list3.add(removeList.get(i));
		}
		
		if(!allowPartial && list3.size()!=0){
			return null;
		}else{
			// generate check-in ID
			Document vd = generateVersionDoc(col, userName);
			MongoCollection<Document> c = DBConnection.getCollection(col);
			
			// check-in all from list1
			for(Document newDoc: list1){
				
				newDoc.append("version", vd);
				newDoc.append("chkoutBy", "na");
				
				
				Document oldDoc = c.findOneAndReplace(new Document("_id", newDoc.getString("_id"))
								, newDoc.append("version", vd), new FindOneAndReplaceOptions().upsert(true));
				if(oldDoc!=null){
					
					oldDoc.append("_id",
							new Document().append("_id", newDoc.getString("_id"))
							.append("versionId", (vd.getString("_id"))));//((Document)oldDoc.get("version")).getString("_id")));
					DBConnection.getArchiveCollection(col).insertOne(oldDoc);
					// updating notification object
					checkinNotif.addModifiedObjectID(newDoc.getString("_id"));
				}
			}
			
			// remove all elements of list4
			for(Document tbr: list4){
				Document oldDoc = c.findOneAndDelete(new Document("_id", tbr.getString("_id")));
				oldDoc.append("_id",
						new Document().append("_id", oldDoc.getString("_id"))
						.append("versionId", (vd.getString("_id"))));
				
				//oldDoc.append("version", vd);
				oldDoc.append("chkoutBy", "na");
				oldDoc.remove("chckoutTime");
				
				DBConnection.getArchiveCollection(col).insertOne(oldDoc);
				// updating notification object
				checkinNotif.addRemovedObjectID(tbr.getString("_id"));
			}
			
			// reset checkout status of all those in list2
			for(Document noChnge: list2){
				DBConnection.getCollection(col).updateOne(new Document("_id",noChnge.getString("_id"))
				, new Document("$set", new Document("chkoutBy", "na"))
				.append("$unset", new Document("chckoutTime","")));
			}
			
			DBConnection.getCollection("verdata").insertOne(vd);
			checkinNotif.setCheckinID(vd.getString("_id"));
			checkinNotif.setTimestamp(vd.getLong("timestamp"));
			
		}
		// send notifications
		checkinNotif.setUserID(userName);
		checkinNotif.setCollection(col);
		checkinNotif.setDb(DBConnection.getDBName());
		NotificationHandler.sendNotification(checkinNotif);
		
		
		return new Document().append("status", "success");
	}

	public static Document getDocument(String col, String id){
		return DBConnection.getCollection(col).find(new Document("_id",id)).first();
	}
	
	public static Document generateVersionDoc(String col, String userName){
		Document doc = new Document();
		String versionID = Long.toHexString(System.currentTimeMillis());
		doc.append("_id", versionID);
		doc.append("collection", col);
		doc.append("userName", userName);
		doc.append("timestamp", System.currentTimeMillis());
		return doc;
	}
}
