package com.thomasvitale.demo.vote;

import java.util.List;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;

@Service
public class VoteService {

    private final JdbcClient jdbcClient;

    public VoteService(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public List<AnswerCount> countByAnswer() {
        return jdbcClient.sql("""
                SELECT answer_number::int AS answer_number, COUNT(*) AS vote_count
                FROM vote, unnest(string_to_array(answers, ',')) AS answer_number
                GROUP BY answer_number
                ORDER BY answer_number
                """)
                .query(AnswerCount.class)
                .list();
    }

}
