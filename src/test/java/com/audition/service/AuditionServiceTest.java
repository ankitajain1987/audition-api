package com.audition.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.audition.integration.AuditionIntegrationClient;
import com.audition.model.AuditionPost;
import com.audition.model.Comments;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AuditionServiceTest {

    @Mock
    private AuditionIntegrationClient auditionIntegrationClient;

    @InjectMocks
    private AuditionService auditionService;

    @BeforeEach
    void setUp() {
    }

    @Test
    void testGetPosts() {
        // Given
        Integer userId = 1;
        List<AuditionPost> mockPosts = Arrays.asList(
            createMockAuditionPosts(1), createMockAuditionPosts(2));
        when(auditionIntegrationClient.getPosts(userId)).thenReturn(mockPosts);

        // When
        List<AuditionPost> result = auditionService.getPosts(userId);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Title 1", result.get(0).getTitle());
        assertEquals("Title 2", result.get(1).getTitle());
        verify(auditionIntegrationClient, times(1)).getPosts(userId);
    }

    @Test
    void testGetPostById() {
        // Given
        Integer postId = 1;
        AuditionPost mockPost = createMockAuditionPosts(postId);
        when(auditionIntegrationClient.getPostById(postId)).thenReturn(mockPost);

        // When
        AuditionPost result = auditionService.getPostById(postId);

        // Then
        assertNotNull(result);
        assertEquals(postId, result.getId());
        assertEquals("Title 1", result.getTitle());
        verify(auditionIntegrationClient, times(1)).getPostById(postId);
    }

    @Test
    void testGetCommentsByPostId() {
        // Given
        Integer postId = 1;
        List<Comments> mockComments = Arrays.asList(
            Comments.builder().id(1).postId(postId).body("Comment 1").build(),
            Comments.builder().id(2).postId(postId).body("Comment 2").build()
        );
        when(auditionIntegrationClient.getCommentsForPostId(postId)).thenReturn(mockComments);

        // When
        List<Comments> result = auditionService.getCommentsByPostId(postId);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Comment 1", result.get(0).getBody());
        assertEquals("Comment 2", result.get(1).getBody());
        verify(auditionIntegrationClient, times(1)).getCommentsForPostId(postId);
    }

    private AuditionPost createMockAuditionPosts(int id) {
        AuditionPost mockPost = new AuditionPost();
        mockPost.setId(1);
        mockPost.setTitle(String.format("Title %s", id));
        mockPost.setBody(String.format("Body %s", id));
        return mockPost;
    }
}

