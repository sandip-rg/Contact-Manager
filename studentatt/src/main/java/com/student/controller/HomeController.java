package com.student.controller;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.student.dao.StudentRepository;
import com.student.helper.Message;
import com.student.model.Student;

@Controller
public class HomeController {

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private StudentRepository studentRepository;

	@RequestMapping("/home")
	public String home(Model model) {
		model.addAttribute("title", "Home - Student Attendance");
		return "home";
	}

	@RequestMapping("/about")
	public String about(Model model) {
		model.addAttribute("title", "About - Student Attendance");
		return "about";
	}

	@RequestMapping("/signup")
	public String signup(Model model) {
		model.addAttribute("title", "Register - Student Attendance");
		model.addAttribute("student", new Student());
		return "signup";
	}

	// handler for register user
	@PostMapping("/do_register")
	public String registerUser(@Valid @ModelAttribute("student") Student student, BindingResult result1,
			@RequestParam(value = "agreement", defaultValue = "false") boolean agreement, Model model,
			HttpSession session) {

		try {
			if (!agreement) {
				System.out.println("you have not agreed the terms and conditions");
				throw new Exception("you have not agreed the terms and conditions");
			}

			if (result1.hasErrors()) {
				System.out.println("Error: " + result1.toString());
				model.addAttribute("student", student);
				return "signup";
			}

			student.setRole("ROLE_USER");
			student.setEnabled(true);
			student.setImageUrl("default.jpg");
			student.setPassword(passwordEncoder.encode(student.getPassword()));

			System.out.println("agreement " + agreement);
			System.out.println("Student	" + student);

			Student result = this.studentRepository.save(student);

			model.addAttribute("Student", new Student());

			session.setAttribute("message", new Message("Successfuly Registered", "alert-success"));
			return "signup";
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("student", student);
			session.setAttribute("message", new Message("Something went wrong !! " + e.getMessage(), "alert-danger"));
			return "signup";
		}
	}

	// handler for custom login
	@GetMapping("/signin")
	public String customLogin(Model model) {
		model.addAttribute("title", "Login - Student Attendance");
		return "login";
	}
}
