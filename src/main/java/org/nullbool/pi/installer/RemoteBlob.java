package org.nullbool.pi.installer;

import java.io.InputStream;
import java.net.URL;

/**
 * @author Bibl (don't ban me pls)
 * @created 25 Jul 2015 13:44:30
 */
public class RemoteBlob implements Blob {

	private final String base;
	private final String id;
	private final String hash;
	private final URL url;

	public RemoteBlob(String base, String id, String hash, URL url) {
		this.base = base;
		this.id = id;
		this.hash = hash;
		this.url = url;
	}
	
	public String getBase() {
		return base;
	}

	public String getId() {
		return id;
	}

	public String getHash() {
		return hash;
	}

	@Override
	public URL getURL() {
		return url;
	}
	
	@Override
	public boolean verify(InputStream is) {
		try {
			String hash = Util.sha1(is);
			boolean b = this.hash.equals(hash);
			if(!b) {
				System.out.printf("Hash mismatch: (local:%s) (remote:%s).%n", hash, this.hash);
			}
			return b;
		} catch (Exception e) {
			System.out.println("Erroenous local block.");
			e.printStackTrace();
			return false;
		}	
	}

	@Override
	public String toString() {
		return id;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((hash == null) ? 0 : hash.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		RemoteBlob other = (RemoteBlob) obj;

		/*if (dataURL == null) {
			if (other.dataURL != null)
				return false;
		} else if (!dataURL.equals(other.dataURL))
			return false;*/

		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;

		if (hash == null) {
			if (other.hash != null)
				return false;
		} else if (!hash.equals(other.hash))
			return false;
		return true;
	}
}