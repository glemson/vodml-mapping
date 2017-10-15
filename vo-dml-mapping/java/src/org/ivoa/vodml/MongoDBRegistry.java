package org.ivoa.vodml;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Random;
import java.util.UUID;

import org.ivoa.vodml.RemoteVODMLRegistry;
import org.ivoa.vodml.VODMLRegistry;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.WriteResult;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;

public class MongoDBRegistry implements VODMLRegistry {

	private static MongoDBRegistry instance;
	protected static synchronized MongoDBRegistry getInstance(String host, int port) throws Exception
	{
		if(instance == null)
			instance = new MongoDBRegistry(host, port);
		else if(!instance.host.equals(host) || instance.port != port)
			throw new Exception("A MongoDBHelper was already initialized with different parameters");
		return instance;
	}
	private String host;
	private int port;
	private Mongo mongo;
	private DB mongoDB;
	private Random random = new Random();
	/** fallback remote registry in case a model does not exist in DB */ 
	private RemoteVODMLRegistry urlReg; 
	/** name of collection with IVOA models */
	public static final String IVOA_MODELS_COLLECTION = "ivoa_models"; // TODO tbd if should be made configurable
	
	public MongoDBRegistry(String host, int port) throws Exception {
		this.host = host;
		this.port = port;
		this.mongo = new Mongo(host, port);
		this.mongoDB = this.mongo.getDB("vodmlmapper");
		this.urlReg = new RemoteVODMLRegistry();
	}

	public DBCollection getIVOAModelsCollection(){
		DBCollection col = mongoDB.getCollection(IVOA_MODELS_COLLECTION);
		return col;
	}
	public DBCollection getIVOAModelFiles(){
		DBCollection col = mongoDB.getCollection(IVOA_MODELS_COLLECTION);
		return col;
	}
	/**
	 * Load a model  in MongoDB with gven name and one or more urls that all should represent the same model.
	 * This allows one to recognize different locations and infer they're the same model.
	 * Particularly e.g. for https://... and http://.. with same suffix.
	 * 
	 * @param name
	 * @param urls
	 * @param docURL
	 * @return
	 * @throws Exception
	 */
	public boolean addModel(String name,String[] urls,String docURL) throws Exception {
		// TODO Auto-generated method stub
		BasicDBObject obj = new BasicDBObject();
		obj.put("vodml-id",name);
		DBCollection col = getIVOAModelsCollection();
		GridFS fs = getModelFiles();
		DBObject o = col.findOne(obj);
		if(o != null){
			col.remove(obj); // remove from ivoa_models
			fs.remove(name);
		}
		obj.put("vodml-url",urls);
		obj.put("documentation-url",docURL);
		WriteResult r = col.insert(obj);
		if(r.getError() == null){
			GridFSInputFile f = fs.createFile(new URL(urls[0]).openStream(), name);
			f.setContentType("application/xml");
			f.save();
			f.validate();
		} else 
			return false;
		
		return true;
	}


	public GridFS getModelFiles() {
		return new GridFS(mongoDB, IVOA_MODELS_COLLECTION + ".fs");
	}

	private String currentTime() {
		SimpleDateFormat f = new SimpleDateFormat("yyyyMMdd-HHmmss.sss");
		return f.format(new Date());
	}

	private String randomLabel() {
		return new UUID(random.nextLong(), random.nextLong()).toString();
	}

	public boolean removeModel(String name) {
		try {
			DBCollection col = getIVOAModelsCollection();
			BasicDBObject query = new BasicDBObject();
			query.put("vodml-id",name);
			WriteResult result = col.remove(query);
			GridFS modelFiles = getModelFiles();
			
		} catch (Exception e) {
			return false;
		}
		return true;
	}


	/**
	 * Code to add file to MongoDB from remote URL.
	 * @param name
	 * @param file
	 * @return
	 */
	public boolean putFile(String collectionName, String name, URL url, String contentType ) throws IOException
	{
		GridFS fs = new GridFS(mongoDB, collectionName+".fs");
		GridFSInputFile f = fs.createFile(url.openStream(), name);
		f.setContentType(contentType);
		f.save();
		f.validate();
		return true;
	}
	
	public Mongo getMongo() {
		return mongo;
	}

	public DB getMongoDB() {
		return mongoDB;
	}

	@Override
	public InputStream openModel(String url) throws Exception {
		// TODO Auto-generated method stub
		DBCollection models = getIVOAModelsCollection();
		DBObject model =  models.findOne(new BasicDBObject("vodml-url",url));
		if(model != null) {
		GridFS fs = new GridFS(mongoDB, IVOA_MODELS_COLLECTION + ".fs");
		GridFSDBFile file = fs.findOne((String)model.get("vodml-id"));
		return file==null?null:file.getInputStream();
		} else 
			return urlReg.openModel(url);
	}
}
