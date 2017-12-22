package com.ge.dspmicro.energy.xml.configuration;

import java.util.Arrays;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class Datasubscriptionconfig {
	
	@JacksonXmlProperty(localName = "subscription_name", isAttribute = true)
	private String subscription_name;
	
	@JacksonXmlElementWrapper(localName = "node", useWrapping = false)
	private Node[] node;
	
	public Datasubscriptionconfig() {
		
	}

	public Datasubscriptionconfig(String subscription_name, Node[] node) {
		super();
		this.subscription_name = subscription_name;
		this.node = node;
	}

	public String getSubscription_name() {
		return subscription_name;
	}

	public void setSubscription_name(String subscription_name) {
		this.subscription_name = subscription_name;
	}

	public Node[] getNode() {
		return node;
	}

	public void setNode(Node[] node) {
		this.node = node;
	}

	@Override
	public String toString() {
		return "Datasubscriptionconfig [subscription_name=" + subscription_name + ", node=" + Arrays.toString(node) + "]";
	}
	
}
