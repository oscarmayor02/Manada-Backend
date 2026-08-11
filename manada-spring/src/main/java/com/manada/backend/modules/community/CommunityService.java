package com.manada.backend.modules.community;

import com.manada.backend.common.exception.ApiException;
import com.manada.backend.modules.community.dto.CommentRequest;
import com.manada.backend.modules.community.dto.PostRequest;
import com.manada.backend.modules.community.dto.PostResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class CommunityService {

    private final CommunityPostRepository postRepository;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;

    public CommunityService(CommunityPostRepository postRepository, CommentRepository commentRepository, LikeRepository likeRepository) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.likeRepository = likeRepository;
    }

    public List<PostResponse> list() {
        return postRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(0, 50)).stream()
            .map(p -> PostResponse.from(p, likeRepository.countByPostId(p.getId()), commentRepository.countByPostId(p.getId())))
            .toList();
    }

    @Transactional
    public CommunityPost create(UUID authorId, PostRequest req) {
        CommunityPost post = new CommunityPost();
        post.setAuthorId(authorId);
        post.setType(req.type());
        post.setCaption(req.caption());
        post.setPhotoUrl(req.photoUrl());
        return postRepository.save(post);
    }

    @Transactional
    public boolean toggleLike(UUID postId, UUID userId) {
        var existing = likeRepository.findByPostIdAndUserId(postId, userId);
        if (existing.isPresent()) {
            likeRepository.delete(existing.get());
            return false;
        }
        Like like = new Like();
        like.setPostId(postId);
        like.setUserId(userId);
        likeRepository.save(like);
        return true;
    }

    @Transactional
    public Comment addComment(UUID postId, UUID authorId, CommentRequest req) {
        if (!postRepository.existsById(postId)) throw ApiException.notFound("Publicación no encontrada.");
        Comment comment = new Comment();
        comment.setPostId(postId);
        comment.setAuthorId(authorId);
        comment.setText(req.text());
        return commentRepository.save(comment);
    }
}
