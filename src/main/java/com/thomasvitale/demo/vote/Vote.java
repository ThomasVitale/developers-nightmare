package com.thomasvitale.demo.vote;

import java.time.Instant;
import java.util.UUID;

import org.jspecify.annotations.Nullable;
import org.springframework.data.annotation.Id;

record Vote(
    @Id @Nullable UUID id,
    String answers,
    Instant createdAt
) {
    public Vote(String answers) {
        this(null, answers, Instant.now());
    }
}
