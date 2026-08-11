package com.manada.backend.modules.community;

import com.manada.backend.common.security.AuthenticatedUser;
import com.manada.backend.modules.community.dto.CommentRequest;
import com.manada.backend.modules.community.dto.PostRequest;
import com.manada.backend.modules.community.dto.PostResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/community")
public class CommunityController {

    private final CommunityService communityService;

    public CommunityController(CommunityService communityService) {
        this.communityService = communityService;
    }

    @GetMapping
    public List<PostResponse> list() {
        return communityService.list();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommunityPost create(@AuthenticationPrincipal AuthenticatedUser user, @Valid @RequestBody PostRequest req) {
        return communityService.create(user.id(), req);
    }

    @PostMapping("/{id}/like")
    public Map<String, Boolean> toggleLike(@AuthenticationPrincipal AuthenticatedUser user, @PathVariable UUID id) {
        return Map.of("liked", communityService.toggleLike(id, user.id()));
    }

    @PostMapping("/{id}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public Comment addComment(@AuthenticationPrincipal AuthenticatedUser user, @PathVariable UUID id, @Valid @RequestBody CommentRequest req) {
        return communityService.addComment(id, user.id(), req);
    }
}
