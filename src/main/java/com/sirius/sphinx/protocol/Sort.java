package com.sirius.sphinx.protocol;

import com.sirius.sphinx.Utils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by pippo on 14-7-5.
 */
public class Sort {

	public SortMode mode = SortMode.RELEVANCE;
	public String attribute = StringUtils.EMPTY;

	public byte[] toBytes() throws IOException {
		ByteArrayOutputStream req = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(req);

		try {
			out.writeInt(mode.code);
			Utils.writeNetUTF8(out, attribute);
			out.flush();
			return req.toByteArray();
		} finally {
			IOUtils.closeQuietly(out);
			IOUtils.closeQuietly(req);
		}
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
				.append("mode", mode)
				.append("attribute", attribute)
				.toString();
	}

	public enum SortMode {

		RELEVANCE(0),
		ATTR_DESC(1),
		ATTR_ASC(2),
		TIME_SEGMENTS(3),
		EXTENDED(4),
		EXPR(5);

		private SortMode(int code) {
			this.code = code;
		}

		public final int code;

	}

}
