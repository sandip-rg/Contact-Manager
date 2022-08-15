package com.student.controller;

import java.util.Random;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.student.service.EmailService;

@Controller
public class ForgotPassController {
	Random random = new Random(1000);
	
	@Autowired
	private EmailService emailService;

	// email id form open handler
//	@RequestMapping("/forgot")
//	public String openEmailForm() {
//		return "forgot_email_form";
//	}

	@RequestMapping("/send-otp")
	public String sendOTP(@RequestParam("username") String email, HttpSession session) {
		System.out.println("Email: " + email);

		// generating 4 digit otp
		int otp = random.nextInt(999999);
		System.out.println("OTP: " + otp);

		// write code for send otp to email
		String subject = "OTP From SCM";
		String message = "<h1> OTP = " +otp+ "</h1>";	
		String to = email;
		boolean flag = this.emailService.sendEmail(subject, message, to);
		
		if(flag) {
			return "verify_otp";
		}else {
			session.setAttribute("message", "Check your email id !!");
			return "forgot_email_form";
		}

		
	}
}
