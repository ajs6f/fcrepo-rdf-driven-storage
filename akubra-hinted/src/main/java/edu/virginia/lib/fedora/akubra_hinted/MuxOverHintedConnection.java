package edu.virginia.lib.fedora.akubra_hinted;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.Map;

import javax.transaction.Transaction;

import org.akubraproject.Blob;
import org.akubraproject.BlobStore;
import org.akubraproject.UnsupportedIdException;
import org.akubraproject.mux.AbstractMuxConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MuxOverHintedConnection extends AbstractMuxConnection {

	private final Logger logger = LoggerFactory
			.getLogger(MuxOverHintedConnection.class);

	public MuxOverHintedConnection(BlobStore store, Transaction txn) {
		super(store, txn);
		logger.debug("Created {} from store: {}", this.getClass()
				.getSimpleName(), store.getId().toString());
	}

	@Override
	public Blob getBlob(URI blobId, Map<String, String> hints)
			throws IOException {
		logger.debug("Retrieving or creating blob with id: {} in store: {}",
				blobId, getStore(hints).getId());
		return super.getBlob(blobId, hints);
	}

	@Override
	public Blob getBlob(InputStream content, long estimatedSize,
			Map<String, String> hints) throws IOException,
			UnsupportedOperationException {
		logger.debug("Retrieving or creating blob in store: {}",
				getStore(hints).getId());
		return super.getBlob(content, estimatedSize, hints);
	}

	@Override
	public BlobStore getStore(URI blobId, Map<String, String> blobhints)
			throws IOException, UnsupportedIdException {
		logger.debug("Storing blob: {}", blobId);
		return getStore(blobhints);
	}

	public BlobStore getStore(Map<String, String> blobhints)
			throws IOException, UnsupportedIdException {

		@SuppressWarnings("unchecked")
		List<HintedBlobStore> stores = (List<HintedBlobStore>) ((MuxOverHintedBlobStore) getBlobStore())
				.getBackingStores();
		for (HintedBlobStore store : stores) {
			if (blobhints != null) {
				logger.debug("Checking store {}...", store.getId());
				if (store.getStoreHints() == null) {
					// matches anything
					logger.debug("Using store with no hints: {}", store.getId());
					return store;
				} else {
					logger.debug(
							"Checking blob hints: {} against store hints: {}",
							blobhints.values(), store.getStoreHints());
					if (blobhints.values().containsAll(
							store.getStoreHints())) {
						logger.debug("Using store: {} with store hints: {}",
								store.getId(), store.getStoreHints());
						return store;
					}
				}
			} else {
				logger.warn("No blob hints found to help store this blob!");
				break;
			}
		}
		logger.debug("Using store: {}",
				((MuxOverHintedBlobStore) getBlobStore()).getDefault().getId());
		return ((MuxOverHintedBlobStore) getBlobStore()).getDefault();
	}
}
