package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class ControllerClass {

	@Autowired
	private RestTemplate rest;
	
	
	@GetMapping("/getFiles/{id}")
	public ResponseEntity<Resource> getFile(@PathVariable(name="id") String id){
		System.out.println(id);
		String url="http://localhost:8082/files/{id}";
		HttpHeaders headers = new HttpHeaders();
		HttpEntity<?> request = new HttpEntity(headers);
		headers.setContentType(MediaType.APPLICATION_JSON);
		String url1=url.replace("{id}", id);
		System.out.println(url1);
		ResponseEntity<Resource> forEntity = rest.getForEntity( url1,Resource.class);
		return forEntity;
	}
	
	@GetMapping("/getFile/{id}")
	public ResponseEntity<Resource> getFile1(@PathVariable(name="id") String id){
		//System.out.println(id);
		String url="http://localhost:8082/certificate";
		Student s=new Student();
		s.setId(id);
		HttpEntity<Student> request = new HttpEntity(s);
		ResponseEntity<Resource> forEntity = rest.exchange(url,HttpMethod.POST,request,Resource.class);
		return forEntity;
	}
	
}
