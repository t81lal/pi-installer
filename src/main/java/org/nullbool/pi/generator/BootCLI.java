package org.nullbool.pi.generator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.commons.exec.CommandLine;
import org.nullbool.pi.generator.r1.R1BlobProducer;
import org.nullbool.pi.installer.Blob;
import org.nullbool.pi.installer.Util;

/**
 * @author Bibl (don't ban me pls)
 * @created 25 Jul 2015 14:29:59
 */
public class BootCLI {

	private static BlobProducer producer;
	
	public static void main(String[] args) {
		try {
			Scanner scanner = new Scanner(System.in);

			System.out.println("Default producer?");
			
			String line = scanner.nextLine();
			if(line.toLowerCase().startsWith("y")) {
				producer = new R1BlobProducer();
			} else {
				System.out.println("Producer class: ");
				line = scanner.nextLine();
				producer = (BlobProducer) Class.forName(line).newInstance();
			}
			
			System.out.println("Started");

			line = null;
			do {
				try {
					process(line);
				} catch(Throwable t) {
					t.printStackTrace();
				}
			} while((line = scanner.nextLine()) != null);

			scanner.close();

		} catch(Throwable t) {
			System.err.println("Fatal error: " + t.getMessage());
			t.printStackTrace();
		}
	}

	private static void process(String line) throws Throwable{
		if (line == null || line.isEmpty())
			return;
		
		CommandLine cmdl = CommandLine.parse(line);
		String cmd = cmdl.getExecutable();
				
		if (cmd.equals("help")) {
			System.out.println("This is a debug tool.");
			return;
		}

		if (cmd.equals("gen")) {
			// FIXME:
			// id, local file, remote url
			String[] parts = args(cmdl);
			if (parts.length != 3) {
				System.err.println("usage: gen <id> <local> <remote>");
			} else {
				String id = parts[0];
				String _local = parts[1];
				File local = new File(_local);
				if (!local.exists()) {
					System.err.println(_local + " doesn't exist.");
					return;
				}
				String remote = parts[2];
				Map<String, String> config = new HashMap<String, String>();
				config.put("id", id);
				config.put("hash", Util.sha1(new FileInputStream(local)));
				config.put("remote", remote);
			}
			return;
		}
		
		if(cmd.equals("merge")) {
			String[] parts = args(cmdl);
			if (parts.length != 2) {
				System.err.println("usage: merge <insheet> <outsheet>");
			} else {
				File in = new File(parts[0]);
				File out = new File(parts[1]);
				byte[] buffer = new byte[1024 * 16];
				try(FileInputStream fis = new FileInputStream(in);
					FileOutputStream fos = new FileOutputStream(out, true)) {
					int read;
					while((read = fis.read(buffer)) > 0) {
						fos.write(buffer, 0, read);
					}
				}
			}
			return;
		}
		
		if(cmd.equals("mavendirgen")) {
			String[] parts = args(cmdl);
			if (parts.length != 4) {
				System.err.println("usage: mavendirgen <dir> <base> <remotebase> <sheet>");
			} else {
				String _dir = parts[0];
				String base = parts[1];
				String remotebase = parts[2];
				if(!remotebase.endsWith("/")) {
					remotebase += "/";
				}
				
				String _sheet = parts[3];
				File sheet = new File(_sheet);
				if(sheet.exists()) {
					sheet.delete();
				}
				
				File dir = new File(_dir);
				File libsdir = new File(sheet.getParentFile(), "sheet_libs");
				libsdir.mkdir();
				
				List<Blob> colsheet = new ArrayList<Blob>();
				for(File f : Util.lst(dir, ".jar")) {
					if(f.getAbsolutePath().contains("target")) {
//						String id = f.getName().substring(0, f.getName().length() - 4);
						String id = f.getName();
						Map<String, String> config = new HashMap<String, String>();
						config.put("base", base);
						config.put("id", id);
						config.put("hash", Util.sha1(new FileInputStream(f)));
						String remote = remotebase + f.getName();
						config.put("url", remote);
						Blob blob = producer.produce(config);
						colsheet.add(blob);
						
						byte[] buffer = new byte[1024 * 16];
						File copy = new File(libsdir, f.getName());
						try(FileInputStream fis = new FileInputStream(f);
							FileOutputStream fos = new FileOutputStream(copy)) {
							int read;
							while((read = fis.read(buffer)) > 0) {
								fos.write(buffer, 0, read);
							}
						}
					}
				}
				
				String text = Util.GSON.toJson(colsheet);
				System.out.println(text);
				BufferedWriter bw = new BufferedWriter(new FileWriter(sheet));
				bw.write(text);
				bw.close();
			}
			return;
		}
		
		System.err.println("Invalid command: " + line);
	}
	
	private static String[] args(CommandLine cmd) {
		String[] a1 = cmd.getArguments();
		String[] args = new String[a1.length];
		for(int i=0; i < a1.length; i++) {
			args[i] = a1[i].replace("\"", "");
		}
		return args;
	}
}