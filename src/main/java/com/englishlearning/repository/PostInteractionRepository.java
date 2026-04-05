package com.englishlearning.repository;

import com.englishlearning.entity.PostInteraction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface PostInteractionRepository extends JpaRepository<PostInteraction, Long> {
    Optional<PostInteraction> findByUserIdAndPostIdAndType(Long userId, Long postId, String type);

    @Transactional
    void deleteByPostId(Long postId);
}