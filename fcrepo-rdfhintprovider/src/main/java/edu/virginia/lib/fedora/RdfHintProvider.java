package edu.virginia.lib.fedora;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.fcrepo.common.rdf.SimpleURIReference;
import org.fcrepo.server.storage.FedoraStorageHintProvider;
import org.fcrepo.server.storage.types.DigitalObject;
import org.fcrepo.server.storage.types.RelationshipTuple;
import org.jrdf.graph.SubjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RdfHintProvider implements FedoraStorageHintProvider {

	private final Logger logger = LoggerFactory
			.getLogger(RdfHintProvider.class);

	public Map<String, String> getHintsForAboutToBeStoredObject(
			DigitalObject obj) {
		logger.debug("Generating hints for object storage with object PID: {}",
				obj.getPid());
		Map<String, String> hints = new HashMap<String, String>();
		for (RelationshipTuple tuple : obj.getRelationships()) {
			hints.put(String.valueOf(tuple.hashCode()), "<" + tuple.predicate
					+ "> " + rdfobjectquoteifneeded(tuple));
		}
		logger.debug("Adding {} to hint map.", hints.toString());
		return hints;
	}

	/**
	 * @param t an RDF tuple
	 * @return the object of the tuple, with either quotes or angle brackets as
	 *         appropriate
	 */
	public static String rdfobjectquoteifneeded(RelationshipTuple t) {
		if (t.isLiteral)
			return "\"" + t.object + "\"";
		else
			return "<" + t.object + ">";
	}

	public Map<String, String> getHintsForAboutToBeStoredDatastream(
			DigitalObject obj, String datastreamId) {
		logger.debug(
				"Generating hints for datastream storage with object PID: {}, datastreamId: {}",
				obj.getPid(), datastreamId);
		Map<String, String> hints = new HashMap<String, String>();
		SubjectNode subject = new SimpleURIReference(URI.create("info:fedora/"
				+ obj.getPid() + "/" + datastreamId));
		for (RelationshipTuple tuple : obj
				.getRelationships(subject, null, null)) {
			hints.put(String.valueOf(tuple.hashCode()), "<" + tuple.predicate
					+ "> " + rdfobjectquoteifneeded(tuple));
		}
		logger.debug("Adding {} to hint map.", hints.toString());
		return hints;
	}

}
