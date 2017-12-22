package com.ge.dspmicro.energy.xml.configuration;

import java.util.Arrays;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/**
 * 
 * @author Nadeem.Ahmed
 *
 */
public class Datasubscriptionconfigs {

	@JacksonXmlElementWrapper(localName = "datasubscriptionconfig", useWrapping = false)
	private Datasubscriptionconfig[] datasubscriptionconfig;

	public Datasubscriptionconfigs() {

	}

	public Datasubscriptionconfigs(Datasubscriptionconfig[] datasubscriptionconfig) {
		super();
		this.datasubscriptionconfig = datasubscriptionconfig;
	}

	public Datasubscriptionconfig[] getDatasubscriptionconfig() {
		return datasubscriptionconfig;
	}

	public void setDatasubscriptionconfig(Datasubscriptionconfig[] datasubscriptionconfig) {
		this.datasubscriptionconfig = datasubscriptionconfig;
	}

	@Override
	public String toString() {
		return Arrays.toString(datasubscriptionconfig);
	}

}
