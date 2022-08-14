package com.student.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.student.dao.ContactRepository;
import com.student.dao.StudentRepository;
import com.student.helper.Message;
import com.student.model.Contact;
import com.student.model.Student;

@Controller
@RequestMapping("/student")
public class StudentController {

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	private StudentRepository studentRepository;

	@Autowired
	private ContactRepository contactRepository;

	// method for adding common data to response
	@ModelAttribute
	public void addCommonData(Model model, Principal principal) {
		String userName = principal.getName();
		System.out.println("Username " + userName);

		// get the student using username(Email)
		Student student = this.studentRepository.getUserByUserName(userName);
		System.out.println("Student" + student);
		model.addAttribute("student", student);
	}

	// dashboard home
	@RequestMapping("/index")
	public String dashboard(Model model, Principal principal) {
		model.addAttribute("title", "Home");
		return "normal/student_dashboard";
	}

	// open add form handler
	@GetMapping("/add-contact")
	public String openAddContactForm(Model model) {
		model.addAttribute("title", "Add Contact");
		model.addAttribute("contact", new Contact());
		return "normal/add_contact_form";
	}

	// processing add contact
	@PostMapping("/process-contact")
	public String processContact(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file,
			Principal principal, HttpSession session) {
		try {
			String name = principal.getName();
			Student student = this.studentRepository.getUserByUserName(name);

			// processing and uploading file
			if (file.isEmpty()) {
				contact.setImage("contact.png");
				System.out.println("File is Empty");
			} else {
				contact.setImage(file.getOriginalFilename());
				File saveFile = new ClassPathResource("static/img").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				System.out.println("Image is uploaded");
			}

			student.getContacts().add(contact);
			contact.setStudent(student);

			this.studentRepository.save(student);
			System.out.println("Data: " + contact);
			System.out.println("Added to database");
			// message success
			session.setAttribute("message", new Message("Your Contact is added !!! Add More...", "success"));
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
			e.printStackTrace();
			// message error
			session.setAttribute("message", new Message("Something went wrong !! Try Again..", "danger"));
		}

		return "normal/add_contact_form";
	}

	// show contact handler
	// per page = 5[n]
	// current page = 0[page]
	@GetMapping("/show-contacts/{page}")
	public String showContacts(@PathVariable("page") int page, Model model, Principal pricipal) {
		model.addAttribute("title", "all contacts");

		String userName = pricipal.getName();
		Student student = this.studentRepository.getUserByUserName(userName);

		Pageable pageable = PageRequest.of(page, 4);
		Page<Contact> contacts = this.contactRepository.findContactsByUser(student.getId(), pageable);
		model.addAttribute("contacts", contacts);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", contacts.getTotalPages());

		return "normal/show_contacts";
	}

	// showing particular contact details
	@GetMapping("/{cId}/contact")
	public String showContactDetail(@PathVariable("cId") Integer cId, Model model, Principal principal) {
		System.out.println("Cid" + cId);
		Optional<Contact> contactOption = this.contactRepository.findById(cId);
		Contact contact = contactOption.get();

		// check
		String userName = principal.getName();
		Student student = this.studentRepository.getUserByUserName(userName);

		if (student.getId() == contact.getStudent().getId()) {
			model.addAttribute("title", "details- " + contact.getName());
			model.addAttribute("contactDetail", contact);
		} else {
			model.addAttribute("title", "Access Denied");
		}

		return "normal/contact_detail";
	}

	// delete contact handler
	@GetMapping("/delete/{cId}")
	@Transactional
	public String deleteContact(@PathVariable("cId") int cId, Model model, Principal principal, HttpSession session) {

		Contact contact = this.contactRepository.findById(cId).get();

		Student student = this.studentRepository.getUserByUserName(principal.getName());
		student.getContacts().remove(contact);
		this.studentRepository.save(student);

		session.setAttribute("message", new Message("Contact Deleted Successfully...", "success"));
		return "redirect:/student/show-contacts/0";
	}

	// open update form handler
	@PostMapping("/update-contact/{cId}")
	public String updateForm(@PathVariable("cId") Integer cId, Model model) {
		model.addAttribute("title", "Update Contact");
		Contact contact = this.contactRepository.findById(cId).get();
		model.addAttribute("contact", contact);
		return "normal/update_form";
	}

	// update contact handler
	@PostMapping("/process-update")
	public String updateContact(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file,
			Model model, HttpSession session, Principal principal) {
		try {
			// old contact details
			Contact oldContact = this.contactRepository.findById(contact.getcId()).get();
			if (!file.isEmpty()) {
				// delete old photo
				File deleteFile = new ClassPathResource("static/img").getFile();
				File file1 = new File(deleteFile, oldContact.getImage());
				file1.delete();

				// update new photo
				File saveFile = new ClassPathResource("static/img").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				contact.setImage(file.getOriginalFilename());
			} else {
				contact.setImage(oldContact.getImage());
			}
			Student student = this.studentRepository.getUserByUserName(principal.getName());
			contact.setStudent(student);
			this.contactRepository.save(contact);
			session.setAttribute("message", new Message("Contact is updated successfully...", "success"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "redirect:/student/" + contact.getcId() + "/contact";
	}

	// Your Profile Handler
	@GetMapping("/profile")
	public String yourProfile(Model model) {
		model.addAttribute("title", "My Profile");
		return "normal/profile";
	}

	// open setting handler
	@GetMapping("/settings")
	public String openSettings() {
		return "normal/settings";
	}

	// change password handler
	@PostMapping("/change-password")
	public String changePassword(@RequestParam("oldPassword") String oldPassword,
			@RequestParam("newPassword") String newPassword, Principal pricipal, HttpSession session) {
		System.out.println("Old pass "+oldPassword);
		System.out.println("New pass "+newPassword);
		
		String userName = pricipal.getName();
		Student currentStudent = this.studentRepository.getUserByUserName(userName);
		System.out.println("password: " +currentStudent.getPassword());
		
		if(this.bCryptPasswordEncoder.matches(oldPassword, currentStudent.getPassword())) {
			//change the password
			currentStudent.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
			this.studentRepository.save(currentStudent);
			session.setAttribute("message", new Message("Password changed successfully...", "success"));
		}else {
			//error
			session.setAttribute("message", new Message("Please enter correct Old Password!!", "danger"));
			return "redirect:/student/settings";
		}
		
		return "redirect:/student/index";
	}
}
