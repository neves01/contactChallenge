package com.example.contacts.client;

import com.example.contacts.model.KenectLabsResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "kenectLabsClient", url = "https://k-messages-api.herokuapp.com/api/v1")
public interface KenectLabsClient {

    @GetMapping("/contacts")
    ResponseEntity<KenectLabsResponse> getContacts(@RequestParam("page") int page);
}
