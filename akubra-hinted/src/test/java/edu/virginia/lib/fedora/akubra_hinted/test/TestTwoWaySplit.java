package edu.virginia.lib.fedora.akubra_hinted.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;

import org.akubraproject.Blob;
import org.akubraproject.BlobStore;
import org.akubraproject.BlobStoreConnection;
import org.akubraproject.UnsupportedIdException;
import org.akubraproject.mem.MemBlobStore;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import edu.virginia.lib.fedora.akubra_hinted.HintedBlobStore;
import edu.virginia.lib.fedora.akubra_hinted.MuxOverHintedBlobStore;

public class TestTwoWaySplit {

	BlobStore splitter, left, right;

	@Before
	public void buildSplitter() throws InstanceAlreadyExistsException,
			MBeanRegistrationException, NotCompliantMBeanException,
			MalformedObjectNameException, NullPointerException {
		left = new MemBlobStore(URI.create("left"));
		right = new MemBlobStore(URI.create("right"));
		HintedBlobStore hintedleft = new HintedBlobStore(
				URI.create("left-store"), left);
		hintedleft.setStoreHints(new ImmutableSet.Builder<String>().add(
				"goes left").build());
		HintedBlobStore hintedright = new HintedBlobStore(
				URI.create("right-store"), right);
		hintedright.setStoreHints(new ImmutableSet.Builder<String>().add(
				"goes right").build());
		splitter = new MuxOverHintedBlobStore(URI.create("splitter-store"),
				hintedleft, hintedright);
	}

	@Test
	public void tryTwoBlobs() throws UnsupportedIdException,
			UnsupportedOperationException, IOException {

		BlobStoreConnection conn = splitter.openConnection(null, null);

		Blob lefty = conn.getBlob(URI.create("lefty"),
				new ImmutableMap.Builder<String, String>()
						.put("1", "goes left").put("2", "not right").build());
		OutputStream out = lefty.openOutputStream(-1, true);
		out.write("This is the left-hand blob.".getBytes());
		out.close();

		Blob righty = conn.getBlob(
				URI.create("righty"),
				new ImmutableMap.Builder<String, String>()
						.put("1", "goes right").put("2", "not left").build());
		out = righty.openOutputStream(-1, true);
		out.write("This is the right-hand blob.".getBytes());
		out.close();

		assertTrue("Couldn't find lefty in the left-hand side!", left
				.openConnection(null, null).getBlob(URI.create("lefty"), null)
				.exists());
		assertTrue("Couldn't find righty in the right-hand side!", right
				.openConnection(null, null).getBlob(URI.create("righty"), null)
				.exists());
		assertFalse("Could find lefty in the right-hand side!", right
				.openConnection(null, null).getBlob(URI.create("lefty"), null)
				.exists());
		assertFalse("Could find righty in the left-hand side!", left
				.openConnection(null, null).getBlob(URI.create("righty"), null)
				.exists());
	}
}
