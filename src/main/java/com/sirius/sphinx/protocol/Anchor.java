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
public class Anchor {

	public String latitudeAttr = null;
	public String longitudeAttr = null;
	public float latitude = 0;
	public float longitude = 0;

	public byte[] toBytes() throws IOException {

		ByteArrayOutputStream req = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(req);

		try {
			if (latitudeAttr == null || latitudeAttr.length() == 0 || longitudeAttr == null
					|| longitudeAttr.length() == 0) {
				out.writeInt(0);
			} else {
				out.writeInt(1);
				Utils.writeNetUTF8(out, latitudeAttr);
				Utils.writeNetUTF8(out, longitudeAttr);
				out.writeFloat(latitude);
				out.writeFloat(longitude);
			}

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
				.append("latitudeAttr", latitudeAttr)
				.append("longitudeAttr", longitudeAttr)
				.append("latitude", latitude)
				.append("longitude", longitude)
				.toString();
	}
}
