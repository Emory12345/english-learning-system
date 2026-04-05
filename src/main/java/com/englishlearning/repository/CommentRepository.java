package com.englishlearning.repository;

import com.englishlearning.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPostId(Long postId);
    List<Comment> findByParentId(Long parentId);

    @Transactional
    void deleteByPostId(Long postId);
}