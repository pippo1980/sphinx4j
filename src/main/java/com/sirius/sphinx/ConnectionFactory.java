package com.sirius.sphinx;

import com.sirius.sphinx.protocol.Command.Version;
import com.sirius.sphinx.protocol.Handshake;
import org.apache.commons.lang3.Validate;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xsocket.connection.BlockingConnection;
import org.xsocket.connection.IBlockingConnection;
import org.xsocket.connection.IConnection.FlushMode;
import org.xsocket.connection.IWriteCompletionHandler;

import java.io.IOException;
import java.net.InetAddress;

/**
 * Created by pippo on 14-7-7.
 */
public class ConnectionFactory extends BasePooledObjectFactory<IBlockingConnection> {

	private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionFactory.class);

	public ConnectionFactory(String host, int port) {
		this.host = host;
		this.port = port;
	}

	private String host;
	private int port;

	@Override
	public IBlockingConnection create() throws Exception {
		//  可用的connection options
		//	static final String SO_SNDBUF = IConnection.SO_SNDBUF;
		//	static final String SO_RCVBUF = IConnection.SO_RCVBUF;
		//	static final String SO_REUSEADDR = IConnection.SO_REUSEADDR;
		//	static final String SO_TIMEOUT = "SOL_SOCKET.SO_TIMEOUT";
		//	static final String SO_KEEPALIVE = IConnection.SO_KEEPALIVE;
		//	static final String SO_LINGER = IConnection.SO_LINGER;
		//	static final String TCP_NODELAY = IConnection.TCP_NODELAY;

		IBlockingConnection connection = new BlockingConnection(InetAddress.getByName(host), port);
		connection.setAutoflush(false);
		connection.setFlushmode(FlushMode.ASYNC);
		return connection;
	}

	@Override
	public PooledObject<IBlockingConnection> wrap(IBlockingConnection obj) {
		return new DefaultPooledObject<IBlockingConnection>(obj);
	}

	@Override
	public boolean validateObject(PooledObject<IBlockingConnection> p) {
		IBlockingConnection connection = p.getObject();
		if (connection == null) {
			return false;
		}

		if (!connection.isOpen()) {
			return false;
		}

		Session session = (Session) connection.getAttachment();
		if (session == null || !session.isValid()) {
			return false;
		}

		return true;
	}

	@Override
	public PooledObject<IBlockingConnection> makeObject() throws Exception {
		IBlockingConnection connection = create();
		handShake(connection, createSession(connection));
		return wrap(connection);
	}

	private Session createSession(IBlockingConnection connection) throws IOException {
		Session session = new Session();
		session.version = (short) connection.readInt();
		Validate.isTrue(session.version >= 1, "invalid sphinx version:[%s]", session.version);
		connection.write(Version.MAJOR_PROTO.code);
		connection.flush();
		return session;
	}

	private void handShake(IBlockingConnection connection, Session session) throws IOException {
		connection.write(new Handshake().toBytes(), new IWriteCompletionHandler() {
			@Override
			public void onWritten(int written) throws IOException {
				LOGGER.debug("written {} bytes", written);
			}

			@Override
			public void onException(IOException e) {
				LOGGER.error("handshake due to error", e);
			}
		});
		connection.flush();
		connection.setAttachment(session);
	}

	@Override
	public void destroyObject(PooledObject<IBlockingConnection> p) throws Exception {
		IBlockingConnection connection = p.getObject();
		connection.close();
	}
}
