package com.englishlearning.controller;

import com.englishlearning.entity.Comment;
import com.englishlearning.entity.Post;
import com.englishlearning.entity.PostInteraction;
import com.englishlearning.entity.User;
import com.englishlearning.repository.CommentRepository;
import com.englishlearning.repository.PostInteractionRepository;
import com.englishlearning.repository.PostRepository;
import com.englishlearning.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/community")
public class CommunityController {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostInteractionRepository postInteractionRepository;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @GetMapping("/posts")
    public List<Post> getPosts(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int pageSize) {
        // 这里可以实现分页查询
        return postRepository.findAll();
    }

    @GetMapping("/posts/{postId}")
    public Post getPostById(@PathVariable Long postId) {
        return postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));
    }

    @PostMapping("/posts")
    public Post createPost(@RequestBody Map<String, String> postData) {
        String title = postData.get("title");
        String content = postData.get("content");
        String category = postData.get("category");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User author = userDetailsService.findUserByEmail(email);

        Post post = new Post();
        post.setTitle(title);
        post.setContent(content);
        post.setCategory(category);
        post.setAuthor(author);
        post.setCreatedAt(LocalDateTime.now());

        return postRepository.save(post);
    }

    @GetMapping("/posts/{postId}/comments")
    public List<Comment> getComments(@PathVariable Long postId) {
        return commentRepository.findByPostId(postId);
    }

    @PostMapping("/posts/{postId}/comments")
    public Comment createComment(@PathVariable Long postId, @RequestBody Map<String, Object> commentData) {
        String content = commentData.get("content").toString();
        String parentId = commentData.get("parentId") != null ? commentData.get("parentId").toString() : null;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User author = userDetailsService.findUserByEmail(email);

        Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));

        Comment comment = new Comment();
        comment.setPost(post);
        comment.setAuthor(author);
        comment.setContent(content);
        comment.setCreatedAt(LocalDateTime.now());

        if (parentId != null) {
            Comment parentComment = commentRepository.findById(Long.valueOf(parentId)).orElseThrow(() -> new RuntimeException("Parent comment not found"));
            comment.setParent(parentComment);
        }

        Comment savedComment = commentRepository.save(comment);
        post.setCommentCount(post.getCommentCount() + 1);
        postRepository.save(post);

        return savedComment;
    }

    @PostMapping("/posts/{postId}/like")
    public Map<String, String> likePost(@PathVariable Long postId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userDetailsService.findUserByEmail(email);

        Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));

        // 检查是否已经点赞
        if (postInteractionRepository.findByUserIdAndPostIdAndType(user.getId(), postId, "like").isEmpty()) {
            PostInteraction interaction = new PostInteraction();
            interaction.setUser(user);
            interaction.setPost(post);
            interaction.setType("like");
            interaction.setCreatedAt(LocalDateTime.now());
            postInteractionRepository.save(interaction);

            post.setLikeCount(post.getLikeCount() + 1);
            postRepository.save(post);
        }

        Map<String, String> response = new java.util.HashMap<>();
        response.put("message", "Post liked successfully");
        return response;
    }

    @PostMapping("/posts/{postId}/collect")
    public Map<String, String> collectPost(@PathVariable Long postId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userDetailsService.findUserByEmail(email);

        Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));

        // 检查是否已经收藏
        if (postInteractionRepository.findByUserIdAndPostIdAndType(user.getId(), postId, "collect").isEmpty()) {
            PostInteraction interaction = new PostInteraction();
            interaction.setUser(user);
            interaction.setPost(post);
            interaction.setType("collect");
            interaction.setCreatedAt(LocalDateTime.now());
            postInteractionRepository.save(interaction);

            post.setCollectCount(post.getCollectCount() + 1);
            postRepository.save(post);
        }

        Map<String, String> response = new java.util.HashMap<>();
        response.put("message", "Post collected successfully");
        return response;
    }

    @GetMapping("/posts/{postId}/interactions/check")
    public boolean checkInteraction(@PathVariable Long postId, @RequestParam String type) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userDetailsService.findUserByEmail(email);

        return postInteractionRepository.findByUserIdAndPostIdAndType(user.getId(), postId, type).isPresent();
    }
}