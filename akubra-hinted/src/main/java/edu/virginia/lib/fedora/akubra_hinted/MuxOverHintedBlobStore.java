package edu.virginia.lib.fedora.akubra_hinted;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

import javax.transaction.Transaction;

import org.akubraproject.BlobStore;
import org.akubraproject.BlobStoreConnection;
import org.akubraproject.mux.AbstractMuxStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MuxOverHintedBlobStore extends AbstractMuxStore {

	private final Logger logger = LoggerFactory
			.getLogger(MuxOverHintedBlobStore.class);

	BlobStore deefault;
	
	public MuxOverHintedBlobStore(URI id) {
		super(id);
		logger.debug("Created {} with id: {}", this.getClass().getSimpleName(),
				id.toString());
	}

	public MuxOverHintedBlobStore(URI id, BlobStore... stores) {
		super(id, stores);
		setDefault(stores[0]);
		logger.debug("Created {} with id: {}", this.getClass().getSimpleName(),
				id.toString());
	}

	protected BlobStore getDefault() {
		return deefault;
	}

	/*
	 * @see
	 * org.akubraproject.BlobStore#openConnection(javax.transaction.Transaction,
	 * java.util.Map)
	 */
	@Override
	public BlobStoreConnection openConnection(Transaction tx,
			Map<String, String> hints) throws UnsupportedOperationException,
			IOException {
		return new MuxOverHintedConnection(this, tx);
	}

	public void setDefault(BlobStore deefault) {
		this.deefault = deefault;
	}

}
