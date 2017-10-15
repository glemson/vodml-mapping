package org.ivoa.vodml;

import java.io.File;
import java.net.URL;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.WriteResult;

public class MongoMain {

	private MongoDBRegistry vodmlRegistry;
	private Mongo mongo;
	private DB mongodb;

	public MongoMain(String mongoHost, int mongoPort) throws Exception{
		vodmlRegistry = new MongoDBRegistry(mongoHost, mongoPort);
		mongo = vodmlRegistry.getMongo();
		mongodb = vodmlRegistry.getMongoDB();
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		MongoMain m = null;
		try {
		m = new MongoMain("localhost", 27017);
//		m = new MongoMain("galgate.rzg.mpg.de", 27017);
		
//		m.addUser("gerard", "Gerard Lemson", "lemson@mpa-garching.mpg.de");
//		m.addUser("omar", "Omar Laurino", "olaurino@head.cfa.harvard.edu");
		m.addStandardModels();		
//		m.name2vodmlid();
	
		} catch(Exception e){
			e.printStackTrace();
		} finally{
			if(m != null)
				m.close();
		}
	}
	public void close() {
		mongo.close();
	}
	public void addStandardModels() throws Exception
	{
		vodmlRegistry.addModel("ivoa", 
				new String[]{"https://volute.g-vo.org/svn/trunk/projects/dm/vo-dml/models/ivoa/IVOA.vo-dml.xml",
			      "http://volute.g-vo.org/svn/trunk/projects/dm/vo-dml/models/ivoa/IVOA.vo-dml.xml",
					"http://localhost:8080/VODML-Mapper/vo-dml/IVOA.vo-dml.xml"
				},
				"https://volute.g-vo.org/svn/trunk/projects/dm/vo-dml/models/ivoa/IVOA.html");
		vodmlRegistry.addModel("vo-dml", 
				new String[]{
					"http://localhost:8080/VODML-Mapper/vo-dml/VO-DML.vo-dml.xml",
					"https://volute.g-vo.org/svn/trunk/projects/dm/vo-dml/models/vo-dml/VO-DML.vo-dml.xml", 
					"http://volute.g-vo.org/svn/trunk/projects/dm/vo-dml/models/vo-dml/VO-DML.vo-dml.xml"
				},
				"https://volute.g-vo.org/svn/trunk/projects/dm/vo-dml/models/vo-dml/VO-DML.html");
		vodmlRegistry.addModel("photdm-alt", 
				new String[]{"https://volute.g-vo.org/svn/trunk/projects/dm/vo-dml/models/photdm-alt/PhotDM-alt.vo-dml.xml",
					"https://volute.g-vo.org/svn/trunk/projects/dm/vo-dml/models/photdm-alt/PhotDM-alt.vo-dml.xml",
					"http://localhost:8080/VODML-Mapper/vo-dml/PhotDM-alt.vo-dml.xml"
				},
				"https://volute.g-vo.org/svn/trunk/projects/dm/vo-dml/models/photdm-alt/PhotDM-alt.html");
		vodmlRegistry.addModel("src",
				new String[]{"https://volute.g-vo.org/svn/trunk/projects/dm/vo-dml/models/source/SourceDM.vo-dml.xml",
				"https://volute.g-vo.org/svn/trunk/projects/dm/vo-dml/models/source/SourceDM.vo-dml.xml",
				"http://localhost:8080/VODML-Mapper/vo-dml/SourceDM.vo-dml.xml"
				},
				"https://volute.g-vo.org/svn/trunk/projects/dm/vo-dml/models/source/SourceDM.html");
	}
	/**
	 * transform the models.name variable to be vodml-id in al collections.
	 * 
	 * @return
	 */
	public boolean addUser(String username, String partyName, String email)
	{
		BasicDBObject obj = new BasicDBObject();
		obj.put("username",username);
		DBCollection col = mongodb.getCollection("users");
		DBObject o = col.findOne(obj);
		if(o != null){
			System.out.printf("User with name '%s' already exists",username);
			return false;
		}
		obj.put("partyName",partyName);
		obj.put("email",email);
		WriteResult r = col.insert(obj);
		return r.getError() == null;
	}
	public boolean updateURL(String modelName, String newURL, String docURL)
	{
		BasicDBObject obj = new BasicDBObject();
		obj.put("vodml-id",modelName);
		DBCollection col = vodmlRegistry.getIVOAModelsCollection();
		DBObject o = col.findOne(obj);
		o.removeField("vodml-url");
		o.put("vodml-url", newURL);
		o.removeField("documentation-url");
		o.put("documentation-url", docURL);
		WriteResult r = col.update(obj, o);
		boolean ok = r.getError() == null;
		return ok;
	}

}
