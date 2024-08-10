package com.audition.web;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.audition.model.AuditionPost;
import com.audition.model.Comments;
import com.audition.service.AuditionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class AuditionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuditionService auditionService;

    @BeforeEach
    public void setup() {
        // Optionally set up initial state here if needed
    }

    @Test
    void testGetPostsWithValidUserId() throws Exception {
        // Given
        int userId = 1;

        List<AuditionPost> mockPosts = Arrays.asList(createAuditionPostTestData(1, userId),
            createAuditionPostTestData(2, 1));

        when(auditionService.getPosts(userId)).thenReturn(mockPosts);

        // When & Then
        mockMvc.perform(get("/posts")
                .param("userId", String.valueOf(userId))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].userId").value("1"))
            .andExpect(jsonPath("$[0].title").value("Title 1"))
            .andExpect(jsonPath("$[1].title").value("Title 2"));
    }

    @Test
    void testGetPostsIfUserIdFilterIsNotGiven() throws Exception {
        List<AuditionPost> mockPosts = Arrays.asList(createAuditionPostTestData(1, 1),
            createAuditionPostTestData(2, 2));

        when(auditionService.getPosts(null)).thenReturn(mockPosts);

        // When & Then
        mockMvc.perform(get("/posts")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].userId").value("1"))
            .andExpect(jsonPath("$[1].userId").value("2"))
            .andExpect(jsonPath("$[0].title").value("Title 1"))
            .andExpect(jsonPath("$[1].title").value("Title 2"));

    }

    @Test
    void testGetPostsById() throws Exception {

        AuditionPost mockAuditionPost = createAuditionPostTestData(1, 1);
        when(auditionService.getPostById(1)).thenReturn(mockAuditionPost);

        // When & Then
        mockMvc.perform(get("/posts/1")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(5))
            .andExpect(jsonPath("$.userId").value("1"))
            .andExpect(jsonPath("$.title").value("Title 1"))
            .andExpect(jsonPath("$.comments.length()").value(2));

    }

    @Test
    void testCommentsForPostId() throws Exception {
        int postId = 1;
        when(auditionService.getCommentsByPostId(postId)).thenReturn(createMockCommentsData(postId));

        // When & Then
        mockMvc.perform(get("/comments")
                .param("postId", String.valueOf(postId))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].name").value("SomeName"))
            .andExpect(jsonPath("$[1].name").value("SomeOtherName"))
            .andExpect(jsonPath("$[0].email").value("someemail@something.com"))
            .andExpect(jsonPath("$[1].email").value("someOtheremail@something.com"));
    }

    @Test
    void testValidateRequestParameterShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/posts")
            .param("userId", "invalidUserId")
            .accept(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
    }

    private AuditionPost createAuditionPostTestData(int id, int userId) {
        AuditionPost auditionPost = new AuditionPost();
        auditionPost.setId(id);
        auditionPost.setUserId(userId);
        auditionPost.setTitle("Title " + id);
        auditionPost.setBody("Body " + id);
        auditionPost.setComments(createMockCommentsData(id));
        return auditionPost;
    }

    private List<Comments> createMockCommentsData(int postId) {
        return Arrays.asList(Comments.builder().postId(postId)
                .email("someemail@something.com").name("SomeName").id(1).body("SomeBody").build(),
            Comments.builder().postId(postId)
                .email("someOtheremail@something.com").name("SomeOtherName").id(1).body("SomeOtherBody").build());
    }
}
