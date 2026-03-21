package com.thomasvitale.demo.vote;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class VoteController {

    static final Map<Integer, String> ANSWER_OPTIONS;

    static {
        var options = new LinkedHashMap<Integer, String>();
        options.put(1, "License approval delays");
        options.put(2, "False positive CVEs blocking releases");
        options.put(3, "Slow manual approval processes");
        options.put(4, "Lack of automation in compliance checks");
        options.put(5, "Dependency management chaos");
        options.put(6, "Supply chain security doubts");
        options.put(7, "Platform/operation team bottlenecks");
        options.put(8, "Other");
        ANSWER_OPTIONS = options;
    }

    private final VoteRepository voteRepository;

    public VoteController(VoteRepository voteRepository) {
        this.voteRepository = voteRepository;
    }

    @GetMapping("/")
    public String survey(@RequestParam(required = false) String voted, Model model) {
        model.addAttribute("options", ANSWER_OPTIONS);
        model.addAttribute("voted", voted != null);
        return "survey";
    }

    @PostMapping("/vote")
    public String vote(@RequestParam(required = false) List<Integer> answers) {
        if (answers == null || answers.isEmpty()) {
            return "redirect:/";
        }
        String answersStr = answers.stream()
                .filter(a -> a >= 1 && a <= ANSWER_OPTIONS.size())
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        if (!answersStr.isEmpty()) {
            voteRepository.save(new Vote(answersStr));
        }
        return "redirect:/?voted=true";
    }

    @GetMapping("/results")
    public String results(Model model) {
        model.addAttribute("options", ANSWER_OPTIONS);
        return "results";
    }

    @GetMapping("/api/results")
    @ResponseBody
    public Map<String, Object> getResults() {
        Map<Integer, Long> counts = new HashMap<>();
        for (var ac : voteRepository.countByAnswer()) {
            counts.put(ac.answerNumber(), ac.voteCount());
        }

        long totalVotes = voteRepository.count();

        List<Map<String, Object>> results = new ArrayList<>();
        for (var entry : ANSWER_OPTIONS.entrySet()) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", entry.getKey());
            item.put("label", entry.getValue());
            item.put("count", counts.getOrDefault(entry.getKey(), 0L));
            results.add(item);
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("results", results);
        response.put("totalVotes", totalVotes);
        return response;
    }

    @DeleteMapping("/api/votes")
    @ResponseBody
    public ResponseEntity<Void> clearVotes() {
        voteRepository.deleteAll();
        return ResponseEntity.noContent().build();
    }

}
