package com.sirius.sphinx.protocol;

import java.nio.ByteBuffer;

/**
 * Created by pippo on 14-7-4.
 */
public class Handshake extends Command {

	@Override
	public byte[] toBytes() {
		ByteBuffer buffer = ByteBuffer.allocate(12);
		buffer.putShort(Type.PERSIST.code);
		buffer.putShort((short) 0);
		buffer.putInt(4);
		buffer.putInt(1);
		return buffer.array();
	}
}
