package com.audition.web;

import com.audition.common.logging.AuditionLogger;
import com.audition.model.AuditionPost;
import com.audition.model.Comments;
import com.audition.service.AuditionService;
import java.util.List;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class AuditionController {

    final AuditionService auditionService;
    final AuditionLogger auditionLogger;
    static final Logger LOG = LoggerFactory.getLogger(AuditionController.class);

    public AuditionController(final AuditionService auditionService, final AuditionLogger auditionLogger) {
        this.auditionService = auditionService;
        this.auditionLogger = auditionLogger;
    }

    @GetMapping("/posts")
    public ResponseEntity<List<AuditionPost>> getPosts(
        @RequestParam(value = "userId", required = false) final String userIdString) {
        Integer userId = null;
        if (Strings.isNotEmpty(userIdString)) {
            userId = validateId(userIdString, "userId");
        }
        return ResponseEntity.ok(auditionService.getPosts(userId));
    }

    @GetMapping("/posts/{id}")
    public ResponseEntity<AuditionPost> getPostsByPostId(@PathVariable("id") final String postIdInputString) {
        final Integer postId = validateId(postIdInputString, "id");
        return ResponseEntity.ok(auditionService.getPostById(postId));
    }

    @GetMapping("/comments")
    public ResponseEntity<List<Comments>> getCommentsByPostId(
        @RequestParam(value = "postId", required = false) final String postIdInputString) {
        final Integer postId = validateId(postIdInputString, "postId");
        return ResponseEntity.ok(auditionService.getCommentsByPostId(postId));
    }

    private Integer validateId(final String id, final String attributeName) {
        try {
            return Integer.parseInt(id);
        } catch (NumberFormatException e) {
            auditionLogger.error(LOG, e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                String.format("Invalid %s format", attributeName));
        }
    }


}
