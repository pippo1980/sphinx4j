package com.sirius.sphinx;

import org.apache.commons.lang3.CharEncoding;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by pippo on 14-7-4.
 */
public final class Utils {

	public static void writeNetUTF8(final DataOutputStream stream, final String str) throws IOException {

		stream.writeShort(0);
		if (str == null) {
			stream.writeShort(0);
		} else {
			stream.writeUTF(str);
		}
	}

	public static String readNetUTF8(DataInputStream istream) throws IOException {
		int iLen = istream.readInt();
		byte[] sBytes = new byte[iLen];
		istream.readFully(sBytes);
		return new String(sBytes, "UTF-8");
	}

	public static String readNetUTF8(ByteBuffer buffer) throws IOException {
		int length = buffer.getInt();
		byte[] bytes = new byte[length];
		buffer.get(bytes);
		return new String(bytes, CharEncoding.UTF_8);
	}

	public static long readDword(ByteBuffer buffer) throws IOException {
		long v = (long) buffer.getInt();
		if (v < 0) {
			v += 4294967296L;
		}
		return v;
	}
}
