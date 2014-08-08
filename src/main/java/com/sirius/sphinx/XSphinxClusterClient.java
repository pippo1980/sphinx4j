package com.sirius.sphinx;

import com.sirius.sphinx.protocol.Command;
import com.sirius.sphinx.protocol.MatchPage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by pippo on 14-7-5.
 */
public class XSphinxClusterClient {

	public XSphinxClusterClient(String... address) throws IOException {
		this.address = address;
		for (String addres : address) {
			getClient(addres);
		}
	}

	private String[] address;

	private Random random = new Random();

	private Map<String, XSphinxClient> clients = new HashMap<String, XSphinxClient>();

	public MatchPage execute(final Command command) throws IOException, InterruptedException {
		XSphinxClient client = getClient();
		try {
			return client.execute(command);
		} catch (Exception e) {

			synchronized (client) {
				client.destroy();
				clients.remove(String.format("%s:%s", client.getHost(), client.getPort()));
			}

			throw new RuntimeException(e);
		}
	}

	public XSphinxClient getClient() throws IOException {
		String addres = address[random.nextInt(address.length)];
		return getClient(addres);
	}

	private synchronized XSphinxClient getClient(String addres) throws IOException {
		XSphinxClient client = clients.get(addres);
		if (client != null) {
			return client;
		}

		String[] _addres = addres.split(":");
		client = new XSphinxClient(_addres[0], Integer.parseInt(_addres[1]));
		clients.put(addres, client);
		return client;
	}

}
