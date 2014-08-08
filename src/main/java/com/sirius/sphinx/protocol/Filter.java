package com.sirius.sphinx.protocol;

import java.io.IOException;

/**
 * Created by pippo on 14-7-4.
 */
public abstract class Filter {

	public Filter(Type type, String attribute, boolean exclude) {
		this.type = type;
		this.attribute = attribute;
		this.exclude = exclude;
	}

	public Type type;

	public String attribute;

	public boolean exclude;

	public abstract byte[] toBytes() throws IOException;

	public enum Type {

		values(0),
		range(1);

		private Type(int code) {
			this.code = code;
		}

		public final int code;

	}

}
