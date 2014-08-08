package com.sirius.sphinx;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.xsocket.connection.IBlockingConnection;

/**
 * Created by pippo on 14-7-7.
 */
public class ConnectionPool extends GenericObjectPool<IBlockingConnection> {

	public ConnectionPool(String host, int port) {
		super(new ConnectionFactory(host, port));
		setMinIdle(Runtime.getRuntime().availableProcessors());
		setMaxIdle(Runtime.getRuntime().availableProcessors());
		setMaxTotal(Runtime.getRuntime().availableProcessors() * 40);
		setTestOnCreate(false);
		setTestOnBorrow(true);
		setTestOnReturn(false);
		setTestWhileIdle(true);
		setTimeBetweenEvictionRunsMillis(1000 * 60 * 10);
		setBlockWhenExhausted(false);
	}

}
