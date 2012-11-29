package edu.virginia.lib.fedora.akubra_hinted;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import javax.transaction.Transaction;

import org.akubraproject.BlobStore;
import org.akubraproject.BlobStoreConnection;
import org.akubraproject.impl.AbstractBlobStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Wraps another BlobStore with hints for use with MuxOverHintedBlobStore or for
 * other kinds of description.
 * 
 * @author ajs6f
 * 
 */
public class HintedBlobStore extends AbstractBlobStore {

	private final Logger logger = LoggerFactory
			.getLogger(HintedBlobStore.class);

	private Set<String> hints;

	private BlobStore store;

	protected HintedBlobStore(URI id) {
		super(id);
		logger.debug("Created {} with id: {}", this.getClass().getSimpleName(),
				id.toString());
	}

	public HintedBlobStore(URI id, BlobStore store) {
		super(id);
		this.store = store;
		logger.debug(
				"Created {} with id: {} over store: {}",
				Arrays.asList(this.getClass().getSimpleName(), id.toString(),
						store.getId().toString()).toArray());
	}

	public HintedBlobStore(URI id, BlobStore store, Set<String> storehints) {
		super(id);
		this.store = store;
		this.hints = storehints;
		logger.debug(
				"Created {} with id: {} over store: {}",
				Arrays.asList(this.getClass().getSimpleName(), id.toString(),
						store.getId().toString()).toArray());
	}

	public Set<String> getStoreHints() {
		return hints;
	}

	public void setStoreHints(Set<String> hints) {
		this.hints = hints;
		logger.debug("Set hints for {} to {}", getId(), hints.toString());
	}

	public void setBlobStore(BlobStore store) {
		this.store = store;
	}

	@Override
	public BlobStoreConnection openConnection(Transaction tx,
			Map<String, String> blobHints)
			throws UnsupportedOperationException, IOException {
		return store.openConnection(tx, blobHints);
	}

}
