package com.sirius.sphinx.protocol;

import com.sirius.sphinx.Utils;
import com.sirius.sphinx.protocol.Attribute.Type;
import com.sirius.sphinx.protocol.Response.Status;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pippo on 14-7-5.
 */
public class MatchPage {

	public MatchPage(int offset, int limit, Status status) {
		this.offset = offset;
		this.limit = limit;
		this.status = status;
	}

	public MatchPage parse(ByteBuffer buffer) throws IOException {
		parseFields(buffer);
		parseItems(buffer);
		parseHit(buffer);
		return this;
	}

	private void parseFields(ByteBuffer buffer) throws IOException {
		int fieldCount = buffer.getInt();
		for (int i = 0; i < fieldCount; i++) {
			fields.add(Utils.readNetUTF8(buffer));
		}
	}

	private void parseItems(ByteBuffer buffer) throws IOException {
		/*返回的*/
		int attrCount = buffer.getInt();
		for (int i = 0; i < attrCount; i++) {
			columns.add(new Attribute(Utils.readNetUTF8(buffer), Type.from(buffer.getInt()), StringUtils.EMPTY));
		}

		/* read match count */
		int count = buffer.getInt();
		int id64 = buffer.getInt();
		for (int i = 0; i < count; i++) {
			MatchItem item = new MatchItem();
			item.parse(id64, columns, buffer);
			items.add(item);
		}
	}

	private void parseHit(ByteBuffer buffer) throws IOException {
		match = buffer.getInt();
		found = buffer.getInt();
		cost = buffer.getInt();
		int hit_count = buffer.getInt();
		for (int i = 0; i < hit_count; i++) {
			hits.add(new Hit(Utils.readNetUTF8(buffer), Utils.readDword(buffer), Utils.readDword(buffer)));
		}
	}

	public int offset;
	public int limit;
	public Status status;
	public List<String> fields = new ArrayList<String>();
	public List<Attribute> columns = new ArrayList<Attribute>();
	public List<MatchItem> items = new ArrayList<MatchItem>();
	public int match;
	public int found;
	public int cost;
	public List<Hit> hits = new ArrayList<Hit>();

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
				.append("offset", offset)
				.append("limit", limit)
				.append("status", status)
				.append("fields", fields)
				.append("columns", columns)
				.append("items", items)
				.append("match", match)
				.append("found", found)
				.append("cost", cost)
				.append("hits", hits)
				.toString();
	}

	public static class Hit {

		public Hit(String word, long docs, long hits) {
			this.word = word;
			this.docs = docs;
			this.hits = hits;
		}

		/** Word form as returned from search daemon, stemmed or otherwise postprocessed. */
		public String word;

		/** Total amount of matching documents in collection. */
		public long docs;

		/** Total amount of hits (occurences) in collection. */
		public long hits;

		@Override
		public String toString() {
			return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
					.append("word", word)
					.append("docs", docs)
					.append("hits", hits)
					.toString();
		}
	}

}
