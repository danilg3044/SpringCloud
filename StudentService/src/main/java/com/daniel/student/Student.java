package com.daniel.student;

import java.util.List;

class Student {
	String id;
	String address;
	String cls;
	List<Course> courses;
	
	public Student() {
	}

	public Student(String id, String address, String cls, List<Course> courses) {
		this.id = id;
		this.address = address;
		this.cls = cls;
		this.courses = courses;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public void setCls(String cls) {
		this.cls = cls;
	}

	public void setCourses(List<Course> courses) {
		this.courses = courses;
	}

	public List<Course> getCourses() {
		return courses;
	}

	public String getId() {
		return id;
	}

	public String getAddress() {
		return address;
	}

	public String getCls() {
		return cls;
	}
}
