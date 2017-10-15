package org.ivoa.vodml;

import java.io.InputStream;
import java.net.URL;

public class RemoteVODMLRegistry implements VODMLRegistry {

	@Override
	public InputStream openModel(String url) throws Exception{
		// TODO Auto-generated method stub
		return new URL(url).openStream();
	}
	public RemoteVODMLRegistry() {
		
	}

}
