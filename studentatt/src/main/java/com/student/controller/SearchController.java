package com.student.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.student.dao.ContactRepository;
import com.student.dao.StudentRepository;
import com.student.model.Contact;
import com.student.model.Student;

@RestController
public class SearchController {
	@Autowired
	private StudentRepository studentRepository;
	@Autowired
	private ContactRepository contactRepository;
	//search handler
	@GetMapping("/search/{query}")
	public ResponseEntity<?> search(@PathVariable("query") String query, Principal principal){
		System.out.println(query);
		Student student = this.studentRepository.getUserByUserName(principal.getName());
		List<Contact> contacts = this.contactRepository.findByNameContainingAndStudent(query, student);
		return ResponseEntity.ok(contacts);
	}
}
