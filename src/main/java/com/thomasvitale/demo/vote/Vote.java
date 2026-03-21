package com.thomasvitale.demo.vote;

import java.time.Instant;
import java.util.UUID;

import org.springframework.data.annotation.Id;

public record Vote(
    @Id UUID id,
    String answers,
    Instant createdAt
) {
    public Vote(String answers) {
        this(null, answers, Instant.now());
    }
}
