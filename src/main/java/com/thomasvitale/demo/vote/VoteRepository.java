package com.thomasvitale.demo.vote;

import java.util.UUID;

import org.springframework.data.repository.ListCrudRepository;

public interface VoteRepository extends ListCrudRepository<Vote, UUID> {

}
