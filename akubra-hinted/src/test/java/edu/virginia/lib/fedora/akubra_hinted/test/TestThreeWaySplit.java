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

public class TestThreeWaySplit {

	BlobStore splitter, left, center, right;

	@Before
	public void buildSplitter() throws InstanceAlreadyExistsException,
			MBeanRegistrationException, NotCompliantMBeanException,
			MalformedObjectNameException, NullPointerException {

		left = new MemBlobStore(URI.create("left"));
		center = new MemBlobStore(URI.create("center"));
		right = new MemBlobStore(URI.create("right"));

		HintedBlobStore hintedleft = new HintedBlobStore(
				URI.create("left-store"), left);
		hintedleft.setStoreHints(new ImmutableSet.Builder<String>().add(
				"goes left").build());

		HintedBlobStore hintedcenter = new HintedBlobStore(
				URI.create("center-store"), center);
		hintedcenter.setStoreHints(new ImmutableSet.Builder<String>().add(
				"goes center").build());

		HintedBlobStore hintedright = new HintedBlobStore(
				URI.create("right-store"), right);
		hintedright.setStoreHints(new ImmutableSet.Builder<String>().add(
				"goes right").build());

		splitter = new MuxOverHintedBlobStore(URI.create("splitter-store"),
				hintedleft, hintedcenter, hintedright);
	}

	@Test
	public void tryThreeBlobs() throws UnsupportedIdException,
			UnsupportedOperationException, IOException {

		URI leftyUri = URI.create("lefty-blob");
		URI rightyUri = URI.create("righty-blob");
		URI centerUri = URI.create("center-blob");

		BlobStoreConnection conn = splitter.openConnection(null, null);

		Blob lefty = conn.getBlob(
				leftyUri,
				new ImmutableMap.Builder<String, String>()
						.put("goes left", "goes left")
						.put("not right", "not right").build());
		OutputStream out = lefty.openOutputStream(-1, true);
		out.write("This is the left-hand blob.".getBytes());
		out.close();

		Blob centy = conn.getBlob(
				centerUri,
				new ImmutableMap.Builder<String, String>()
						.put("goes center", "goes center")
						.put("only center", "only center").build());
		out = centy.openOutputStream(-1, true);
		out.write("This is the center-hand blob.".getBytes());
		out.close();

		Blob righty = conn.getBlob(
				rightyUri,
				new ImmutableMap.Builder<String, String>()
						.put("goes right", "goes right")
						.put("not left", "not left").build());
		out = righty.openOutputStream(-1, true);
		out.write("This is the right-hand blob.".getBytes());
		out.close();

		assertTrue("Couldn't find lefty in the left-hand side!", left
				.openConnection(null, null).getBlob(leftyUri, null).exists());
		assertTrue("Couldn't find centy in the center-hand side!", center
				.openConnection(null, null).getBlob(centerUri, null).exists());
		assertTrue("Couldn't find righty in the right-hand side!", right
				.openConnection(null, null).getBlob(rightyUri, null).exists());

		assertFalse("Could find lefty in the right-hand side!", right
				.openConnection(null, null).getBlob(leftyUri, null).exists());
		assertFalse("Could find lefty in the center-hand side!", center
				.openConnection(null, null).getBlob(leftyUri, null).exists());
		assertFalse("Could find centy in the right-hand side!", right
				.openConnection(null, null).getBlob(centerUri, null).exists());
		assertFalse("Could find centy in the left-hand side!", left
				.openConnection(null, null).getBlob(centerUri, null).exists());
		assertFalse("Could find righty in the left-hand side!", left
				.openConnection(null, null).getBlob(rightyUri, null).exists());
		assertFalse("Could find righty in the center-hand side!", center
				.openConnection(null, null).getBlob(rightyUri, null).exists());
	}
}
