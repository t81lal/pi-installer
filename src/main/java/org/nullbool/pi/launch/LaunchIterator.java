package org.nullbool.pi.launch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.nullbool.pi.installer.RemoteBlob;

/**
 * @author Bibl (don't ban me pls)
 * @created 26 Jul 2015 12:14:16
 */
@Deprecated
public class LaunchIterator implements Iterable<RemoteBlob>{

	private final List<RemoteBlob> resolved;
	
	public LaunchIterator(RemoteBlob[] blobs, LaunchProfile profile) {
		resolved = new ArrayList<RemoteBlob>();
		resolve(blobs, profile);
	}
	
	private void resolve(RemoteBlob[] blobs, LaunchProfile profile) {
		Map<String, RemoteBlob> mapped = new HashMap<String, RemoteBlob>();
		for(RemoteBlob b : blobs) {
			mapped.put(b.getId(), b);
		}
		if(mapped.size() != blobs.length) {
			throw new IllegalStateException("Blob id collisions.");
		}
		
		for(String s : profile) {
			RemoteBlob b = mapped.remove(s);
			if(b == null) {
				throw new IllegalArgumentException("No blob for id: " + s);
			}
			resolved.add(b);
		}
		
		if(mapped.size() > 0) {
			System.out.println("Unresolved: " + mapped);
			if(profile.isUnresolvedAfter()) {
				resolved.addAll(mapped.values());
			} else {
				resolved.addAll(0, mapped.values());
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<RemoteBlob> iterator() {
		return null;
	}
}