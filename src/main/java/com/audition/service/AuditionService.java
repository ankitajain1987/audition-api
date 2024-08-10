package com.audition.service;

import com.audition.integration.AuditionIntegrationClient;
import com.audition.model.AuditionPost;
import com.audition.model.Comments;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class AuditionService {

    private final AuditionIntegrationClient auditionIntegrationClient;

    public AuditionService(final AuditionIntegrationClient auditionIntegrationClient) {
        this.auditionIntegrationClient = auditionIntegrationClient;
    }


    public List<AuditionPost> getPosts(final Integer userId) {
        return auditionIntegrationClient.getPosts(userId);
    }

    public AuditionPost getPostById(final Integer postId) {
        return auditionIntegrationClient.getPostById(postId);
    }

    public List<Comments> getCommentsByPostId(final Integer postId) {
        return auditionIntegrationClient.getCommentsForPostId(postId);
    }

}
