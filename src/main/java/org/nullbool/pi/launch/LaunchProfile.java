package org.nullbool.pi.launch;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Bibl (don't ban me pls)
 * @created 26 Jul 2015 12:09:40
 */
@Deprecated
public class LaunchProfile implements Iterable<String> {

	private final List<String> blobs;
	private boolean unresolvedAfter;
	
	public LaunchProfile() {
		this(false);
	}
	
	public LaunchProfile(boolean unresolvedAfter) {
		blobs = new ArrayList<String>();
		this.unresolvedAfter = unresolvedAfter;
	}
	
	public boolean isUnresolvedAfter() {
		return unresolvedAfter;
	}

	public void setUnresolvedAfter(boolean unresolvedAfter) {
		this.unresolvedAfter = unresolvedAfter;
	}

	public void append(String blob) {
		blobs.add(blob);
	}
	
	public void remove(String blob) {
		blobs.add(blob);
	}
	
	public void addStart(String blob) {
		blobs.add(0, blob);
	}
	
	@Override
	public Iterator<String> iterator() {
		return blobs.iterator();
	}
}