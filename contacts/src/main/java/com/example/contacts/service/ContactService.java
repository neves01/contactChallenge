package com.example.contacts.service;

import com.example.contacts.client.KenectLabsClient;
import com.example.contacts.model.Contact;
import com.example.contacts.model.KenectLabsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.http.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

@Service
public class ContactService {

    private static final Logger logger = LoggerFactory.getLogger(ContactService.class);
    private static final int THREAD_POOL_SIZE = 10;

    @Autowired
    private KenectLabsClient kenectLabsClient;

    private final ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

    public List<Contact> getAllContacts() {
        List<Contact> allContacts = new ArrayList<>();

        // First contacts page is already included into allContacts
        final int totalPages = getTotalPages(allContacts);

        List<CompletableFuture<List<Contact>>> futures = IntStream.rangeClosed(2, totalPages)
                .mapToObj(page -> CompletableFuture.supplyAsync(() -> fetchContacts(page), executorService))
                .toList();

        futures.forEach(future -> {
            try {
                allContacts.addAll(future.get());
            } catch (InterruptedException | ExecutionException e) {
                logger.error("Error fetching contacts in parallel", e);
            }
        });

        return allContacts;
    }

    private int getTotalPages(List<Contact> allContacts) {
        int totalPages = 1;
        final int firstPage = 1;
        try {
            ResponseEntity<KenectLabsResponse> firstResponse = kenectLabsClient.getContacts(firstPage);
            Optional.ofNullable(firstResponse.getBody())
                    .map(KenectLabsResponse::getContacts).ifPresent(allContacts::addAll);

            totalPages = Optional.ofNullable(firstResponse.getHeaders().getFirst("Total-Pages"))
                    .map(Integer::parseInt)
                    .orElse(1);

            logger.info("Total number of pages = {}", totalPages);
        } catch (Exception e) {
            logger.error("Error fetching initial contacts from Kenect Labs API", e);
        }
        return totalPages;
    }

    private List<Contact> fetchContacts(int page) {
        try {
            ResponseEntity<KenectLabsResponse> responseEntity = kenectLabsClient.getContacts(page);
            return Optional.ofNullable(responseEntity.getBody())
                    .map(KenectLabsResponse::getContacts)
                    .orElse(new ArrayList<>());
        } catch (Exception e) {
            logger.error("Error fetching contacts from Kenect Labs API for page {}", page, e);
            return new ArrayList<>();
        }
    }
}
