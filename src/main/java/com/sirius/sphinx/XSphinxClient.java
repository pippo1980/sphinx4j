package com.sirius.sphinx;

import com.sirius.sphinx.protocol.Command;
import com.sirius.sphinx.protocol.MatchPage;
import com.sirius.sphinx.protocol.Response;
import com.sirius.sphinx.protocol.Response.Status;
import com.sirius.sphinx.protocol.Search;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xsocket.connection.IBlockingConnection;
import org.xsocket.connection.IWriteCompletionHandler;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by pippo on 14-7-3.
 */
public class XSphinxClient {

	private static final Logger LOGGER = LoggerFactory.getLogger(XSphinxClient.class);

	public XSphinxClient(String host, int port) throws IOException {
		pool = new ConnectionPool(host, port);
	}

	public void destroy() {
		LOGGER.warn("the client:[{}] will stop", this);
		this.pool.close();
	}

	private ConnectionPool pool;
	private String host;
	private int port;

	public ConnectionPool getPool() {
		return pool;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public void setMaxIdle(int maxIdle) {
		pool.setMaxIdle(maxIdle);
	}

	public void setMinIdle(int minIdle) {
		pool.setMinIdle(minIdle);
	}

	public void setMaxTotal(int maxTotal) {
		pool.setMaxTotal(maxTotal);
	}

	public void setBlockWhenExhausted(boolean blockWhenExhausted) {
		pool.setBlockWhenExhausted(blockWhenExhausted);
	}

	public void setMaxWaitMillis(long maxWaitMillis) {
		pool.setMaxWaitMillis(maxWaitMillis);
	}

	public void setLifo(boolean lifo) {
		pool.setLifo(lifo);
	}

	public void setTestOnCreate(boolean testOnCreate) {
		pool.setTestOnCreate(testOnCreate);
	}

	public void setTestOnBorrow(boolean testOnBorrow) {
		pool.setTestOnBorrow(testOnBorrow);
	}

	public void setTestOnReturn(boolean testOnReturn) {
		pool.setTestOnReturn(testOnReturn);
	}

	public void setTestWhileIdle(boolean testWhileIdle) {
		pool.setTestWhileIdle(testWhileIdle);
	}

	public void setTimeBetweenEvictionRunsMillis(long timeBetweenEvictionRunsMillis) {
		pool.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
	}

	public void setNumTestsPerEvictionRun(int numTestsPerEvictionRun) {
		pool.setNumTestsPerEvictionRun(numTestsPerEvictionRun);
	}

	public void setMinEvictableIdleTimeMillis(long minEvictableIdleTimeMillis) {
		pool.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
	}

	public void setSoftMinEvictableIdleTimeMillis(long softMinEvictableIdleTimeMillis) {
		pool.setSoftMinEvictableIdleTimeMillis(softMinEvictableIdleTimeMillis);
	}

	public void setEvictionPolicyClassName(String evictionPolicyClassName) {
		pool.setEvictionPolicyClassName(evictionPolicyClassName);
	}

	public MatchPage execute(Command command) throws Exception {
		IBlockingConnection connection = pool.borrowObject(1000);

		try {
			request(command, connection);
			return response(command, connection);
		} catch (Exception e) {
			LOGGER.error("execute command:[{}] due to error:[{}]", command, ExceptionUtils.getStackTrace(e));
			if (connection != null) {
				pool.invalidateObject(connection);
			}
			throw new RuntimeException(e);
		} finally {
			if (connection != null) {
				pool.returnObject(connection);
			}
		}
	}

	private void request(final Command command, IBlockingConnection connection) throws IOException {
		byte[] packet = command.toBytes();
		connection.write(packet, new IWriteCompletionHandler() {
			@Override
			public void onWritten(int written) throws IOException {
				LOGGER.debug("written {} bytes", written);
			}

			@Override
			public void onException(IOException e) {
				LOGGER.error("write command:[{}] due to error:[{}]", command, ExceptionUtils.getStackTrace(e));
			}
		});
		connection.flush();
	}

	private MatchPage response(Command command, IBlockingConnection connection) throws IOException {
		Response response = new Response();
		response.header.status = Status.from(connection.readShort());
		response.header.version = connection.readShort();
		response.header.body_size = connection.readInt();
		response.body = ByteBuffer.wrap(connection.readBytesByLength(response.header.body_size));
		LOGGER.debug("the response is:[{}]", response);

		switch (response.header.status) {
			case OK:
				Search search = (Search) command;
				/*这协议真神,status在body里面还有*/
				MatchPage page = new MatchPage(search.offset, search.limit, Status.from(response.body.getInt()));
				page.parse(response.body);
				LOGGER.debug("the search result is:[{}]", page);
				return page;
			default:
				throw new IllegalStateException(String.format("invalid status:[%s], the error is:[%s]",
						response.header.status,
						new String(response.body.array())));
		}

	}

}
