package edu.virginia.lib.fedora.akubra_hinted.it;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yourmediashelf.fedora.client.FedoraClient;
import com.yourmediashelf.fedora.client.FedoraClientException;
import com.yourmediashelf.fedora.client.FedoraCredentials;
import com.yourmediashelf.fedora.client.request.FedoraRequest;

public class OneObjectOneDatastreamIT {
	
	private final static Logger logger = LoggerFactory
			.getLogger(OneObjectOneDatastreamIT.class);


	private static FedoraClient fc;

	@BeforeClass
	public static void ingestObject() throws MalformedURLException,
			FedoraClientException {
		fc = new FedoraClient(new FedoraCredentials(new URL(
				"http://localhost:8080/fedora"), "fedoraAdmin", "fedoraAdmin"));
		fc.debug(true);
		FedoraRequest.setDefaultClient(fc);
		logger.info("Ingesting test object test:1...");
		FedoraClient.ingest("test:1").content("").execute();
	}
	
	@Test
	public void checkForObjectXML() {
		
	}
	
	@Test
	public void checkForDatastream() {
		
	}

	@AfterClass
	public static void purgeObject() {
		logger.info("Purging test object test:1...");
		FedoraClient.purgeObject("test:1");
	}

}
