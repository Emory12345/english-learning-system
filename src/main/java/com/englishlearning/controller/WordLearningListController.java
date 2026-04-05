package com.englishlearning.controller;

import com.englishlearning.entity.User;
import com.englishlearning.entity.Word;
import com.englishlearning.entity.WordLearningList;
import com.englishlearning.repository.UserRepository;
import com.englishlearning.repository.WordLearningListRepository;
import com.englishlearning.repository.WordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/word-learning-list")
public class WordLearningListController {

    @Autowired
    private WordLearningListRepository wordLearningListRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WordRepository wordRepository;

    @PostMapping("/add")
    public String addWordToLearningList(@RequestParam Long userId, @RequestParam Long wordId) {
        try {
            // 检查用户是否存在
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // 检查单词是否存在
            Word word = wordRepository.findById(wordId)
                    .orElseThrow(() -> new RuntimeException("Word not found"));

            // 检查是否已经在学习列表中
            if (wordLearningListRepository.findByUserIdAndWordId(userId, wordId).isPresent()) {
                return "单词已在学习列表中";
            }

            // 创建新的学习列表项
            WordLearningList learningList = new WordLearningList();
            learningList.setUser(user);
            learningList.setWord(word);
            learningList.setIsLearned(false);

            // 保存到数据库
            wordLearningListRepository.save(learningList);

            return "添加成功";
        } catch (Exception e) {
            return "添加失败: " + e.getMessage();
        }
    }

    @GetMapping("/user/{userId}")
    public List<Map<String, Object>> getLearningListByUserId(@PathVariable Long userId) {
        List<WordLearningList> learningLists = wordLearningListRepository.findByUserId(userId);
        List<Map<String, Object>> result = new ArrayList<>();

        for (WordLearningList item : learningLists) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", item.getId());
            map.put("word", item.getWord());
            map.put("isLearned", item.getIsLearned() != null ? item.getIsLearned() : false);
            result.add(map);
        }

        return result;
    }

    @Transactional
    @PostMapping("/remove")
    public String removeFromLearningList(@RequestParam Long userId, @RequestParam Long wordId) {
        try {
            wordLearningListRepository.deleteByUserIdAndWordId(userId, wordId);
            return "移除成功";
        } catch (Exception e) {
            return "移除失败: " + e.getMessage();
        }
    }
}
