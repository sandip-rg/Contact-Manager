package com.student.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.student.model.Contact;
import com.student.model.Student;

public interface ContactRepository extends JpaRepository<Contact, Integer>{
	//contacts list of particular student
//	@Query("from Contact as c where c.student.id =:userId")
//	public List<Contact> findContactsByUser(@Param("userId") int userId);
	
	//pagination with contacts list of particular student
	@Query("from Contact as c where c.student.id =:userId")
	//currentpage - page
	//contacts per page - 5
	public Page<Contact> findContactsByUser(@Param("userId") int userId, Pageable pageable);
	
	//search
	public List<Contact> findByNameContainingAndStudent(String name, Student student);
}
