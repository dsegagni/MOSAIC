package it.fsm.mosaic.mongodb;


import it.fsm.mosaic.model.CacheMongoObject;

import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import org.bson.types.ObjectId;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.util.JSON;

public class MongoDbUtil {
	
	public CacheMongoObject insertObj(String obj){
		ObjectId id = null;
		MongoClient mc = null;
		CacheMongoObject cacheMOResult = new CacheMongoObject();
		
		try {
	        
			mc = new MongoClient("10.7.59.104");
			DB mosaicDB = mc.getDB("mosaic");
			DBCollection coll = mosaicDB.getCollection("process");
			DBObject bson = ( DBObject ) JSON.parse(obj);
			
			//adding MD5 hash as root attribute
			byte[] bytesOfMessage = obj.getBytes("UTF-8");

			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] thedigest = md.digest(bytesOfMessage);
			
			bson.put("jsonMD5", new String(thedigest));
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss.SSS");	
			
			bson.put("request_time", df.format(new java.util.Date()));
			
			BasicDBObject whereQuery = new BasicDBObject();
			System.out.println("MONGO DB insert obj "+new String(thedigest));
			whereQuery.put("jsonMD5", new String(thedigest));
			//whereQuery.put("concept", "LOC");
			DBCursor cursor = coll.find(whereQuery);
			boolean found=false;
			while(cursor.hasNext()) {
			    found=true;
			    DBObject o = cursor.next();
			    cacheMOResult.setMongoObj(o);
                String results = (String) o.get("results") ; 
                if(results !=null){
                	cacheMOResult.setStatus(2);
                }else{
                	cacheMOResult.setStatus(1);
                }
			}
			if(!found){
				coll.insert(bson);
				System.out.println("MONGO DB insert obj");
				id = (ObjectId) bson.get("_id");
				 cacheMOResult.setMongoObj(bson);
				 cacheMOResult.setStatus(0);
			}
			
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			if(mc!=null){
				mc.close();
			}
		}
		
		return cacheMOResult;
		
	}

}
