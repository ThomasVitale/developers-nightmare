package com.thomasvitale.demo.vote;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class VoteRepositoryTests {

    @Autowired
    VoteRepository voteRepository;

    @Test
    void saveNewVote() {
        Vote saved = voteRepository.save(Vote.of("1,2"));
        assertThat(saved.id()).isNotNull();
    }

}
