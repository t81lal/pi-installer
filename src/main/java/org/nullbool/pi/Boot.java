package org.nullbool.pi;

import java.io.File;
import java.io.FileInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

import javax.swing.JOptionPane;

import org.nullbool.pi.installer.BlobRetriever;
import org.nullbool.pi.installer.IOHelper;
import org.nullbool.pi.installer.RemoteBlob;
import org.nullbool.pi.installer.RemoteBlobRetriever;
import org.nullbool.pi.installer.ResourceConstants;
import org.nullbool.pi.installer.Util;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.launch.FrameworkFactory;

/**
 * @author Bibl (don't ban me pls)
 * @created 25 Jul 2015 13:57:17
 */
public class Boot {

	private static final URL P2_REPO_SITE = mk("http://107.150.29.22/programs/pi/blobs/sheets/p2repo.sheet");
	private static final URL UPDATE_SITE = mk("http://107.150.29.22/programs/pi/blobs/sheets/latest.sheet");
	@Deprecated
	public static final int MAX_BUFF_SIZE = 1024;
	private static File[] jars = null;
	private static File[] libs = null;
	private static BundleContext context;

	public static void main(String[] args) {
		try {
			jars = blob_it(P2_REPO_SITE);
			libs = blob_it(UPDATE_SITE);
			System.out.println("Launching");
			run();
		} catch (Throwable e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Fatal error.", "Boot Error", JOptionPane.ERROR_MESSAGE);
			System.exit(2);
		}
	}

	private static File[] blob_it(URL url) throws Throwable {
		BlobRetriever<RemoteBlob> retriever = getImpl(url);
		Collection<RemoteBlob> blobs = retriever.get();
		System.out.println("Recieved: ");
		for(RemoteBlob b : blobs) {
			System.out.println(b);
		}
		System.out.println("Verifying.");
		return verify(blobs);
	}

	private static void run() {
		FrameworkFactory frameworkFactory = ServiceLoader.load(FrameworkFactory.class).iterator().next();

		Map<String, String> config = new HashMap<String, String>();
		config.put("osgi.console", "");
		config.put("osgi.clean", "true");
		config.put("osgi.noShutdown", "false");
		config.put("eclipse.ignoreApp", "true");
		config.put("osgi.bundles.defaultStartLevel", "4");
		config.put("osgi.configuration.area", "./configuration");

		// config.put("osgi.debug", "configuration/debug.options");
		// config.put("osgi.console", "localhost:2223");
		// config.put("osgi.console.ssh", "localhost:2222");
		// config.put("osgi.console.ssh.useDefaultSecureStorage", "true");
		// config.put("osgi.console.enable.builtin", "false");

		// automated bundles deployment
		config.put("felix.fileinstall.dir", "./dropins");
		config.put("felix.fileinstall.noInitialDelay", "true");
		config.put("felix.fileinstall.start.level", "4");

		//@formatter:off
		// System.setProperty("ssh.server.keystore", "configuration/hostkey.ser");
		// System.setProperty("org.eclipse.equinox.console.jaas.file", "configuration/store");
		// System.setProperty("java.security.auth.login.config", "configuration/org.eclipse.equinox.console.authentication.config");
		//@formatter:on

		Framework framework = frameworkFactory.newFramework(config);

		try {
			framework.start();
		} catch (BundleException e) {
			e.printStackTrace();
		}

		context = framework.getBundleContext();

		// logging
		//		Bundle b1 = install("slf4j-api");
		//		Bundle b2 = install("logback-core");
		//		Bundle b3 = install("logback-classic");
		//		try {
		//			b1.start();
		//			b2.start();
		//			b3.start();
		//		} catch (BundleException e) {
		//			e.printStackTrace();
		//		}

		// framework bundles
		//		start("org.eclipse.osgi");
		// org.eclipse.osgi 3_10+ HAS TO BE USED FOR .DTO
		start("org.eclipse.osgi.services");
		start("org.eclipse.osgi.util");
		start("org.eclipse.equinox.common");
		start("org.eclipse.equinox.registry");
		start("org.eclipse.equinox.preferences");
		start("org.eclipse.equinox.app");
		start("org.eclipse.core.jobs");
		start("org.eclipse.core.contenttype");
		start("org.eclipse.core.runtime");
		start("org.eclipse.equinox.security");
		start("org.eclipse.equinox.event");
		start("org.eclipse.equinox.log");

		// security
		start("bcprov-ext-jdk16");

		// default shell
		start("org.apache.felix.gogo.runtime");
		start("org.apache.felix.gogo.command");
		start("org.apache.felix.gogo.shell");
		start("org.eclipse.equinox.console");

		// automated bundles deployment
		start("org.apache.felix.fileinstall");

		// mvn locator
		start("pax-url-mvn");

		// wrap locator (wrap non-OSGi into bundle)
		start("pax-url-wrap");

		// framework admin
		start("org.eclipse.equinox.frameworkadmin");

		// ssh console
		// start("org.apache.mina.core");
		// start("org.apache.sshd.core");
		// start("org.eclipse.equinox.console.ssh");
		// install("org.eclipse.equinox.console.jaas.fragment");

		// p2 dropins discovery (doesn't work)
		// start("org.sat4j.core");
		// start("org.sat4j.pb");
		// start("org.eclipse.equinox.p2.core");
		// start("org.eclipse.equinox.p2.discovery");
		// start("org.eclipse.equinox.p2.metadata");
		// start("org.eclipse.equinox.p2.repository");
		// start("org.eclipse.equinox.p2.metadata.repository");
		// start("org.eclipse.equinox.p2.engine");
		// start("org.eclipse.equinox.p2.director");
		// start("org.eclipse.equinox.p2.director.app");

		int len = libs.length;
		Bundle[] cxts = new Bundle[len];
		for(int i=0; i < len; i++) {
			cxts[i] = install(libs[i]);
			System.out.println("Installing " + libs[i].getAbsolutePath());
		}

		fr: for(Bundle b : cxts) {
			Dictionary<String, String> dict = b.getHeaders();
			Enumeration<String> e = dict.keys();
			while(e.hasMoreElements()) {
				String s = e.nextElement();
				if(s.equals("Bundle-ActivationPolicy")) {
					String v = dict.get(s);
					if(v != null) {
						if(v.equals("lazy")) {
							System.out.println("Skipping running " + b.getLocation());
							continue fr;
						}
					}
				}
			}
			try {
				System.out.println("Starting " + b.getLocation());
				b.start();
			} catch (BundleException e1) {
				e1.printStackTrace();
			}
		}
		

		for(Bundle b : cxts) {
			if(b.getSymbolicName().equals("org.nullbool.pi.core.launcher")) {
				try {
					b.start();
				} catch (BundleException e) {
					e.printStackTrace();
				}
				break;
			}
		}
	}

