package com.sirius.sphinx.protocol;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.nio.ByteBuffer;

/**
 * Created by pippo on 14-7-5.
 */
public class Response {

	public Header header = new Header();
	public ByteBuffer body;

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
				.append("header", header)
				.append("body", body)
				.toString();
	}

	public static class Header {

		public Header() {

		}

		public Header(Status status, short version, int body_size) {
			this.status = status;
			this.version = version;
			this.body_size = body_size;
		}

		public Status status;
		public short version;
		public int body_size;

		@Override
		public String toString() {
			return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
					.append("status", status)
					.append("version", version)
					.append("body_size", body_size)
					.toString();
		}
	}

	public enum Status {

		OK(0),
		ERROR(1),
		RETRY(2),
		WARNING(3);

		private Status(int code) {
			this.code = code;
		}

		public final int code;

		public static Status from(int code) {
			for (Status status : Status.values()) {
				if (code == status.code) {
					return status;
				}
			}

			throw new IllegalArgumentException(String.format("invalid status code:[%s]", code));
		}

	}
}
