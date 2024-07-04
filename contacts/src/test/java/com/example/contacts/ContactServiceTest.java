package com.example.contacts;

import com.example.contacts.client.KenectLabsClient;
import com.example.contacts.model.Contact;
import com.example.contacts.model.KenectLabsResponse;
import com.example.contacts.service.ContactService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ContactServiceTest {

	@Autowired
	private ContactService contactService;

	@MockBean
	private KenectLabsClient kenectLabsClient;

	@Test
	void getAllContacts_whenThirdApiHasOneSinglePage_shouldReturnListHavingIt() {
		List<Contact> mockContacts = new ArrayList<>();
		mockContacts.add(new Contact(1L, "Fred Jones", "fred@example.com"));
		KenectLabsResponse mockResponse = new KenectLabsResponse(mockContacts);

		when(kenectLabsClient.getContacts(1)).thenReturn(ResponseEntity.ok(mockResponse));

		List<Contact> results = contactService.getAllContacts();

		assertEquals(1, results.size());
		assertEquals("Fred Jones", results.get(0).getName());

		verify(kenectLabsClient).getContacts(1);
	}

	@Test
	void getAllContacts_whenThirdApiRetrievingGoesRight_shouldReturnAllAvailableData() {
		List<Contact> firstPageContacts = new ArrayList<>();
		firstPageContacts.add(new Contact(1L, "Fred Silva", "silva@example.com"));
		KenectLabsResponse firstPageResponse = new KenectLabsResponse(firstPageContacts);

		List<Contact> secondPageContacts = new ArrayList<>();
		secondPageContacts.add(new Contact(2L, "Olinda Henriques", "olinda@example.com"));
		KenectLabsResponse secondPageResponse = new KenectLabsResponse(secondPageContacts);

		HttpHeaders headers = new HttpHeaders();
		headers.add("Total-Pages", "2");
		when(kenectLabsClient.getContacts(1)).thenReturn(ResponseEntity.ok().headers(headers).body(firstPageResponse));
		when(kenectLabsClient.getContacts(2)).thenReturn(ResponseEntity.ok(secondPageResponse));

		List<Contact> results = contactService.getAllContacts();

		assertEquals(2, results.size());
		assertEquals("Olinda Henriques", results.get(1).getName());

		verify(kenectLabsClient).getContacts(1);
		verify(kenectLabsClient).getContacts(2);
	}

	@Test
	void getAllContacts_whenFirstThirdApiCheckingFails_shouldReturnEmptyList() {
		when(kenectLabsClient.getContacts(1)).thenReturn(ResponseEntity.internalServerError().build());

		var result = contactService.getAllContacts();

		assertTrue(result.isEmpty());

		verify(kenectLabsClient).getContacts(1);
	}

	@Test
	void getAllContacts_whenFirstPageReturnsNullAndSecondHasData_shouldReturnListWithDataFromSecondPage() {
		KenectLabsResponse firstPageResponse = new KenectLabsResponse(null);
		HttpHeaders headers = new HttpHeaders();
		headers.add("Total-Pages", "2");

		List<Contact> secondPageContacts = new ArrayList<>();
		secondPageContacts.add(new Contact(2L, "Olinda Henriques", "olinda@example.com"));
		KenectLabsResponse secondPageResponse = new KenectLabsResponse(secondPageContacts);

		when(kenectLabsClient.getContacts(1)).thenReturn(ResponseEntity.ok().headers(headers).body(firstPageResponse));
		when(kenectLabsClient.getContacts(2)).thenReturn(ResponseEntity.ok(secondPageResponse));

		var result = contactService.getAllContacts();

		assertEquals(secondPageContacts, result);
		assertEquals("Olinda Henriques", result.get(0).getName());
		verify(kenectLabsClient).getContacts(1);
		verify(kenectLabsClient).getContacts(2);
	}
}
