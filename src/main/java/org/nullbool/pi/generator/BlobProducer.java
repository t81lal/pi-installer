package org.nullbool.pi.generator;

import java.util.Map;

import org.nullbool.pi.installer.Blob;

/**
 * @author Bibl (don't ban me pls)
 * @created 25 Jul 2015 14:25:41
 */
public interface BlobProducer {

	public Blob produce(Map<String, String> config);
}