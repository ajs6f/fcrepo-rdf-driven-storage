package edu.virginia.lib.fedora.test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.*;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.fcrepo.server.storage.types.DigitalObject;
import org.fcrepo.server.storage.types.RelationshipTuple;
import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.PredicateNode;
import org.jrdf.graph.SubjectNode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

import edu.virginia.lib.fedora.RdfHintProvider;

@RunWith(MockitoJUnitRunner.class)
public class RdfHintProviderTest {
	private static final Logger logger = LoggerFactory
			.getLogger(RdfHintProviderTest.class);

	private static final RdfHintProvider hinter = new RdfHintProvider();

	@Mock
	private DigitalObject object;

	private static final String objPid = "test:1";
	private static final String datastreamId = "DS1";

	private static final String objUri = "info:fedora/" + objPid;
	private static final String dsUri = objUri + "/" + datastreamId;

	private static final String pred1 = "http://predicate1";
	private static final String pred2 = "http://predicate2";
	private static final String pred3 = "http://predicate3";
	private static final String obj1 = "http://object1";
	private static final String obj2 = "http://object2";
	private static final String obj3 = "http://object3";

	private static final RelationshipTuple objtup1 = tuple(objUri, pred1, obj1);
	private static final RelationshipTuple objtup2 = tuple(objUri, pred2, obj2);
	private static final RelationshipTuple objtup3 = tuple(objUri, pred3, obj3);
	private static final RelationshipTuple dstup1 = tuple(dsUri, pred1, obj1
			+ "/ds");
	private static final RelationshipTuple dstup2 = tuple(dsUri, pred2, obj2
			+ "/ds");
	private static final RelationshipTuple dstup3 = tuple(dsUri, pred3, obj3
			+ "/ds");

	private static final Set<RelationshipTuple> objrels = new ImmutableSet.Builder<RelationshipTuple>()
			.add(objtup1).add(objtup2).add(objtup3).build();
	private static final Set<RelationshipTuple> dsrels = new ImmutableSet.Builder<RelationshipTuple>()
			.add(dstup1).add(dstup2).add(dstup3).build();

	private final static Map.Entry<String, String> testEntry(RelationshipTuple t) {
		return Maps.immutableEntry(
				String.valueOf(t.hashCode()),
				"<" + t.predicate + "> "
						+ RdfHintProvider.rdfobjectquoteifneeded(t));
	}

	private static final Map<String, String> objrelsasmap = new ImmutableMap.Builder<String, String>()
			.put(testEntry(objtup1).getKey(), testEntry(objtup1).getValue())
			.put(testEntry(objtup2).getKey(), testEntry(objtup2).getValue())
			.put(testEntry(objtup3).getKey(), testEntry(objtup3).getValue())
			.build();
	private static final Map<String, String> dsrelsasmap = new ImmutableMap.Builder<String, String>()
			.put(testEntry(dstup1).getKey(), testEntry(dstup1).getValue())
			.put(testEntry(dstup2).getKey(), testEntry(dstup2).getValue())
			.put(testEntry(dstup3).getKey(), testEntry(dstup3).getValue())
			.build();

	@Test
	public void checkHinter() {

		logger.info("Building object mock...");
		when(object.getRelationships()).thenReturn(objrels);

		logger.info("Building object hints to compare...");
		Map<String, String> returnedhints = hinter
				.getHintsForAboutToBeStoredObject(object);
		logger.info("Received the following Map from HintProducer:");
		for (Entry<String, String> e : returnedhints.entrySet()) {
			logger.info("{} -> {}", e.getKey(), e.getValue());
		}
		assertEquals("Object RDF wasn't correctly translated to hints!",
				returnedhints, objrelsasmap);
		logger.info("Object RDF was correctly translated to hints.");

		logger.info("Building datastream mock...");
		when(object.getPid()).thenReturn(objPid);
		when(
				object.getRelationships((SubjectNode) any(),
						(PredicateNode) isNull(), (ObjectNode) isNull()))
				.thenReturn(dsrels);
		returnedhints = hinter.getHintsForAboutToBeStoredDatastream(object,
				datastreamId);
		logger.info("Received the following Map from HintProducer:");
		for (Entry<String, String> e : returnedhints.entrySet()) {
			logger.info("{} -> {}", e.getKey(), e.getValue());
		}

		assertEquals("Datastream RDF wasn't correctly translated to hints!",
				returnedhints, dsrelsasmap);
		logger.info("Object RDF was correctly translated to hints.");
	}

	private static RelationshipTuple tuple(String sub, String pred, String obj) {
		return new RelationshipTuple(sub, pred, obj, false, null);
	}

}
