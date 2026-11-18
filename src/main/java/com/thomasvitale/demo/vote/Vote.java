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
    static Vote of(String answers) {
        return new Vote(null, answers, Instant.now());
    }
}
