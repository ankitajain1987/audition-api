package com.audition.integration;

import com.audition.common.exception.SystemException;
import com.audition.common.logging.AuditionLogger;
import com.audition.model.AuditionPost;
import com.audition.model.Comments;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Component
public class AuditionIntegrationClient {

    final Logger logger = LoggerFactory.getLogger(AuditionIntegrationClient.class);
    AuditionLogger auditionLogger;

    public static final String AN_UNEXPECTED_ERROR_OCCURRED_S = "An unexpected error occurred: %s";
    public static final String URL = "https://jsonplaceholder.typicode.com/posts/";
    public static final String COMMENTS_URL = "https://jsonplaceholder.typicode.com/comments?postId=";
    private final RestTemplate restTemplate;

    public AuditionIntegrationClient(final RestTemplate restTemplate, final AuditionLogger auditionLogger) {
        this.restTemplate = restTemplate;
        this.auditionLogger = auditionLogger;
    }

    public List<AuditionPost> getPosts(final Integer userId) {
        // Fetch posts from the external API
        List<AuditionPost> posts = restTemplate.exchange(
            URL,
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<AuditionPost>>() {
            }
        ).getBody();

        if (userId != null) {
            // Filter posts based on the userId filter
            assert posts != null;
            if (!posts.isEmpty()) {
                posts = posts.stream().filter(post -> userId == post.getUserId()).toList();
            }
        }

        return posts;
    }

    public AuditionPost getPostById(final Integer id) {
        try {
            // Fetch posts from the external API

            final AuditionPost posts = restTemplate.exchange(
                URL + id,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<AuditionPost>() {
                }
            ).getBody();
            assert posts != null;
            posts.setComments(getCommentsForPost(id));

            return posts;
        } catch (HttpClientErrorException e) {
            // Throw specific exceptions based on HTTP status codes
            auditionLogger.logHttpStatusCodeError(logger,
                String.format("Error occurred while fetching the Post with id  %s", id), e.getStatusCode().value());
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                auditionLogger.logHttpStatusCodeError(logger,
                    String.format("Cannot find a Post with id %s", id), e.getStatusCode().value());
                throw new SystemException(String.format("Cannot find a Post with id %s ", id), "Resource Not Found",
                    HttpStatus.NOT_FOUND.value());
            } else {
                auditionLogger.logHttpStatusCodeError(logger,
                    String.format("unexpected error occurred while fetching post with post id : %s", id),
                    e.getStatusCode().value());
                throw new SystemException(
                    String.format("An unexpected error occurred while fetching post with post id : %s", id),
                    e.getStatusCode().value());
            }
        } catch (Exception e) {
            // Log unexpected exceptions
            auditionLogger.error(logger, String.format(AN_UNEXPECTED_ERROR_OCCURRED_S, e.getMessage()));
            // Rethrow as a general exception
            throw new SystemException(String.format("Error occurred while fetching the Post with id %s", id),
                HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    public List<Comments> getCommentsForPost(final Integer postId) {
        try {
            // Fetch posts from the external API
            return restTemplate.exchange(
                String.format(URL + "%s%s", postId, "/comments"),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Comments>>() {
                }
            ).getBody();
        } catch (HttpClientErrorException e) {
            // Throw specific exceptions based on HTTP status codes
            auditionLogger.logHttpStatusCodeError(logger,
                String.format("Error occurred while fetching the comments for post with postId %s", postId),
                e.getStatusCode().value());
            return Collections.emptyList();
        } catch (Exception e) {
            // Log unexpected exceptions
            auditionLogger.error(logger, String.format(AN_UNEXPECTED_ERROR_OCCURRED_S, e.getMessage()));
            return Collections.emptyList();
        }
    }

    public List<Comments> getCommentsForPostId(final Integer postId) {
        try {
            // Fetch posts from the external API
            return restTemplate.exchange(
                String.format(COMMENTS_URL + "%s", postId),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Comments>>() {
                }
            ).getBody();
        } catch (HttpClientErrorException e) {
            auditionLogger.logHttpStatusCodeError(logger,
                String.format("Error occurred while fetching the Post with id  %s", postId), e.getStatusCode().value());
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new SystemException(String.format("Cannot find a comments for Post with id %s ", postId),
                    "Resource Not Found",
                    HttpStatus.NOT_FOUND.value());
            } else {
                auditionLogger.error(logger, String.format(AN_UNEXPECTED_ERROR_OCCURRED_S, e.getMessage()));
                throw new SystemException(
                    String.format("An unexpected error occurred while fetching post with post id : %s", postId),
                    e.getStatusCode().value());
            }
        } catch (Exception e) {
            auditionLogger.error(logger, String.format(AN_UNEXPECTED_ERROR_OCCURRED_S, e.getMessage()));
            throw new SystemException(String.format("Error occurred while fetching the Comments for postId %s", postId),
                HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
}
