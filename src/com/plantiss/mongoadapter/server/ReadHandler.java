package com.plantiss.mongoadapter.server;

import java.util.ArrayList;
import org.bson.Document;

public class ReadHandler {
	/**
	 * Method to return all the documents for a checkin version
	 * @param col
	 * @param queryDoc
	 * @param version
	 * @return
	 */
	public static synchronized ArrayList<Document> find(String col, Document queryDoc,
			String version){
		
		ArrayList<Document> res = null;
	
		// Find all objects from current collection with version <= requested version

		res = (DBConnection.getCollection(col).find(queryDoc.append("version._id", new Document("$lte",version))).into(new ArrayList<Document>()));
		
		// adjust query to search over the archive collection
		if(queryDoc.get("_id")!=null){
			queryDoc.append("_id._id", queryDoc.get("_id"));
			queryDoc.remove("_id");
		}
		
		// get all relevant docs from archive	
		for(Document d:DBConnection.getArchiveCollection(col).find(queryDoc.append("version._id", new Document("$lte",version)).append("_id.versionId", new Document("$gt", version))).into(new ArrayList<Document>())){
			//System.out.println(d.toJson(new JsonWriterSettings(true)));
			String id = ((Document)d.get("_id")).getString("_id");
			d.append("_id", id);
			res.add(d);
		}
		
		// remove metadata fields
		for(Document d: res){
			d.remove("chckoutTime");
			d.remove("chkoutBy");
			d.remove("version");
		}
			
		
		return res;
	}
}
