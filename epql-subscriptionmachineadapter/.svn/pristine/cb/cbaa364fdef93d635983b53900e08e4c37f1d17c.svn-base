package com.ge.dspmicro.energy.xml.configuration;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "CustomAdapterConfig")
public class CustomAdapterConfig {

	@JsonIgnoreProperties(ignoreUnknown = true)
	@JacksonXmlProperty(localName = "DataPullingInterval")
	private String dataPullingInterval;
	
	@JsonIgnoreProperties(ignoreUnknown = true)
	@JacksonXmlProperty(localName = "DataSource")
	private String dataSource;
	
	@JacksonXmlElementWrapper(localName = "datasubscriptionconfigs", useWrapping = false)
	private Datasubscriptionconfigs[] datasubscriptionconfigs;

	public CustomAdapterConfig() {
	}

	public CustomAdapterConfig(Datasubscriptionconfigs[] datasubscriptionconfigs) {
		this.datasubscriptionconfigs = datasubscriptionconfigs;
	}
	
	public CustomAdapterConfig(String dataPullingInterval, String dataSource, Datasubscriptionconfigs[] datasubscriptionconfigs) {
		super();
		this.dataPullingInterval = dataPullingInterval;
		this.dataSource = dataSource;
		this.datasubscriptionconfigs = datasubscriptionconfigs;
	}

	public String getDataPullingInterval() {
		return dataPullingInterval;
	}

	public void setDataPullingInterval(String dataPullingInterval) {
		this.dataPullingInterval = dataPullingInterval;
	}

	public String getDataSource() {
		return dataSource;
	}

	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}

	public Datasubscriptionconfigs[] getDatasubscriptionconfigs() {
		return datasubscriptionconfigs;
	}

	public void setDatasubscriptionconfigs(Datasubscriptionconfigs[] datasubscriptionconfigs) {
		this.datasubscriptionconfigs = datasubscriptionconfigs;
	}

	@Override
	public String toString() {
		String jSonStr ="{";
		jSonStr +="\"DataPullingInterval\":\""+ dataPullingInterval +  "\",";
		jSonStr += "\"DataSource\":\""+ dataSource +  "\",";
		jSonStr += "\"" + "datasubscriptionconfigs\":" + Arrays.toString(datasubscriptionconfigs);
		jSonStr +="}";
		
		return jSonStr;
	}

}
