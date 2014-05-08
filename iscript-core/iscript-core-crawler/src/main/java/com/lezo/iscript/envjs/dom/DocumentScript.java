package com.lezo.iscript.envjs.dom;

import org.w3c.dom.Document;

public class DocumentScript {
	private Document document;
	private LocationScript location;

	public DocumentScript(Document document, LocationScript location) {
		super();
		this.document = document;
		this.location = location;
	}

	public Document getDocument() {
		return document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}

	public LocationScript getLocation() {
		return location;
	}

	public void setLocation(LocationScript location) {
		this.location = location;
	}
}
