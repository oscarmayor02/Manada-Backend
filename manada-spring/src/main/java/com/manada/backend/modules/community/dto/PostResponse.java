package com.manada.backend.modules.community.dto;

import com.manada.backend.modules.community.CommunityPost;
import com.manada.backend.modules.community.PostType;

import java.time.Instant;
import java.util.UUID;

public record PostResponse(
    UUID id, UUID authorId, PostType type, String caption, String photoUrl,
    long likeCount, long commentCount, Instant createdAt
) {
    public static PostResponse from(CommunityPost p, long likeCount, long commentCount) {
        return new PostResponse(p.getId(), p.getAuthorId(), p.getType(), p.getCaption(), p.getPhotoUrl(),
            likeCount, commentCount, p.getCreatedAt());
    }
}
