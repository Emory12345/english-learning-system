package com.englishlearning.controller;

import com.englishlearning.entity.Word;
import com.englishlearning.repository.WordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/words")
public class WordsController {

    @Autowired
    private WordRepository wordRepository;

    @GetMapping("/level/{level}")
    public List<Word> getWordsByLevel(@PathVariable String level) {
        return wordRepository.findByLevel(level);
    }
}