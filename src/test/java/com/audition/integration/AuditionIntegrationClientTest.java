package com.audition.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.isNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.audition.common.exception.SystemException;
import com.audition.common.logging.AuditionLogger;
import com.audition.model.AuditionPost;
import com.audition.model.Comments;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

class AuditionIntegrationClientTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private AuditionLogger auditionLogger;

    @InjectMocks
    private AuditionIntegrationClient auditionIntegrationClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetPostsSuccess() {
        // Given
        Integer userId = 1;

        List<AuditionPost> mockPosts = Arrays.asList(createMockAuditionPosts(1), createMockAuditionPosts(2));

        when(restTemplate.exchange(any(String.class), eq(HttpMethod.GET), isNull(),
            any(ParameterizedTypeReference.class)))
            .thenReturn(new ResponseEntity<>(mockPosts, HttpStatus.OK));

        // When
        List<AuditionPost> result = auditionIntegrationClient.getPosts(userId);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Title 1", result.get(0).getTitle());
        assertEquals("Title 2", result.get(1).getTitle());
        verify(restTemplate, times(1)).exchange(any(String.class), eq(HttpMethod.GET), isNull(),
            any(ParameterizedTypeReference.class));
    }

    @Test
    void testGetPostByIdNotFound() {
        // Given
        Integer postId = 1;
        when(restTemplate.exchange(any(String.class), eq(HttpMethod.GET), isNull(),
            any(ParameterizedTypeReference.class)))
            .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        // When & Then
        SystemException exception = assertThrows(SystemException.class, () ->
            auditionIntegrationClient.getPostById(postId));
        assertEquals(HttpStatus.NOT_FOUND.value(), exception.getStatusCode());
        verify(auditionLogger, times(2)).logHttpStatusCodeError(any(), anyString(), eq(HttpStatus.NOT_FOUND.value()));
    }

    @Test
    void testGetPostByIdSuccess() {
        // Given
        Integer postId = 1;
        AuditionPost mockPost = createMockAuditionPosts(postId);
        when(restTemplate.exchange(any(String.class), eq(HttpMethod.GET), isNull(),
            any(ParameterizedTypeReference.class)))
            .thenReturn(new ResponseEntity<>(mockPost, HttpStatus.OK));
        // When
        AuditionPost result = auditionIntegrationClient.getPostById(postId);

        // Then
        assertNotNull(result);
        assertEquals(postId, result.getId());
        assertEquals("Title 1", result.getTitle());
        verify(restTemplate, times(2)).exchange(any(String.class), eq(HttpMethod.GET), isNull(),
            any(ParameterizedTypeReference.class));
    }

    @Test
    void testGetCommentsSuccess() {
        // Given
        Integer postId = 1;
        List<Comments> mockComments = List.of(Comments.builder().id(1).postId(postId).body("Comment 1").build());
        when(restTemplate.exchange(any(String.class), eq(HttpMethod.GET), isNull(),
            any(ParameterizedTypeReference.class)))
            .thenReturn(new ResponseEntity<>(mockComments, HttpStatus.OK));
        // When
        List<Comments> result = auditionIntegrationClient.getCommentsForPost(postId);

        // Then
        assertNotNull(result);
        assertEquals(postId, result.get(0).getPostId());
        assertEquals("Comment 1", result.get(0).getBody());
        verify(restTemplate, times(1)).exchange(any(String.class), eq(HttpMethod.GET), isNull(),
            any(ParameterizedTypeReference.class));
    }

    @Test
    void testGetCommentsForPostIdSuccess() {
        // Given
        Integer postId = 1;
        List<Comments> mockComments = List.of(Comments.builder().id(1).postId(postId).body("Comment 1").build());
        when(restTemplate.exchange(any(String.class), eq(HttpMethod.GET), isNull(),
            any(ParameterizedTypeReference.class)))
            .thenReturn(new ResponseEntity<>(mockComments, HttpStatus.OK));

        // When
        List<Comments> result = auditionIntegrationClient.getCommentsForPostId(postId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Comment 1", result.get(0).getBody());
        verify(restTemplate, times(1)).exchange(any(String.class), eq(HttpMethod.GET), isNull(),
            any(ParameterizedTypeReference.class));
    }

    @Test
    void testGetCommentsForPostIdNotFound() {
        // Given
        Integer postId = 1;
        when(restTemplate.exchange(any(String.class), eq(HttpMethod.GET), isNull(),
            any(ParameterizedTypeReference.class)))
            .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        // When & Then
        SystemException thrownException = assertThrows(SystemException.class, () ->
            auditionIntegrationClient.getCommentsForPostId(postId)
        );

        // Then
        assertEquals(HttpStatus.NOT_FOUND.value(), thrownException.getStatusCode());
        assertEquals("Cannot find a comments for Post with id 1 ", thrownException.getMessage());

        // Then
        verify(auditionLogger, times(1)).logHttpStatusCodeError(any(), anyString(), eq(HttpStatus.NOT_FOUND.value()));
    }

    private AuditionPost createMockAuditionPosts(int id) {
        AuditionPost mockPost = new AuditionPost();
        mockPost.setId(1);
        mockPost.setUserId(1);
        mockPost.setTitle(String.format("Title %s", id));
        mockPost.setBody(String.format("Body %s", id));
        return mockPost;
    }
}

