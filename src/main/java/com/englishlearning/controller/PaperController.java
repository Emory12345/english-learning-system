package com.englishlearning.controller;

import com.englishlearning.entity.Paper;
import com.englishlearning.entity.Question;
import com.englishlearning.repository.PaperRepository;
import com.englishlearning.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/papers")
public class PaperController {

    @Autowired
    private PaperRepository paperRepository;

    @Autowired
    private QuestionRepository questionRepository;

    // 获取所有试卷
    @GetMapping
    public List<Paper> getPapers() {
        return paperRepository.findAll();
    }

    // 根据ID获取试卷
    @GetMapping("/{id}")
    public Paper getPaperById(@PathVariable Long id) {
        return paperRepository.findById(id).orElse(null);
    }

    // 获取试卷的所有题目
    @GetMapping("/{id}/questions")
    public List<Question> getQuestionsByPaperId(@PathVariable Long id) {
        // 这里需要根据实际的数据库关系来实现
        // 假设Question表中有一个paper_id字段关联到Paper表
        return questionRepository.findAll();
    }
}
