package com.plantiss.mongoadapter.server;

import java.util.ArrayList;

import org.bson.Document;

public class CheckoutHandler {

	public static synchronized ArrayList<Document> checkout(String col,
			ArrayList<Document> queryList, String userName,
			boolean allowPartial){
		/*if(queryList.size()==0){
			
			return new ArrayList<Document>();
		}*/
		
		ArrayList<Document> list = query(col,queryList);
		ArrayList<Document> returnVal1 = new ArrayList<Document>();
		ArrayList<Document> returnVal2 = new ArrayList<Document>();
		
		for(Document d: list){
			if(d.getString("chkoutBy").equals(userName)||
					d.getString("chkoutBy").equals("na")){
				returnVal1.add(d);
			}else{
				returnVal2.add(d);
			}
		}
		
		if(!allowPartial && returnVal2.size()>0){
			returnVal1.removeAll(returnVal1);
			returnVal1.add(0,new Document().append("status", "failed"));
			return returnVal1;
		}else{
			// update all docs in list1
			long checkoutTime = System.currentTimeMillis();
			
			for(Document d: returnVal1){
				d.append("chckoutTime", checkoutTime)
				.append("chkoutBy", userName);
				updateChkOutStatus(col, d.getString("_id"), userName, checkoutTime);
				
				// remove version control meta data
				d.remove("chckoutTime");
				if(!allowPartial) d.remove("chkoutBy");
				d.remove("version");
				
			}
			
			for(Document d: returnVal2){
				// remove version control meta data
				d.remove("chckoutTime");
				if(!allowPartial) d.remove("chkoutBy");
				d.remove("version");
				
			}
			// add list1+list2
			returnVal1.addAll(returnVal2);
			
			returnVal1.add(0,new Document().append("status", "success"));
			
			return returnVal1;
		}
	}
	
	public static synchronized void updateChkOutStatus(String col, String id, String userName, long checkoutTime){
		DBConnection.getCollection(col).updateOne(new Document("_id",id), 
				new Document("$set", new Document()
									.append("chckoutTime", checkoutTime)
									.append("chkoutBy", userName)));
		
	}
	
	public static synchronized ArrayList<Document> query(String col,ArrayList<Document> queryList){
		ArrayList<Document> returnVal = new ArrayList<Document>();
		for(Document query:queryList){
			DBConnection.getCollection(col).find(query).into(returnVal);
		}
		return returnVal;
	}
	
	
	
}
