package org.nullbool.pi.installer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author Bibl (don't ban me pls)
 * @created 25 Jul 2015 13:54:49
 */
public class Util {

	public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	
	public static String require(Map<String, String> flags, String key) {
		if(!flags.containsKey(key))
			throw new IllegalArgumentException("no flag: " + key);
		String val = flags.get(key);
		if(val == null)
			throw new IllegalArgumentException("null flag: " + key);
		
		return val;
	}
	
	public static byte[] read(int maxBuffSize, InputStream is) throws IOException {
		byte[] buff = new byte[maxBuffSize];
		int read;
		ByteArrayOutputStream boas = new ByteArrayOutputStream();
		while((read = is.read(buff)) > 0) {
			boas.write(buff, 0, read);
		}
		return boas.toByteArray();
	}

	// http://stackoverflow.com/questions/6293713/java-how-to-create-sha-1-for-a-file
	public static String sha1(InputStream is) throws Exception  {
		MessageDigest digest = MessageDigest.getInstance("SHA-1");
		int n = 0;
		byte[] buffer = new byte[8192];
		while (n != -1) {
			n = is.read(buffer);
			if (n > 0) {
				digest.update(buffer, 0, n);
			}
		}
		is.close();
		byte[] b = digest.digest();
		StringBuffer sb = new StringBuffer("");
	    for (int i = 0; i < b.length; i++) {
	    	sb.append(Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1));
	    }
	    return sb.toString();
	}
	
	public static Set<File> lst(File dir, String suffix) {
		Set<File> set = new HashSet<File>();
		for(File f : dir.listFiles()) {
			if(f.isDirectory()) {
				set.addAll(lst(f, suffix));
			} else if(f.getAbsolutePath().endsWith(suffix)){
				set.add(f);
			}
		}
		return set;
	}
}