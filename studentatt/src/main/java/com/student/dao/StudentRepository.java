package com.student.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.student.model.Student;

public interface StudentRepository extends JpaRepository<Student, Integer> {
	@Query("select s from Student s where s.email=:email")
	public Student getUserByUserName(@Param("email") String email);
}
