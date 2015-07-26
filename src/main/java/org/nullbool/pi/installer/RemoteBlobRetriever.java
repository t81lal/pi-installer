package org.nullbool.pi.installer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

/**
 * @author Bibl (don't ban me pls)
 * @created 25 Jul 2015 13:51:43
 */
public class RemoteBlobRetriever implements BlobRetriever<RemoteBlob> {

	private final URL url;
	
	public RemoteBlobRetriever(URL url) {
		this.url = url;
	}

	@Override
	public URL getBlockSheetURL() {
		return url;
	}
	
	@Override
	public Collection<RemoteBlob> get() throws IOException {
		StringBuilder sb = new StringBuilder();
		
		URLConnection con = url.openConnection();
		BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String tmp;
		while((tmp = br.readLine()) != null) {
			sb.append(tmp).append('\n');
		}
		br.close();
		
		RemoteBlob[] blobs = Util.GSON.fromJson(sb.toString(), RemoteBlob[].class);
		return new HashSet<RemoteBlob>(Arrays.asList(blobs));
	}
}