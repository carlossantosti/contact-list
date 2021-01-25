package com.nagel.contacts.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.nagel.contacts.model.Contact;
import com.nagel.contacts.repository.ContactRepository;

/**
 * Main methods to Contact.
 * 
 * @author Carlos
 *
 */
@Service
public class ContactService {
	private final Integer pageDefault = 1;
	private final Integer sizeDefault = 50;
	
	@Autowired
	ContactRepository contactRepository;

	/**
	 * Save a list of contacts to the database.
	 * 
	 * @param contacts - List of Contacts.
	 */
	public void saveContacts(List<Contact> contacts) {
		contacts.forEach((c) -> {
			this.contactRepository.save(c);
		});
	}

	/**
	 * Get contacts from the database by page.
	 * Return paginated contacts.
	 * 
	 * @param page - The page to start search.
	 * @param size - How much contacts per page.
	 * @return Map with a list of contacts (Page<Contact>) and a pageNumbers.
	 */
	public Map<String, Object> getPaginatedContacts(Optional<Integer> page, Optional<Integer> size) {
		int currentPage = (Integer) page.orElse(pageDefault);
		int pageSize = (Integer) size.orElse(sizeDefault);

		Page<Contact> contactsPage = contactRepository.findAll(PageRequest.of(currentPage - 1, pageSize));
		
		return preparePaginatedContacts(contactsPage);
	}

	/**
	 * Filter contacts from the database using name.
	 * Return paginated contacts.
	 * 
	 * @param name - The name to be searched.
	 * @param page - The page to start search.
	 * @param size - How much contacts per page.
	 * @return Map with a list of contacts (Page<Contact>) and a pageNumbers.
	 */
	public Map<String, Object> findByNamePaginatedContacts(String name, Optional<Integer> page,
			Optional<Integer> size) {
		int currentPage = page.orElse(pageDefault);
		int pageSize = size.orElse(sizeDefault);

		Page<Contact> contactsPage = contactRepository.findByNameContaining(name,
				PageRequest.of(currentPage - 1, pageSize));
		
		return preparePaginatedContacts(contactsPage);
	}

	/**
	 * Creates de Map to be send to the view.
	 * 
	 * @param contactsPage - Page<Contact> to be transformed to view information paginated.
	 * @return Map with a list of contacts (Page<Contact>) and a pageNumbers.
	 */
	private Map<String, Object> preparePaginatedContacts(Page<Contact> contactsPage) {
		Map<String, Object> model = new HashMap<>();
		model.put("contacts", contactsPage);

		int totalPages = contactsPage.getTotalPages();
		if (totalPages > 0) {
			List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages).boxed().collect(Collectors.toList());
			model.put("pageNumbers", pageNumbers);
		}

		return model;
	}
}
