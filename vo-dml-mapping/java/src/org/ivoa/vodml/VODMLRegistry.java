package org.ivoa.vodml;

import java.io.InputStream;

public interface VODMLRegistry {

	public InputStream openModel(String url) throws Exception;
}
