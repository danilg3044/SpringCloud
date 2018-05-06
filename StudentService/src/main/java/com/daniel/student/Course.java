package com.daniel.student;

import java.util.Calendar;
import java.util.List;

class Course {
	long id;
	String name;
	Calendar start;
	Calendar end;
	List<Student> students;

	public Course() {		
	}
	
	public Course(long id, String name, Calendar start, Calendar end, List<Student> students) {
		this.name = name;
		this.id = id;
		this.start = start;
		this.end = end;
		this.students = students;
	}
	
	public List<Student> getStudents() {
		return students;
	}

	public void setStudents(List<Student> students) {
		this.students = students;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Calendar getStart() {
		return start;
	}

	public void setStart(Calendar start) {
		this.start = start;
	}

	public Calendar getEnd() {
		return end;
	}

	public void setEnd(Calendar end) {
		this.end = end;
	}
}