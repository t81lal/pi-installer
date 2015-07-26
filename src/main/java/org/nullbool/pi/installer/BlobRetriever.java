package org.nullbool.pi.installer;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;

/**
 * @author Bibl (don't ban me pls)
 * @created 25 Jul 2015 13:48:29
 */
public interface BlobRetriever<T extends Blob> {

	public URL getBlockSheetURL();
	
	public Collection<T> get() throws IOException;
}