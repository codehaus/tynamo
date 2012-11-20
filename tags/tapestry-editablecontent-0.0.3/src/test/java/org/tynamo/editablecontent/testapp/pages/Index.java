package org.tynamo.editablecontent.testapp.pages;

import java.util.Date;

import org.apache.tapestry5.alerts.AlertManager;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

public class Index {
	@Persist
	private String textValue;

	@Inject
	private AlertManager alertManager;

	public Date getCurrentTime() {
		return new Date();
	}

	public String getTextValue() {
		return textValue;
	}

	public void setTextValue(String textValue) {
		this.textValue = textValue;
		alertManager.info(textValue);
	}

	@Inject
	private Request request;

	// @InjectComponent
	// private Zone textAreaZone;
	//
	// Object onSuccess() {
	// return request.isXHR() ? textAreaZone.getBody() : null;
	// }
	//
	// Object onFailure() {
	// return request.isXHR() ? textAreaZone.getBody() : null;
	// }

}
