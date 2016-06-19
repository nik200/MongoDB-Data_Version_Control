import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import org.bson.Document;
import org.bson.json.JsonWriterSettings;

import com.plantiss.mongoadapter.client.MongoVCClient;
import com.plantiss.mongoadapter.client.NotificationListener;
import com.plantiss.mongoadapter.client.QueryHandler;
import com.plantiss.mongoadapter.objects.CheckinOptions;
import com.plantiss.mongoadapter.objects.CheckoutOptions;
import com.plantiss.mongoadapter.objects.Notification;
import com.plantiss.mongoadapter.objects.NotificationReciever;
import com.plantiss.mongoadapter.server.DBConnection;
import com.plantiss.mongoadapter.server.NotificationHandler;
import com.plantiss.mongoadapter.server.ReadHandler;


public class Test implements NotificationReciever{


	public static void main(String[] args) throws ClassNotFoundException, IOException, InterruptedException {
		QueryHandler handler = new QueryHandler("objects","nk123456","localhost",25000);

		//handler.setServiceHost("107.180.74.131");

		
		NotificationListener notifier = new NotificationListener("nk123456","localhost",25000);
		//notifier.register(new Test());
		
		
		
		ArrayList<Document> checkout = new ArrayList<Document>();
		ArrayList<Document> checkin = new ArrayList<Document>();
		ArrayList<Document> checkinremove = new ArrayList<Document>();
		
		CheckoutOptions optionsco = new CheckoutOptions();
		optionsco.setAllowPartial(true);

		CheckinOptions optionsci = new CheckinOptions();
		optionsci.setAllowPartial(true);
		
		checkout.add(new Document("_id","ID001"));
		//checkout.add(new Document("_id","ID005"));
		//checkout.add(new Document("_id","ID004"));
		//System.out.println(handler.checkOut(checkout, optionsco));
		
		
		ArrayList<String> ignore = new ArrayList<String>();
		//ignore.add("name");
		optionsci.setIgnoreFields(ignore);
		checkin.add(new Document("_id","ID001").append("name", "one25"));
		//checkinremove.add(new Document("_id","ID005"));
		//checkin.add(new Document("_id","ID006").append("name", "six2"));
		//checkinremove.add(new Document("_id","ID004"));
		//checkin.add(new Document("_id","ID005").append("name", "five"));
		
		
		//handler.checkIn(checkin, checkinremove, optionsci);
		//for(Document d:handler.find(new Document(), "155633bbc57", null))
		//System.out.println(d.toJson(new JsonWriterSettings(true)));
		
		/*handler.deleteOne(new Document("_id","1"));
		handler.deleteOne(new Document("name","nikhil"));*/
		//Thread.sleep(60000);
		//notifier.unregister();
		
		
		for(Document d:handler.getCheckinHistory(new Document("_id","ID001"), false))
		//for(Document d:handler.find(new Document()))
			System.out.println(d.toJson(new JsonWriterSettings(true)));
		
		
		
	}

	@Override
	public void notify(Notification notification) {
		System.out.println("Notification recieved !!!!"+notification.getModifiedObjects());
		
	}
}
