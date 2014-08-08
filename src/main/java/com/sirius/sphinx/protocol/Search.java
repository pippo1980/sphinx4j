package com.sirius.sphinx.protocol;

import com.sirius.sphinx.Utils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pippo on 14-7-3.
 */
public class Search extends Command {

	public Search() {

	}

	public Search(String query) {
		this.query = query;
	}

	public Search(String query, String index) {
		this.query = query;
		this.index = index;
	}

	public int offset = 0;
	public int limit = 10;
	public MatchMode mode = MatchMode.ALL;
	public RankMode rank = RankMode.PROXIMITY_BM25;
	public Sort sort = new Sort();
	public String query;
	public int[] weights;
	public String index = "*";
	public int minId;
	public int maxId;
	public List<Filter> filters = new ArrayList<Filter>();
	public Group group = new Group();
	public Anchor anchor = new Anchor();
	public Map<String, Integer> indexWeights = new LinkedHashMap<String, Integer>();
	public int maxQueryTime = 0;
	public Map<String, Integer> fieldWeights = new LinkedHashMap<String, Integer>();
	public String comment = StringUtils.EMPTY;
	public String select = "*";

	@Override
	public byte[] toBytes() throws IOException {
		assemblePayload();
		ByteBuffer buffer = ByteBuffer.allocate(2 + 2 + 4 + 4 + 4 + payload.length);
		buffer.putShort(Type.SEARCH.code);
		buffer.putShort((short) Version.COMMAND_SEARCH.code);
		buffer.putInt(payload.length + 4 + 4);
		/*client标记*/
		buffer.putInt(0);
		/*每次只有1个search,其实是支持同时发多个query的*/
		buffer.putInt(1);
		buffer.put(payload);
		return buffer.array();
	}

	private void assemblePayload() throws IOException {
		ByteArrayOutputStream req = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(req);
		try {
			out.writeInt(offset);
			out.writeInt(limit);
			out.writeInt(mode.code);
			out.writeInt(rank.code);
			out.write(sort.toBytes());
			Utils.writeNetUTF8(out, query);
			writeWeights(out);
			Utils.writeNetUTF8(out, index);
			writeIdRange(out);
			writeFilters(out);
			out.write(group.toBytes());
			out.write(anchor.toBytes());
			writeIndexWeights(out);
			out.writeInt(maxQueryTime);
			writeFieldWeights(out);
			Utils.writeNetUTF8(out, comment);
			writeOverrides(out);
			Utils.writeNetUTF8(out, select);
			out.flush();
			payload = req.toByteArray();
		} finally {
			IOUtils.closeQuietly(out);
			IOUtils.closeQuietly(req);
		}
	}

	private void writeWeights(DataOutputStream out) throws IOException {
		out.writeInt(weights == null ? 0 : weights.length);
		if (weights == null) {
			return;
		}

		for (int weight : weights) {
			out.writeInt(weight);
		}
	}

	private void writeIdRange(DataOutputStream out) throws IOException {
		out.writeInt(0);
		out.writeInt(minId);
		out.writeInt(maxId);
	}

	private void writeFilters(DataOutputStream out) throws IOException {
		out.writeInt(filters.size());
		if (filters == null || filters.isEmpty()) {
			out.write(new byte[0]);
			return;
		}

		for (Filter filter : filters) {
			byte[] array = filter.toBytes();
			if (array.length > 0) {
				out.write(array);
			}
		}
	}

	private void writeIndexWeights(DataOutputStream out) throws IOException {
		out.writeInt(indexWeights.size());
		for (String index : indexWeights.keySet()) {
			Integer weight = indexWeights.get(index);
			Utils.writeNetUTF8(out, index);
			out.writeInt(weight.intValue());
		}
	}

	private void writeFieldWeights(DataOutputStream out) throws IOException {
		out.writeInt(fieldWeights.size());
		for (String field : fieldWeights.keySet()) {
			Integer weight = fieldWeights.get(field);
			Utils.writeNetUTF8(out, field);
			out.writeInt(weight.intValue());
		}
	}

	private void writeOverrides(DataOutputStream out) throws IOException {
		out.writeInt(0);
	}

	public static void main(String[] args) throws IOException {
		Search search = new Search("电信", "test2");
		search.assemblePayload();
		System.out.println(Arrays.toString(search.payload));
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
				.append("offset", offset)
				.append("limit", limit)
				.append("mode", mode)
				.append("rank", rank)
				.append("sort", sort)
				.append("query", query)
				.append("weights", weights)
				.append("index", index)
				.append("minId", minId)
				.append("maxId", maxId)
				.append("filters", filters)
				.append("group", group)
				.append("anchor", anchor)
				.append("indexWeights", indexWeights)
				.append("maxQueryTime", maxQueryTime)
				.append("fieldWeights", fieldWeights)
				.append("comment", comment)
				.append("select", select)
				.toString();
	}

	public enum MatchMode {

		ALL(0),
		ANY(1),
		PHRASE(2),
		BOOLEAN(3),
		EXTENDED(4),
		FULLSCAN(5),
		EXTENDED2(6);

		private MatchMode(int code) {
			this.code = code;
		}

		public final int code;

	}

	public enum RankMode {

		PROXIMITY_BM25(0),
		BM25(1),
		NONE(2),
		WORDCOUNT(3),
		PROXIMITY(4),
		MATCHANY(5),
		FIELDMASK(6),
		SPH04(7),
		EXPR(8),
		TOTAL(9);

		private RankMode(int code) {
			this.code = code;
		}

		public final int code;

	}
}
