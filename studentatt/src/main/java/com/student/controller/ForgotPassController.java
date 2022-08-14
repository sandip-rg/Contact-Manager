package com.student.controller;

import java.util.Random;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ForgotPassController {
	Random random = new Random(1000);

	// email id form open handler
	@RequestMapping("/forgot")
	public String openEmailForm() {
		return "forgot_email_form";
	}

	@RequestMapping("/send-otp")
	public String sendOTP(@RequestParam("username") String email) {
		System.out.println("Email: " + email);

		// generating 4 digit otp
		int otp = random.nextInt(999999);
		System.out.println("OTP: " + otp);

		// write code for send otp to email

		return "verify_otp";
	}
}
