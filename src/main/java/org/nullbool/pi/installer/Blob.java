package org.nullbool.pi.installer;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * @author Bibl (don't ban me pls)
 * @created 25 Jul 2015 13:48:58
 */
public interface Blob {

	public URL getURL();
	
	public boolean verify(InputStream is) throws IOException;
}