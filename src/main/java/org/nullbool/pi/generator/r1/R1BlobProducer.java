package org.nullbool.pi.generator.r1;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import org.nullbool.pi.generator.BlobProducer;
import org.nullbool.pi.installer.Blob;
import org.nullbool.pi.installer.RemoteBlob;
import org.nullbool.pi.installer.Util;

/**
 * @author Bibl (don't ban me pls)
 * @created 25 Jul 2015 14:26:33
 */
public class R1BlobProducer implements BlobProducer {

	@Override
	public Blob produce(Map<String, String> config) {
		String base = Util.require(config, "base");
		String id = Util.require(config, "id");
		String hash = Util.require(config, "hash");
		String url = Util.require(config, "url");
		
		try {
			return new RemoteBlob(base, id, hash, new URL(url));
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}
	}
}