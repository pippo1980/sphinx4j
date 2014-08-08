package com.sirius.sphinx;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.UUID;

/**
 * Created by pippo on 14-7-7.
 */
public class Session {

	public String id = UUID.randomUUID().toString();
	public short version = -1;

	public boolean isValid() {
		return version >= 1;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.append("id", id)
				.append("version", version)
				.toString();
	}
}
