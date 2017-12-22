package com.ge.dspmicro.energy.xml.exp;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class Employee {

	@JacksonXmlProperty(localName = "id", isAttribute = true)
	private String id;
	
	@JacksonXmlProperty(localName = "first_name")
	private String firstName;
	
	@JacksonXmlProperty(localName = "last_name")
	private String lastName;
	
	@JacksonXmlProperty(localName = "age")
	private int age;
	
	@JacksonXmlProperty(localName = "pot_current")
	private Double pot_current;
	

	public Employee() {
	}

	public Employee(String id, String firstName, String lastName, int age, Double pot_current) {
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.age = age;
		this.pot_current= pot_current;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}
	
	public Double getPot_current() {
		return pot_current;
	}

	public void setPot_current(Double pot_current) {
		this.pot_current = pot_current;
	}


	@Override
	public String toString() {
		return "{\"id\":\"" + id + "\",\"firstName\":\"" + firstName + "\",\"lastName\":\"" + lastName + "\",\"age\":\"" + age + "\",\"pot_current\":\"" + pot_current + "\"}";
	}

}
