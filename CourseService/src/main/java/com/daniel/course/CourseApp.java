package com.daniel.course;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.daniel.course.config.SwaggerConfig;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

// http://localhost:8081/hystrix -> http://localhost:8081/hystrix.stream

@RestController
@SpringBootApplication
@EnableCircuitBreaker
@EnableHystrixDashboard
@EnableEurekaClient
@EnableFeignClients
@Import(SwaggerConfig.class)
public class CourseApp {

	@Autowired
    private StudentClient studentClient;
	
	public static void main(String[] args) {
		SpringApplication.run(CourseApp.class, args);
	}
	
	String defaultEchoCourseName(String name) {
        return "Hello " + name;
    }
	
	@HystrixCommand(fallbackMethod = "defaultEchoCourseName")
	@RequestMapping(method = RequestMethod.GET, value = "/hello/{name}")
	public String echoCourseName(@PathVariable(name = "name") String name) {
		try {
			Thread.sleep(1000 * 20);
		}
		catch (InterruptedException e) {
		}
		
		return "Hello  " + name + " Responsed on : " + new Date();
	}

	
	Course defaultGetCourseDetails(String name) {
		return new Course(1, name, Calendar.getInstance(), Calendar.getInstance(), Collections.emptyList());
    }
	
	@HystrixCommand(fallbackMethod = "defaultGetCourseDetails")
	@RequestMapping(method = RequestMethod.GET, value = "/details/{name}")
	public Course courseDetails(@PathVariable(name = "name") String name) {
		List<Student> students = studentClient.getStudentsOfCourse(1);
		return new Course(1, name, Calendar.getInstance(), Calendar.getInstance(), students);
	}
	
	
	List<Course> defaultGetStudentCourses(String studentId) {
		return Collections.emptyList();
    }
	
	@HystrixCommand(fallbackMethod = "defaultGetStudentCourses")
	@RequestMapping(method = RequestMethod.GET, value = "/courses/{studentId}")
	public List<Course> getStudentCourses(@PathVariable(name = "studentId") String studentId) {
		return Arrays.asList(new Course(1, "Math", Calendar.getInstance(), Calendar.getInstance(), Collections.emptyList()), 
				new Course(2, "Biology", Calendar.getInstance(), Calendar.getInstance(), Collections.emptyList()));
	}
}

@FeignClient("student-service")
interface StudentClient {
    @RequestMapping(method = RequestMethod.GET, value = "/course/{courseId}")
    List<Student> getStudentsOfCourse(@PathVariable("courseId") long courseId);
}