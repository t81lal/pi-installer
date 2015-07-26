package org.nullbool.pi.installer;

import java.awt.Image;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.HashSet;
import java.util.Set;

import javax.swing.ImageIcon;

public class IOHelper {

	public static abstract class FileFilter {
		public abstract boolean accept(File f);
	}
	
	public static Set<File> lstRec(File dir, FileFilter filter) {
		Set<File> set = new HashSet<File>();
		for(File f : dir.listFiles()) {
			if(f.isDirectory()) {
				if(filter.accept(f))
					set.add(f);
			} else {
				set.addAll(lstRec(f, filter));
			}
		}
		return set;
	}
	
	public static Set<File> lstFlat(File dir, FileFilter filter) {
		Set<File> set = new HashSet<File>();
		for(File f : dir.listFiles()) {
			if(filter.accept(dir))
				set.add(f);
		}
		return set;
	}
	
	public static URL makeURL(String url) throws IOException {
		return new URL(url);
	}

	public static URL makeURL(URL url, String next) throws IOException {
		return new URL(url.toExternalForm() + next);
	}

	public static URL safe_makeURL(String url) {
		try {
			return new URL(url);
		} catch (IOException e) {
			return null;
		}
	}

	public static URL safe_makeURL(URL url, String next) {
		try {
			return new URL(url.toExternalForm() + next);
		} catch (IOException e) {
			return null;
		}
	}

	public static File checkmkdir(String dir) {
		return checkmkdir(new File(dir));
	}

	public static File checkmkdir(File dir) {
		if (dir.exists() && dir.isFile())
			dir.delete();
		if (!dir.exists())
			dir.mkdir();
		return dir;
	}

	public static File checkmkfile(String dir) {
		return checkmkfile(new File(dir));
	}

	public static File checkmkfile(File f) {
		if (f.exists() && f.isDirectory())
			f.delete();
		if (!f.exists())
			try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		return f;
	}

	public static String read(InputStream is) throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		int read;
		byte[] data = new byte[16384];
		while ((read = is.read(data, 0, data.length)) != -1) {
			buffer.write(data, 0, read);
		}
		buffer.flush();
		byte[] dataRead = buffer.toByteArray();
		return new String(dataRead);
	}

	public static String read(File f) throws IOException {
		try (BufferedReader br = new BufferedReader(new FileReader(f))) {
			String s = "";
			StringBuilder sb = new StringBuilder();
			while ((s = br.readLine()) != null) {
				sb.append(s).append("\n");
			}
			return sb.toString();
		}
	}

	public static void write(File file, String data) throws IOException {
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
			bw.write(data);
		}
	}

	public static void copy(File sourceFile, File destFile) throws IOException {
		if (!destFile.exists())
			destFile.createNewFile();

		FileChannel source = null;
		FileChannel destination = null;
		try {
			source = new FileInputStream(sourceFile).getChannel();
			destination = new FileOutputStream(destFile).getChannel();
			destination.transferFrom(source, 0, source.size());
		} finally {
			if (source != null)
				source.close();
			if (destination != null)
				destination.close();
		}
	}

	public static String getDigest(File file, String algorithm, int hashLength) {
		try {
			return getDigest(new FileInputStream(file), algorithm, hashLength);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String getDigest(InputStream is, String algorithm, int hashLength) {
		DigestInputStream stream = null;
		try {
			stream = new DigestInputStream(is, MessageDigest.getInstance(algorithm));
			byte[] buffer = new byte[65536];
			int read;
			do {
				read = stream.read(buffer);
			} while (read > 0);
		} catch (Exception ignored) {
			return null;
		} finally {
			try {
				stream.close();
			} catch (IOException e) {
			}
		}
		return String.format("%1$0" + hashLength + "x", new Object[] { new BigInteger(1, stream.getMessageDigest().digest()) });
	}

	public static Image getImage(String path) {
		URL url = IOHelper.class.getResource("/images/" + path);
		return new ImageIcon(url).getImage();
	}

	public static void download(URL url, File file) throws IOException {
		if(file.exists())
			file.delete();
		ReadableByteChannel rbc = Channels.newChannel(url.openStream());
		FileOutputStream fos = new FileOutputStream(file);
		fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
		fos.close();
	}

	public static void safe_download(URL url, File file) {
		try {
			download(url, file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}