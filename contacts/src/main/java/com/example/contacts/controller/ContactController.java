package com.example.contacts.controller;

import com.example.contacts.model.Contact;
import com.example.contacts.service.ContactService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/contact-service/v1")
public class ContactController {

    @Autowired
    private ContactService contactService;

    @GetMapping("/contacts")
    public List<Contact> getAllContacts() {
        log.info("Request received to get all contacts");
        return contactService.getAllContacts();
    }
}
