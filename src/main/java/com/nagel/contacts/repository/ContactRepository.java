package com.nagel.contacts.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.nagel.contacts.model.Contact;

/**
 * 
 * Interface responsible to retrieve data using the pagination and
 * sorting abstraction.
 * 
 * @author Carlos
 *
 */
public interface ContactRepository extends PagingAndSortingRepository<Contact, Integer> {
	
	/**
	 * Filter contacts by Name.
	 * 
	 * @param name - The name to be searched.
	 * @param pageable - The interface for pagination information.
	 * @return List of Contacts Paginated.
	 */
    Page<Contact> findByNameContaining(String name, Pageable pageable);
    
}
