package com.nagel.contacts.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.nagel.contacts.model.Contact;
import com.nagel.contacts.repository.ContactRepository;
import com.nagel.contacts.service.ContactService;
import com.nagel.contacts.utils.CSVHelper;

/**
 * Controller the interaction between the system and the view.
 * 
 * @author Carlos
 *
 */
@Controller
public class ContactController {
	private final Optional<Integer> pageDefault = Optional.of(1);
	private final Optional<Integer> sizeDefault = Optional.of(50);
	
	@Autowired
	ContactService contactService;
	@Autowired
	ContactRepository contactRepository;

	/**
	 * Get contacts paginated.
	 * 
	 * @param model - Java-5-specific interface that defines a holder for model attributes.
	 * @param page - The page to start.
	 * @param size - How much contacts per page.
	 * @return route.
	 */
	@GetMapping({ "/" })
	public String getContacts(Model model, @RequestParam("page") Optional<Integer> page,
			@RequestParam("size") Optional<Integer> size) {
		model.mergeAttributes(contactService.getPaginatedContacts(page, size));
		return "index";
	}

	/**
	 * Filter contacts by name.
	 * Set Paginated data to the view.
	 * 
	 * @param model - Java-5-specific interface that defines a holder for model attributes.
	 * @param searchText - The value to be searched.
	 * @param page - The page to start search.
	 * @param size - How much contacts per page.
	 * @return route.
	 */
	@GetMapping({ "/search" })
	public String getContactsByFilter(Model model, @RequestParam("search-text") String searchText,
			@RequestParam("page") Optional<Integer> page, @RequestParam("size") Optional<Integer> size) {
		if (searchText.equals("")) {
			model.mergeAttributes(contactService.getPaginatedContacts(pageDefault, sizeDefault));
			model.addAttribute("search", false);
		} else {
			model.mergeAttributes(contactService.findByNamePaginatedContacts(searchText, page, size));
			model.addAttribute("search", true);
		}

		model.addAttribute("searchText", searchText);
		return "index";
	}

	/**
	 * Upload the data from CSV File.
	 * 
	 * @param file - The csv file to be transformed into contacts.
	 * @param model - Java-5-specific interface that defines a holder for model attributes.
	 * @return route.
	 */
	@PostMapping({ "/upload-contacts" })
	public String postContacts(@RequestParam("file") MultipartFile file, Model model) {
		Map<String, Object> uploadInfo = new HashMap<>();

		try {
			List<Contact> contacts = CSVHelper.csvToContacts(file.getInputStream());
			if (contacts.isEmpty()) {
				throw new Exception("No contacts were found in the file uploaded");
			}

			contactService.saveContacts(contacts);
			uploadInfo.put("error", false);
		} catch (Exception e) {
			uploadInfo.put("error", true);
			uploadInfo.put("message", e.getMessage());
		}

		model.mergeAttributes(uploadInfo);
		model.mergeAttributes(contactService.getPaginatedContacts(pageDefault, sizeDefault));
		return "/index";
	}

	/**
	 * Delete all contacts.
	 * 
	 * @param model - Java-5-specific interface that defines a holder for model attributes.
	 * @return route.
	 */
	@GetMapping({ "/delete-all" })
	public String getDeleteAllContacts(Model model) {
		contactRepository.deleteAll();
		model.mergeAttributes(contactService.getPaginatedContacts(pageDefault, sizeDefault));
		return "/index";
	}
}