	private static File[] getJARs() {
		if (jars == null) {
			throw new IllegalStateException("No p2 jars.");
		}
		return jars;
	}

	private static File[] getLibs() {
		if (libs == null) {
			throw new IllegalStateException("No pi modules.");
		}
		return libs;
	}

	protected static Bundle start(String name) {
		Bundle bundle = install(name);
		if (bundle != null) {
			try {
				bundle.start();
			} catch (BundleException e) {
				e.printStackTrace();
			}
		}
		return bundle;
	}

	protected static Bundle install(File f) {
		String url = "file:" + f.getAbsolutePath();
		try {
			return context.installBundle(url);
		} catch (BundleException e) {
			e.printStackTrace();
		}
		return null;
	}

	protected static Bundle install(String name) {
		String found = null;
		for (File jar : getJARs()) {
			String fn = jar.getName();
			if (fn.startsWith(name + "_") || fn.startsWith(name + "-")) {
				found = "file:" + jar.getAbsolutePath();
				break;
			}
		}
		if(found == null) {
			for (File jar : getLibs()) {
				String fn = jar.getName();
				if (fn.startsWith(name + "_") || fn.startsWith(name + "-")) {
					found = "file:" + jar.getAbsolutePath();
					break;
				}
			}
		}

		if (found == null) {
			throw new RuntimeException(String.format("JAR for %s not found", name));
		}
		try {
			return context.installBundle(found);
		} catch (BundleException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static File[] verify(Collection<RemoteBlob> blobs) throws Throwable {
		File[] files = new File[blobs.size()];
		int i = 0;
		for(RemoteBlob b : blobs) {
			File base = new File(ResourceConstants.OSGI_DATA_DIR, b.getBase());
			if(!base.exists()) {
				base.mkdirs();
			}

			File f = new File(base, b.getId());
			System.out.println("Verifying: " + f.getAbsolutePath() + " ...");
			if(!f.exists() || !b.verify(new FileInputStream(f))) {
				System.out.println("Downloading from: " + b.getURL() + " ...");
				IOHelper.download(b.getURL(), f);
				System.out.println("newhash: " + Util.sha1(new FileInputStream(f)));
			}
			System.out.println("... done.");
			files[i++] = f;
		}
		System.out.println("Done verifying blobs.");
		return files;
	}

	private static URL mk(String s) {
		try {
			return new URL(s);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Fatal error.", "Init Error", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
			return null;
		}
	}

	private static BlobRetriever<RemoteBlob> getImpl(URL url) {
		return new RemoteBlobRetriever(url);
	}
}