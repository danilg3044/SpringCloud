package com.daniel.student;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.daniel.config.SwaggerConfig;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

// http://localhost:8082/swagger-ui.html
// http://localhost:8082/v2/api-docs
// http://localhost:8082/hystrix -> enter http://localhost:8082/hystrix.stream

@RestController
@SpringBootApplication
@EnableCircuitBreaker
@EnableEurekaClient
@EnableHystrixDashboard
@Import(SwaggerConfig.class)
public class StudentApp {

	@Autowired
    private RestTemplate restTemplate;
  
	@Bean
	@LoadBalanced
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
	   return builder.build();
	}
	
	
	List<String> getAllStudentsDefault() {
        return Collections.emptyList();
    }
	
	@HystrixCommand(fallbackMethod = "getAllStudentsDefault")
	@Produces( { MediaType.APPLICATION_JSON  } )
	@RequestMapping(method = RequestMethod.GET, value = "/all")
	public @ResponseBody List<Student> getAllStudents() {
		//throw new Exception();
		return Arrays.asList(new Student("id11", "Toronto", "MCA", Collections.emptyList()),
				new Student("id22", "London", "ABC", Collections.emptyList()));
	}
	
	
	List<Student> getStudentsOfCourseDefault(long courseId) {
        return Collections.emptyList();
    }
	
	@HystrixCommand(fallbackMethod = "getStudentsOfCourseDefault")
	@Produces( { MediaType.APPLICATION_JSON  } )
	@RequestMapping(method = RequestMethod.GET, value = "/course/{courseId}")
	public @ResponseBody List<Student> getStudentsOfCourse(@PathVariable(name = "courseId") long courseId) {
		//throw new Exception();
		return Arrays.asList(new Student("id11", "Toronto", "MCA", Collections.emptyList()), new Student("id22", "London", "ABC", Collections.emptyList()));
	}

	
	Student getStudentByIdDefault(String name) {
		return new Student("id00", "Tel-Aviv", "MPH", Collections.emptyList());
    }
	
	@HystrixCommand(fallbackMethod = "getStudentByIdDefault")
	@Produces( { MediaType.APPLICATION_JSON } )
	@RequestMapping(method = RequestMethod.GET, value = "/student/{studentId}")
	public Student getStudentById(@PathVariable(name = "studentId") String studentId) {
		return new Student(studentId, "Pune", "XYZ", Collections.emptyList());
	}
	
	
	Student getStudentsCoursesDefault(String studentId) {
		return new Student(studentId, "", "", Collections.emptyList());
    }
	
	@HystrixCommand(fallbackMethod = "getStudentsCoursesDefault")
	@RequestMapping(method = RequestMethod.GET, value = "/student/{studentId}/courses")
	public Student getStudentsCourses(@PathVariable(name = "studentId") String studentId) {
		Student student = new Student(studentId, "Daniel", "QWERTY", Collections.emptyList());
	    ResponseEntity<List<Course>> courses = restTemplate.exchange("http://course-service/courses/{studentId}", HttpMethod.GET,
	    											null, new ParameterizedTypeReference<List<Course>>() {}, (Object) studentId);
	    student.setCourses(courses.getBody());
	    return student;
	}

	public static void main(String[] args) {
		SpringApplication.run(StudentApp.class, args);
	}
}

