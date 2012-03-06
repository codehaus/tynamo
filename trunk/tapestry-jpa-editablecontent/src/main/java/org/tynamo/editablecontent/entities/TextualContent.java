package org.tynamo.editablecontent.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

@Entity
public class TextualContent {
	public static final int CONTENT_MAX_LENGTH = 100000;

	@Id
	@Column(length = 255)
	private String id;

	@Version
	private long version;

	@Lob
	// should be the default: @Basic(fetch = FetchType.LAZY)
	@Column(length = CONTENT_MAX_LENGTH)
	private String value;

	private Date lastModified = new Date();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false)
	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		if (lastModified != null) this.lastModified = lastModified;
	}

	public long getVersion() {
		return version;
	}
}
