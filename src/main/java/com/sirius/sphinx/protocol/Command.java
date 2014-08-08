package com.sirius.sphinx.protocol;

import java.io.IOException;

/**
 * Created by pippo on 14-7-3.
 */
public abstract class Command {

	public Command() {

	}

	public Command(Type type, short version, byte[] payload) {
		this.code = type.code;
		this.version = version;
		this.length = payload.length;
		this.payload = payload;
	}

	public short code;

	public short version;

	public int length;

	public byte[] payload;

	public abstract byte[] toBytes() throws IOException;

	public enum Type {

		SEARCH((short) 0),
		EXCERPT((short) 1),
		UPDATE((short) 2),
		KEYWORDS((short) 3),
		PERSIST((short) 4),
		FLUSHATTRS((short) 7);

		private Type(short code) {
			this.code = code;
		}

		public final short code;

	}

	public enum Version {

		MAJOR_PROTO(0x1),
		COMMAND_SEARCH(0x119),
		COMMAND_EXCERPT(0x102),
		OMMAND_KEYWORDS(0x103),
		COMMAND_FLUSHATTRS(0x100);

		private Version(int code) {
			this.code = code;
		}

		public final int code;

	}
}
