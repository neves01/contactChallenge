package com.example.contacts.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
public class Contact {
    private Long id;
    private String name;
    private String email;
    private String source;
    @JsonProperty("created_at")
    private Instant createdAt;
    @JsonProperty("updated_at")
    private Instant updatedAt;

    private static final String DEFAULT_SOURCE_VALUE = "KENECT_LABS";

    public Contact() {
        this.source = DEFAULT_SOURCE_VALUE;
    }

    public Contact(Long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.source = DEFAULT_SOURCE_VALUE;
    }
}
