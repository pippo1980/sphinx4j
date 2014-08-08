package com.sirius.sphinx.protocol;

import com.sirius.sphinx.Utils;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by pippo on 14-7-5.
 */
public class Attribute implements Cloneable {

	public Attribute(String name, Type type, Object value) {
		this.name = name;
		this.type = type;
		this.value = value;
	}

	public void parse(ByteBuffer buffer) throws IOException {

		switch (type) {
			case INTEGER:
				value = buffer.getInt();
				break;
			case BIGINT:
				value = buffer.getLong();
				break;
			case FLOAT:
				value = buffer.getFloat();
				break;
			case STRING:
				value = Utils.readNetUTF8(buffer);
				break;
			case MULTI:
				long m_val = Utils.readDword(buffer);
				value = new long[(int) m_val];
				for (int k = 0; k < m_val; k++) {
					((long[]) value)[k] = Utils.readDword(buffer);
				}
				break;
			case MULTI64:
				long m64_val = Utils.readDword(buffer) / 2;
				value = new long[(int) m64_val];
				for (int k = 0; k < m64_val; k++) {
					((long[]) value)[k] = buffer.getLong();
				}
				break;
			default:
				value = Utils.readDword(buffer);
		}
	}

	public String name;
	public Type type;
	public Object value;

	@Override
	public String toString() {
		return String.format("{name=%s,type=%s,value=%s}", name, type, value);
	}

	@Override
	public Attribute clone() {
		return new Attribute(this.name, this.type, null);
	}

	public enum Type {

		INTEGER(1),
		TIMESTAMP(2),
		ORDINAL(3),
		BOOL(4),
		FLOAT(5),
		BIGINT(6),
		STRING(7),
		MULTI(0x40000001),
		MULTI64(0x40000002);

		private Type(int code) {
			this.code = code;
		}

		public final int code;

		public static Type from(int code) {
			for (Type status : Type.values()) {
				if (code == status.code) {
					return status;
				}
			}

			throw new IllegalArgumentException(String.format("invalid type code:[%s]", code));
		}

	}
}
