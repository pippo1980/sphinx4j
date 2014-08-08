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
 * Created by pippo on 14-7-4.
 */
public class Group {

	public Function function = Function.DAY;
	public String group_by = StringUtils.EMPTY;
	public String group_sort = "@group desc";
	public int max_matches = 100;
	public int cutoff = 0;
	public int retry_count = 0;
	public int retry_delay = 0;
	public String distinct = StringUtils.EMPTY;

	public byte[] toBytes() throws IOException {
		ByteArrayOutputStream req = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(req);

		try {
			out.writeInt(function.code);
			Utils.writeNetUTF8(out, group_by);
			out.writeInt(max_matches);
			Utils.writeNetUTF8(out, group_sort);
			out.writeInt(cutoff);
			out.writeInt(retry_count);
			out.writeInt(retry_delay);
			Utils.writeNetUTF8(out, distinct);
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
				.append("function", function)
				.append("group_by", group_by)
				.append("group_sort", group_sort)
				.append("max_matches", max_matches)
				.append("cutoff", cutoff)
				.append("retry_count", retry_count)
				.append("retry_delay", retry_delay)
				.append("distinct", distinct)
				.toString();
	}

	public enum Function {

		DAY(0),
		WEEK(1),
		MONTH(2),
		YEAR(3),
		ATTR(4),
		ATTRPAIR(5);

		private Function(int code) {
			this.code = code;
		}

		public final int code;

	}

}
