package com.englishlearning.controller;

import com.englishlearning.entity.Comment;
import com.englishlearning.entity.Post;
import com.englishlearning.entity.PostInteraction;
import com.englishlearning.entity.User;
import com.englishlearning.repository.CommentRepository;
import com.englishlearning.repository.PostInteractionRepository;
import com.englishlearning.repository.PostRepository;
import com.englishlearning.service.CustomUserDetailsService;
import com.englishlearning.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
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

    @Autowired
    private JwtUtil jwtUtil;

    // 从Authorization header中获取当前用户
    private User getCurrentUser(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Please login first");
        }
        String token = authorizationHeader.substring(7);
        try {
            String email = jwtUtil.getEmailFromToken(token);
            return userDetailsService.findUserByEmail(email);
        } catch (Exception e) {
            throw new RuntimeException("Please login first");
        }
    }

    @GetMapping("/posts")
    public List<Post> getPosts() {
        // 按置顶状态和创建时间排序，置顶的在前
        return postRepository.findAll(Sort.by(Sort.Order.desc("top"), Sort.Order.desc("createdAt")));
    }

    @GetMapping("/posts/{postId}")
    public Post getPostById(@PathVariable Long postId) {
        return postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));
    }

    @PostMapping("/posts")
    public Post createPost(@RequestHeader(value = "Authorization", required = false) String authorizationHeader,
                           @RequestBody Map<String, String> postData) {
        String title = postData.get("title");
        String content = postData.get("content");
        String category = postData.get("category");

        User author = getCurrentUser(authorizationHeader);

        Post post = new Post();
        post.setTitle(title);
        post.setContent(content);
        post.setCategory(category);
        post.setAuthor(author);
        post.setCreatedAt(LocalDateTime.now());
        post.setTop(false);

        return postRepository.save(post);
    }

    @DeleteMapping("/posts/{postId}")
    public Map<String, String> deletePost(@RequestHeader(value = "Authorization", required = false) String authorizationHeader,
                                           @PathVariable Long postId) {
        User currentUser = getCurrentUser(authorizationHeader);

        Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));

        // 检查权限：只有帖子作者、管理员或教师可以删除
        if (!post.getAuthor().getId().equals(currentUser.getId()) && !"admin".equals(currentUser.getRole()) && !"teacher".equals(currentUser.getRole())) {
            throw new RuntimeException("No permission to delete this post");
        }

        // 删除相关的评论和互动记录
        commentRepository.deleteByPostId(postId);
        postInteractionRepository.deleteByPostId(postId);
        postRepository.delete(post);

        Map<String, String> response = new java.util.HashMap<>();
        response.put("message", "Post deleted successfully");
        return response;
    }

    @PutMapping("/posts/{postId}/top")
    public Map<String, String> toggleTopPost(@RequestHeader(value = "Authorization", required = false) String authorizationHeader,
                                              @PathVariable Long postId, @RequestBody Map<String, Boolean> data) {
        User currentUser = getCurrentUser(authorizationHeader);

        // 检查是否为管理员或教师
        if (!"admin".equals(currentUser.getRole()) && !"teacher".equals(currentUser.getRole())) {
            throw new RuntimeException("No permission to top this post");
        }

        Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));
        post.setTop(data.get("top"));
        postRepository.save(post);

        Map<String, String> response = new java.util.HashMap<>();
        response.put("message", "Post top status updated successfully");
        return response;
    }

    @GetMapping("/posts/{postId}/comments")
    public List<Comment> getComments(@PathVariable Long postId) {
        return commentRepository.findByPostId(postId);
    }

    @PostMapping("/posts/{postId}/comments")
    public Comment createComment(@RequestHeader(value = "Authorization", required = false) String authorizationHeader,
                                 @PathVariable Long postId, @RequestBody Map<String, Object> commentData) {
        String content = commentData.get("content").toString();
        String parentId = commentData.get("parentId") != null ? commentData.get("parentId").toString() : null;

        User author = getCurrentUser(authorizationHeader);

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
    public Map<String, String> likePost(@RequestHeader(value = "Authorization", required = false) String authorizationHeader,
                                         @PathVariable Long postId) {
        User user = getCurrentUser(authorizationHeader);

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
    public Map<String, String> collectPost(@RequestHeader(value = "Authorization", required = false) String authorizationHeader,
                                            @PathVariable Long postId) {
        User user = getCurrentUser(authorizationHeader);

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
    public boolean checkInteraction(@RequestHeader(value = "Authorization", required = false) String authorizationHeader,
                                    @PathVariable Long postId, @RequestParam String type) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return false;
        }
        try {
            String token = authorizationHeader.substring(7);
            String email = jwtUtil.getEmailFromToken(token);
            User user = userDetailsService.findUserByEmail(email);
            return postInteractionRepository.findByUserIdAndPostIdAndType(user.getId(), postId, type).isPresent();
        } catch (Exception e) {
            return false;
        }
    }
}