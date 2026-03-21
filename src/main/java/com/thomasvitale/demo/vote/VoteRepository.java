package com.thomasvitale.demo.vote;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;

public interface VoteRepository extends ListCrudRepository<Vote, UUID> {

    @Query("""
        SELECT answer_number::int, COUNT(*) AS vote_count
        FROM vote, unnest(string_to_array(answers, ',')) AS answer_number
        GROUP BY answer_number
        ORDER BY answer_number
    """)
    List<AnswerCount> countByAnswer();

    record AnswerCount(int answerNumber, long voteCount) {
    }

}
