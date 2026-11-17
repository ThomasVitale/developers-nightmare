package com.thomasvitale.demo.vote;

import java.util.UUID;

import org.springframework.data.repository.ListCrudRepository;

interface VoteRepository extends ListCrudRepository<Vote, UUID> {}
