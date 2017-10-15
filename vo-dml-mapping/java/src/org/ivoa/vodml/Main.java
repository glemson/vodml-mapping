package org.ivoa.vodml;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import javax.xml.bind.JAXB;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		String url = "http://volute.g-vo.org/svn/trunk/projects/dm/vo-dml/models/source/SourceDM.vo-dml.xml";
		MongoDBRegistry m = new MongoDBRegistry("localhost", 27017);
		VODMLManager vodml = new VODMLManager(m, url);
//		VODMLManager vodml = new VODMLManager(new RemoteVODMLRegistry(), url);
// TODO do something
		
	}

}
