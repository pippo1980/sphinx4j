package com.sirius.sphinx.protocol;

import com.sirius.sphinx.Utils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by pippo on 14-7-4.
 */
public class LongRangeFilter extends Filter {

	public LongRangeFilter(String attribute, long min, long max) {
		super(Type.range, attribute, false);
		this.min = min;
		this.max = max;
	}

	public long min;

	public long max;

	public byte[] toBytes() throws IOException {
		ByteArrayOutputStream req = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(req);

		try {
			Utils.writeNetUTF8(out, attribute);
			out.writeInt(type.code);
			out.writeLong(min);
			out.writeLong(max);
			out.writeInt(exclude ? 1 : 0);
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
				.append("type", type)
				.append("attribute", attribute)
				.append("exclude", exclude)
				.append("min", min)
				.append("max", max)
				.toString();
	}
}
