package com.nagel.contacts.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import com.nagel.contacts.model.Contact;

/**
 * Manipulate CSV File.
 * 
 * @author Carlos
 *
 */
public class CSVHelper {

	/**
	 * Read and transform CSV File to List of Contacts.
	 * 
	 * @param is InputStream from the CSV File.
	 * @return List of Contacts.
	 */
	public static List<Contact> csvToContacts(InputStream is) {
		try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
				CSVParser csvParser = new CSVParser(fileReader,
						CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim().withAllowMissingColumnNames());) {

			List<Contact> contacts = new ArrayList<>();
			Iterable<CSVRecord> csvRecords = csvParser.getRecords();

			for (CSVRecord csvRecord : csvRecords) {
				Contact contact = new Contact(csvRecord.get("name"), csvRecord.get("url"));
				contacts.add(contact);
			}

			return contacts;
		} catch (IOException e) {
			throw new RuntimeException("fail to parse CSV file: " + e.getMessage());
		}
	}

}
