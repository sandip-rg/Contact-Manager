package com.student.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.student.dao.StudentRepository;
import com.student.model.Student;

public class StudentDetailsServiceImpl implements UserDetailsService{

	@Autowired
	private StudentRepository studentRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		//fetching student from database
		Student student = studentRepository.getUserByUserName(username);
		if(student == null) {
			throw new UsernameNotFoundException("Could not found user!!!");
		}
		
		CustomStudentDetails customStudentDetails = new CustomStudentDetails(student);
		return customStudentDetails;
	}

}
