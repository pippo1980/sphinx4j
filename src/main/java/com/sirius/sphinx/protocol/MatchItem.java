package com.sirius.sphinx.protocol;

import com.sirius.sphinx.Utils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pippo on 14-7-5.
 */
public class MatchItem {

	public void parse(int id64, List<Attribute> columns, ByteBuffer buffer) throws IOException {
		this.id = id64 == 0 ? Utils.readDword(buffer) : buffer.getLong();
		this.weight = buffer.getInt();

		for (Attribute column : columns) {
			Attribute attribute = column.clone();
			attribute.parse(buffer);
			attributes.add(attribute);
		}
	}

	public long id;
	public int weight;
	public List<Attribute> attributes = new ArrayList<Attribute>();

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
				.append("id", id)
				.append("weight", weight)
				.append("attributes", attributes)
				.toString();
	}
}
