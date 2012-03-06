package org.tynamo.editablecontent.entities;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Lob;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.tynamo.editablecontent.entities.RevisionedContent.RevisionedContentId;

@Entity
@IdClass(RevisionedContentId.class)
public class RevisionedContent {
	public static class RevisionedContentId implements Serializable {
		private static final long serialVersionUID = 1L;
		private String id;
		private long revision;

		public RevisionedContentId() {
		}

		public RevisionedContentId(String id, long revision) {
			this.id = id;
			this.revision = revision;
		}

		public String getId() {
			return id;
		}

		public long getRevision() {
			return revision;
		}

		public boolean equals(Object o) {
			return ((o instanceof RevisionedContentId) && id == ((RevisionedContentId) o).getId() && revision == ((RevisionedContentId) o)
				.getRevision());
		}

		public int hashCode() {
			return (id + revision).hashCode();
		}
	}

	public static final int CONTENT_MAX_LENGTH = 10000;

	@Id
	private String id;

	private long revision;

	@Lob
	// should be the default: @Basic(fetch = FetchType.LAZY)
	@Column(length = CONTENT_MAX_LENGTH)
	private byte[] value;

	private Date lastModified = new Date();

	public RevisionedContent() {
	}

	public RevisionedContent(TextualContent textualContent) {
		id = textualContent.getId();
		revision = textualContent.getVersion();
		lastModified = textualContent.getLastModified();

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		GZIPOutputStream gzip;
		try {
			gzip = new GZIPOutputStream(out);
			gzip.write(textualContent.getValue().getBytes());
			gzip.close();
		} catch (IOException e) {
			// ignore, shouldn't happen with a byte[]
		}
		value = out.toByteArray();
	}

	// TODO probably not the right place for this operation
	public String valueToString() {
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(value);
			GZIPInputStream gzis = new GZIPInputStream(bais);
			InputStreamReader reader = new InputStreamReader(gzis, Charset.forName("UTF-8"));
			BufferedReader in = new BufferedReader(reader);

			String line;
			StringBuilder sb = new StringBuilder();
			while ((line = in.readLine()) != null)
				sb.append(line);
			return sb.toString();

		} catch (IOException e) {
			// Ignore, shouldn't happen
			return null;
		}
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public byte[] getValue() {
		return value;
	}

	public void setValue(byte[] value) {
		this.value = value;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@NotNull
	@Column(nullable = false, updatable = false)
	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		if (lastModified != null) this.lastModified = lastModified;
	}

	@Id
	public long getRevision() {
		return revision;
	}

	public void setRevision(long revision) {
		this.revision = revision;
	}
}
