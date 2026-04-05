package com.englishlearning.controller;

import com.englishlearning.entity.Word;
import com.englishlearning.repository.WordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/words")
public class WordsController {

    @Autowired
    private WordRepository wordRepository;

    @GetMapping("/level/{level}")
    public List<Word> getWordsByLevel(@PathVariable String level) {
        return wordRepository.findByLevel(level);
    }

    @GetMapping("/age-group/{ageGroup}")
    public List<Word> getWordsByAgeGroup(@PathVariable Integer ageGroup) {
        return wordRepository.findByAgeGroup(ageGroup);
    }

    @GetMapping("/age-group/{ageGroup}/page")
    public Map<String, Object> getWordsByAgeGroupWithPagination(
            @PathVariable String ageGroup,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Word> wordPage;
        
        // 检查是否是多个年龄段（逗号分隔）
        if (ageGroup.contains(",")) {
            // 解析多个年龄段
            List<Integer> ageGroups = java.util.Arrays.stream(ageGroup.split(","))
                    .map(Integer::parseInt)
                    .collect(java.util.stream.Collectors.toList());
            wordPage = wordRepository.findByAgeGroupIn(ageGroups, pageable);
        } else {
            // 单个年龄段
            int age = Integer.parseInt(ageGroup);
            wordPage = wordRepository.findByAgeGroup(age, pageable);
        }
        
        return Map.of(
                "content", wordPage.getContent(),
                "totalElements", wordPage.getTotalElements(),
                "totalPages", wordPage.getTotalPages(),
                "currentPage", wordPage.getNumber(),
                "pageSize", wordPage.getSize()
        );
    }
}
